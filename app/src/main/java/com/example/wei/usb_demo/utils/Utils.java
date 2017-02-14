package com.example.wei.usb_demo.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.wei.usb_demo.app.AppContext;
import com.example.wei.usb_demo.app.CustomConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dnurse3 on 14/10/27.
 */
public class Utils {

    private static final String TAG = "Utils";

    public static String getAvailMemoryFat(Context context) {
        return Formatter.formatFileSize(context, getAvailMemory(context));
    }

    public static long getAvailMemory(Context context) {// 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        return mi.availMem;
    }

    public static String getTotalMemoryFat(Context context) {
        return Formatter.formatFileSize(context, getTotalMemory(context));
    }

    public static long getTotalMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.parseInt(arrayOfString[1]) * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        return initial_memory;
    }

    public static long getAvailaleSdSize() {
        File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;

    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    public static float dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxValue / scale + 0.5f);
    }

    public static float px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale + 0.5f);
    }

    public static float sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnectedOrConnecting();
            }
        }
        return false;
    }

    public static boolean isWifi(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            }
        }
        return false;
    }

    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable()
                        && mMobileNetworkInfo.isConnectedOrConnecting();
            }
        }
        return false;
    }

    //保留到小数点后1位
    public static String round(float value) {
        return String.format(Locale.US, "%.1f", value);
    }

    public static String getMetaValue(Context context, String key) {
        String value = null;
        if (context == null) {
            return value;
        }

        ApplicationInfo appInfo;
        try {

            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            value = String.valueOf(appInfo.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static String getVersion(Context context) {
        String value = null;
        if (context == null) {
            return value;
        }

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            value = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static int getVersionCode(Context context) {
        int value = 0;
        if (context == null) {
            return value;
        }

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            value = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static boolean isBlackScreen(Context context) {
        KeyguardManager km = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    public static String getLoginSet(Context context) {
        String osVer = "Android:" + android.os.Build.VERSION.RELEASE;
        String brand = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        String softver = "2.0";
        try {
            softver = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject object = new JSONObject();
        try {
            object.put("osver", osVer);
            object.put("brand", brand);
            object.put("model", model);
            object.put("softver", softver);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String getChannel(Context mContext) {
        ApplicationInfo appInfo;
        try {
            appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            String path = String.valueOf(appInfo.metaData.get("UMENG_CHANNEL"));
            return path;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static File getImageFileDir(Context context) {
        File path = context.getCacheDir();
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (context.getExternalCacheDir() != null) {
                path = context.getExternalCacheDir();
            }
        }

        return path;
    }


    private static Pattern pattern_pic1 = Pattern.compile("\\{img\\}");
    private static Pattern pattern_pic2 = Pattern.compile("(\\|)([0-9]*)(\\{/img\\})");
    private static Pattern pattern_d1 = Pattern.compile("-D1-");
    private static Pattern pattern_d2 = Pattern.compile("-D2-");

    public static String formatWebContent(String src) {
        StringBuffer tempBuffer = new StringBuffer();
        Matcher m1 = pattern_pic1.matcher(src);
        while (m1.find()) {
            m1.appendReplacement(tempBuffer, "<img src=\"");
        }
        m1.appendTail(tempBuffer);
        String temp = tempBuffer.toString();

        Matcher m2 = pattern_pic2.matcher(temp);
        StringBuffer buffer = new StringBuffer();
        while (m2.find()) {
            m2.appendReplacement(buffer, "&s=0\" width=\"100%\">");
        }
        m2.appendTail(buffer);

        String sd1 = buffer.toString();
        Matcher m4 = pattern_d1.matcher(sd1);
        StringBuffer buf4 = new StringBuffer();
        while (m4.find()) {
            m4.appendReplacement(buf4, "<");
        }
        m4.appendTail(buf4);

        String sd2 = buf4.toString();
        Matcher m5 = pattern_d2.matcher(sd2);
        StringBuffer buf5 = new StringBuffer();
        while (m5.find()) {
            m5.appendReplacement(buf5, ">");
        }
        m5.appendTail(buf5);

        String s = buf5.toString();
        s.replaceAll("-D1-", "<");
        s.replaceAll("-D2-", ">");

        String s1 = "<body style=\"color:#333333;line-height:1.5em\">";
        String s2 = "</body>";
        s = s1 + s + s2;
        return s;
    }


    /**
     * 得到网页中图片的地址
     */
    public static List<String> getImgStr(String htmlStr) {
        if (TextUtils.isEmpty(htmlStr)) return null;
        List<String> pics = new ArrayList<String>();
        String regEx_img = "http://[([\\w']|.|/|\\-)]+.[(jpg)|(bmp)|(gif)|(png)]";// 图片链接地址
        Pattern pattern = java.util.regex.Pattern.compile(regEx_img, java.util.regex.Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlStr);
        while (matcher.find()) {
            pics.add(matcher.group());
        }
        return pics;
    }

    public static List<String> getLocalImgStr(String htmlStr) {
        if (TextUtils.isEmpty(htmlStr)) return null;
        List<String> pics = new ArrayList<String>();
        String regEx_img = "((file:///android_asset/knowledges/images/)|(file:///storage/sdcard0/Dnurse/Images/))[\\w'-]+.[(jpg)|(bmp)|(gif)|(png)]";
        Pattern pattern = java.util.regex.Pattern.compile(regEx_img, java.util.regex.Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(htmlStr);
        while (matcher.find()) {
            pics.add(matcher.group());
        }
        return pics;
    }


    public static String getSDCardPath() {
        File sdcardDir = null;
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
            return sdcardDir.toString();
        } else {
            return null;
        }
    }

    public static long string2Seconds(String time) {
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = bartDateFormat.parse(time);
            return date.getTime() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static <T> boolean equals(Collection<T> a, Collection<T> b) {
        if (a == null) {
            return false;
        }
        if (b == null) {
            return false;
        }
        if (a.isEmpty() && b.isEmpty()) {
            return true;
        }
        if (a.size() != b.size()) {
            return false;
        }
        List<T> alist = new ArrayList<T>(a);
        List<T> blist = new ArrayList<T>(b);
        Collections.sort(alist, new Comparator<T>() {

            public int compare(T o1, T o2) {
                return o1.hashCode() - o2.hashCode();
            }

        });

        Collections.sort(blist, new Comparator<T>() {
            public int compare(T o1, T o2) {
                return o1.hashCode() - o2.hashCode();
            }

        });
        return alist.equals(blist);
    }

    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }

    public static final String DEFAULT_IMEI = "012345678901234";
    public static String IMEI;


    //===========
    public static void setMobileData(Context pContext, boolean pBoolean) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;

            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);

            method.invoke(mConnectivityManager, pBoolean);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static boolean getMobileDataState(Context pContext, Object[] arg) {

        try {

            ConnectivityManager mConnectivityManager = (ConnectivityManager) pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            Class ownerClass = mConnectivityManager.getClass();

            Class[] argsClass = null;
            if (arg != null) {
                argsClass = new Class[1];
                argsClass[0] = arg.getClass();
            }

            Method method = ownerClass.getMethod("getMobileDataEnabled", argsClass);

            Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

            return isOpen;

        } catch (Exception e) {
            return false;
        }
    }


    private static long lastClickTime;
    private final static int SPACE_TIME = 400;

    public static void initLastClickTime() {
        lastClickTime = 0;
    }

    public synchronized static boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        if (currentTime - lastClickTime > SPACE_TIME) {
            lastClickTime = currentTime;
            isClick2 = false;
        } else {
            isClick2 = true;
        }
        return isClick2;
    }


//    public static void insertData(Context context, float testValue, Calendar data) {
//        String sn = ((AppContext) context.getApplicationContext()).getActiveUser().getSn();
//        ModelData testData = new ModelData();
//        testData.setValue(testValue);
//        testData.setUid(sn);
//        testData.setTimePoint(ReminderApi.getCurrentTimePoint(context, sn));
//        testData.setDataFrom(DataFrom.DATA_FROM_DEVICE);
//        testData.setFoodType(DataCommon.getFoodTypeByTimePointHaveExtra(testData.getTimePoint()));
//        testData.setDataTime(data.getTimeInMillis());
//        testData.markModify();
//
//        long id = DataDBM.getInstance(context).insertData(testData, true);
//        if (id > 0) {
//            testData.setId(id);
//            SyncClient.sendSyncEvent(context, DataEvents.SYNC_DATA_UPLOAD, sn, true, false);
//        } else {
//            UIHelper.ToastMessage(context, R.string.data_insert_failed);
//        }
//    }

    public static boolean kfreeServiceStart(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(100);
        Iterator<ActivityManager.RunningServiceInfo> l = services.iterator();
        boolean created = false;
        while (l.hasNext()) {
            ActivityManager.RunningServiceInfo si = l.next();
            if (si.service.getShortClassName().contains("KfreeService")) {
                created = true;
                break;
            }
        }
        return created;
    }


    public static void compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;// 个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.PNG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            if (options > 0) {
                bmp.compress(Bitmap.CompressFormat.PNG, options, baos);
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 限制ListView的最大高度
     *
     * @param appContext
     * @param mListView
     * @param limitHeight
     */
    public static void setListViewHeightWithLimit(AppContext appContext, ListView mListView, int limitHeight) {
        ListAdapter listAdapter = mListView.getAdapter();
        if (listAdapter == null) {
            return;
        } else {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
                View listItem = listAdapter.getView(i, null, mListView);
                listItem.measure(0, 0); // 计算子项View 的宽高
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }

            if (Utils.px2dip(appContext, totalHeight) >= limitHeight) {
                ViewGroup.LayoutParams params = mListView.getLayoutParams();
                params.height = (int) Utils.dip2px(appContext, limitHeight);
                mListView.setLayoutParams(params);
            }
        }
    }

    /**
     * 限制ListView的最大高度
     *
     * @param appContext
     * @param mListView
     * @param limitHeight
     */
    public static void setListViewHeightWithLimit(AppContext appContext, ListView mListView, int limitHeight, int headAndFoot) {
        ListAdapter listAdapter = mListView.getAdapter();
        if (listAdapter == null) {
            return;
        } else {
            int totalHeight = 0;
            for (int i = 1; i < listAdapter.getCount() - 1; i++) { // listAdapter.getCount()返回数据项的数目
                View listItem = listAdapter.getView(i, null, mListView);
                listItem.measure(0, 0); // 计算子项View 的宽高
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }

            totalHeight += headAndFoot;

            if (Utils.px2dip(appContext, totalHeight) >= limitHeight) {
                ViewGroup.LayoutParams params = mListView.getLayoutParams();
                params.height = (int) Utils.dip2px(appContext, limitHeight);
                mListView.setLayoutParams(params);
            }
        }
    }


    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {

                return appProcess.processName + "   pid: " + pid;
            }
        }
        return null;
    }

    public static String mapToJSONStr(Map<String, String> param) {
        if (param != null && param.size() > 0) {
            JSONObject json = new JSONObject();
            Set set = param.keySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                try {
                    String temp = param.get(key);
                    try {
                        int num = Integer.parseInt(temp);
                        json.put(key, num);
                    } catch (Exception e) {
                        json.put(key, temp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return json.toString();
        }
        return "";
    }

    public static boolean isNull(String input) {
        if (!isEmpty(input) && !NULL.equals(input)) {
            return false;
        }
        return true;
    }

    public static final String NULL = "null";

    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static void acquireWakeLock(long milltime, Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock m_wakeLockObj = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "TAG");


        m_wakeLockObj.acquire(milltime);
    }


    public static String getEventTag(String tag, int i) {
        return tag + i;
    }

    public static String getEventTagA(int i) {
        return getEventTag("a", i);
    }

    // adb pull /sdcard/dnurse2.db /Users/i/Desktop/
    public static void copyDB(Context context) {
        File databasePath = context.getDatabasePath(CustomConfig.DB_NAME);
        String dbName = CustomConfig.DB_NAME;
        String sd_db_path = Utils.getSDCardPath() + "/" + dbName;
        File sd_db_file = new File(sd_db_path);
        if (sd_db_file.exists()) {
            sd_db_file.delete();
        }
        try {
            streamToSD(new FileInputStream(databasePath.getPath()), sd_db_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void streamToSD(InputStream myInput, String path) throws IOException {
        OutputStream myOutput = new FileOutputStream(path);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

}
