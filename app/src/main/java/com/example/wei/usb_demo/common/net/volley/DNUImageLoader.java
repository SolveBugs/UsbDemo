package com.example.wei.usb_demo.common.net.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.example.wei.usb_demo.common.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * Created by wdd on 2014/12/15 0015.
 */
public class DNUImageLoader extends ImageLoader {

    public static final String TAG = "DNUImageLoader";
    public static final boolean DEBUG = true;
    private Context context;

    public DNUImageLoader(RequestQueue queue, Context context) {
        super(queue, DRUImageCache.getInstance(context));
        this.context = context;
    }

    public void clearCache(String url) {
        DRUImageCache.getInstance(context).clearUrlImage(url);
    }
}

class DRUImageCache implements ImageLoader.ImageCache {

    private static DRUImageCache lruImageCache; //单例
    private LruCache<String, Bitmap> mHardBitmapCache; //硬引用
    private LinkedHashMap<String, SoftReference<Bitmap>> mSoftBitmapCache; //软引用
//    private StorageCache mStorageCache; // 外部文件缓存

    private DRUImageCache(Context context) {

        /**
         * 初始化软引用
         */
        final int softCacheCapacity = 40;
        mSoftBitmapCache = new LinkedHashMap<String, SoftReference<Bitmap>>(softCacheCapacity, 0.75f, true) {

            @Override
            protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
                if (size() > softCacheCapacity) {
                    if (DNUImageLoader.DEBUG)
                        Log.v(DNUImageLoader.TAG, "Soft Reference limit , purge one");
//                    mStorageCache.putBitmap(eldest.getKey(), eldest.getValue().get());
                    return true;
                }
                return false;
            }
        };

        /**
         * 初始化硬引用
         */
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mHardBitmapCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                if (DNUImageLoader.DEBUG)
                    Log.v(DNUImageLoader.TAG, "hard cache is full , push to soft cache");
                mSoftBitmapCache.put(key, new SoftReference<Bitmap>(oldValue));
            }
        };

        /**
         * 初始化外部文件缓存
         */
//        mStorageCache = new StorageCache(context);


    }

    public static DRUImageCache getInstance(Context context) {
        synchronized (context) {
            if (lruImageCache == null) {
                lruImageCache = new DRUImageCache(context);
            }
            return lruImageCache;
        }

    }

    @Override
    public Bitmap getBitmap(String s) {
        synchronized (mHardBitmapCache) {
            Bitmap bitmap = mHardBitmapCache.get(s);
            if (bitmap != null)
                return bitmap;
        }
        //硬引用缓存区间中读取失败，从软引用缓存区间读取
        synchronized (mSoftBitmapCache) {
            SoftReference<Bitmap> bitmapSoftReference = mSoftBitmapCache.get(s);
            if (bitmapSoftReference != null) {
                Bitmap bitmap2 = bitmapSoftReference.get();
                if (bitmap2 != null) {
                    mHardBitmapCache.put(s, bitmap2);
                    return bitmap2;
                } else {
                    if (DNUImageLoader.DEBUG)
                        Log.v(DNUImageLoader.TAG, "The image has been recycled!");
                    mSoftBitmapCache.remove(s);
                }
            }
        }
        //从软引用读取失败，从外部文件读取
//        synchronized (mStorageCache) {
//            Bitmap bitmap3 = mStorageCache.getBitmap(s);
//            if (bitmap3 != null) {
//                mHardBitmapCache.put(s, bitmap3); //加入到硬引用中
//                return bitmap3;
//            }
//        }
        return mHardBitmapCache.get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        if (bitmap != null) {
            synchronized (mHardBitmapCache) {
                mHardBitmapCache.put(s, bitmap);
            }
            synchronized (mSoftBitmapCache) {
                mSoftBitmapCache.put(s, new SoftReference<Bitmap>(bitmap));
            }
        }
    }

    public void clearUrlImage(String s) {
//        if(ifHas(s)){
//            mHardBitmapCache.remove(s);
//            mSoftBitmapCache.remove(s);
//            mStorageCache.removeBitmap(s);
//        }
        mHardBitmapCache.evictAll();
        mSoftBitmapCache.clear();
//        mStorageCache.removeBitmap(s);
    }


}

class StorageCache {

    private static BitmapFactory.Options mBitmapOptions;

    static {
        mBitmapOptions = new BitmapFactory.Options();
        mBitmapOptions.inPurgeable = true; //避免oom
    }

    private File mCacheDir;

    public StorageCache(Context context) {
        this.mCacheDir = context.getCacheDir();
    }

    /**
     * 清理缓存文件
     */
    public static boolean removeCache(String dirPath) {
        File dir = new File(dirPath);
        if (dir.isFile()) {
            boolean delete = dir.delete();
            Utils.printFileDirState(delete);
            return true;
        }
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return true;
        }
        int dirSize = 0;
        //删除所有缓存
        int all = (int) (1 * files.length + 1);
        //对files 进行排序
        Arrays.sort(files, new FileLastModifiedSort());
        for (int i = 0; i < all; i++) {
            boolean delete = files[i].delete();
            Utils.printFileDirState(delete);
        }
        return true;
    }

    //缓存bitmap到外部存储
    public boolean putBitmap(String key, Bitmap bitmap) {
        File file = new File(mCacheDir, key);
        if (file.exists()) {
            if (DNUImageLoader.DEBUG) Log.v(DNUImageLoader.TAG, "Already exist!");
            return true;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mCacheDir.getAbsolutePath() + File.separator + key);
            boolean saved = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            if (saved) {
                return true;
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public Bitmap getBitmap(String key) {
        File bitmapFile = new File(mCacheDir, key);
        if (bitmapFile != null && bitmapFile.exists()) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(bitmapFile), null, mBitmapOptions);
                if (bitmap != null) {
                    return bitmap;
                }
            } catch (FileNotFoundException e) {
            }
        }
        return null;
    }

    public void removeBitmap(String key) {
        File file = new File(mCacheDir, key);
        if (file.exists()) {
            boolean delete = file.delete();
            Utils.printFileDirState(delete);
        }
    }

    /**
     * 根据文件最后修改时间进行排序
     */
    private static class FileLastModifiedSort implements Comparator<File>, Serializable {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() > rhs.lastModified()) {
                return 1;
            } else if (lhs.lastModified() == rhs.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}