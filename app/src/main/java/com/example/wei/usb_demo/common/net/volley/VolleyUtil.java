package com.example.wei.usb_demo.common.net.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by hoyouly on 15/8/6.
 */
public class VolleyUtil {
    private static VolleyUtil mInstance;
    private RequestQueue mRequestQueue;
    private Context context;
    private DNUImageLoader mImageLoader;

    private VolleyUtil(Context context) {
        this.context = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new DNUImageLoader(mRequestQueue, context);
    }

    public static synchronized VolleyUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyUtil(context.getApplicationContext());
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext()是关键, 它会避免
            // Activity或者BroadcastReceiver带来的缺点.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public DNUImageLoader getImageLoader() {
        return mImageLoader;
    }

    public <T> void startRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
        //如果每次都调用start方法，就会出现 这个异常 com.android.volley.NoConnection error, java.io.InterruptedIOException
        //原因可以参考blog http://blog.csdn.net/lonewolf521125/article/details/47449069
//        getRequestQueue().start();
    }


}
