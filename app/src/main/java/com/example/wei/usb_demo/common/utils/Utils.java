package com.example.wei.usb_demo.common.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wei.usb_demo.activity.base.AppManager;
import com.example.wei.usb_demo.app.AppContext;
import com.example.wei.usb_demo.common.net.BaseURLs;
import com.example.wei.usb_demo.user.db.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dnurse3 on 14/10/27.
 */
public class Utils {

    public static final String FROM = "FROM";

    private static long lastClickTime;
    private final static int SPACE_TIME = 400;
    public final static String YEAE = "年";
    public final static String MONTH = "月";
    public final static String DAY = "日";
    public final static String ZERO = "0";
    public final static String KG = "kg";
    public final static String G = "g";
    public final static String CM = "cm";
    public final static String AGE = "岁";
    public final static String DEGREE = "°";
    public final static String LIA = "两";
    public final static String MMOLL = "mmol/L";
    public final static String KJ = "KJ";
    public static final String URL_ = "url";

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

    public synchronized static boolean isDoubleClick(long time) {
        long currentTime = System.currentTimeMillis();
        boolean isClick2;
        if (currentTime - lastClickTime > time) {
            lastClickTime = currentTime;
            isClick2 = false;
        } else {
            isClick2 = true;
        }
        return isClick2;
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
            if (mConnectivityManager != null) {
                NetworkInfo mNetworkInfo = mConnectivityManager
                        .getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable() && mNetworkInfo.isConnectedOrConnecting();
                }
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
        }

        return value;
    }

    public static String getChannel(Context mContext) {
        ApplicationInfo appInfo;
        try {
            appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            String path = String.valueOf(appInfo.metaData.get("UMENG_CHANNEL"));
            return path;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return "";
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
        }

        return value;
    }


    public static boolean isBlackScreen(Context context) {
        KeyguardManager km = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }


    //==============
    //==============

    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                if (rest == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                } else {
                    result = false;
                }
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    public static String getDeviceInfo(Context context) {
        try {
            JSONObject json = new JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = null;
            if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                device_id = tm.getDeviceId();
            }
            String mac = null;
            FileReader fstream = null;
            try {
                fstream = new FileReader("/sys/class/net/wlan0/address");
            } catch (FileNotFoundException e) {
                fstream = new FileReader("/sys/class/net/eth0/address");
            }
            BufferedReader in = null;
            if (fstream != null) {
                try {
                    in = new BufferedReader(fstream, 1024);
                    mac = in.readLine();
                } catch (IOException e) {
                } finally {
                    if (fstream != null) {
                        try {
                            fstream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            json.put("mac", mac);
            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }
            if (TextUtils.isEmpty(device_id)) {
                device_id = Settings.Secure.getString(context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
            json.put("device_id", device_id);
            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //==============
    //==============

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
        s = s.replaceAll("-D1-", "<");
        s = s.replaceAll("-D2-", ">");

        String s1 = "<body style=\"color:#333333;line-height:1.5em\">";
        String s2 = "</body>";
        s = s1 + s + s2;
        return s;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }


    private static OnClickListner onClickListner;

    public static void setOnClickListner(OnClickListner onClickListner) {
        Utils.onClickListner = onClickListner;
    }

    public interface OnClickListner {
        void rightBtClick();

        void leftBtClick();
    }


    public static String getSDCardPath() {
        File sdcardDir = null;
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
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


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (Utils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (Utils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }


    public static String highlighted(String content, String highLighContent) {

        if (TextUtils.isEmpty(content)) {
            return content;
        }

        if (content.contains("<font color=#26b0fc>")) {
            content = content.replaceAll("<font color=#26b0fc>", "");
            content = content.replaceAll("</font>", "");
        }

        if (TextUtils.isEmpty(highLighContent) ||
                !content.contains(highLighContent)) {
            return content;
        }

        int index = content.indexOf(highLighContent);
        int len = highLighContent.length();
        StringBuilder sb = new StringBuilder();
        sb.append(content.substring(0, index)).
                append("<font color=#26b0fc>").
                append(content.substring(index, index + len)).
                append("</font>").
                append(content.substring(index + len, content.length()));
        return sb.toString();
    }


    /**
     * 版本 >4.4设置 一个顶部的margin
     *
     * @param context
     * @param view
     */
    public static void setViewMargin(Context context, View view) {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//            lp.setMargins(0, (int) context.getResources().getDimension(R.dimen.px_to_dip_140), 0, 0);
//            view.setLayoutParams(lp);
//        }

    }

    public static void setViewMargin(Context context, View view, int resId) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, (int) context.getResources().getDimension(resId), 0, 0);
            view.setLayoutParams(lp);
        }
    }


    public static int parseStringtoInt(String str) {
        int value = 0;
        if (StringUtils.isEmpty(str)) return value;
        try {
            value = Integer.parseInt(str);
        } catch (Exception e) {
            value = 0;
        }
        return value;
    }

    public static float parseStringtoFloat(String str) {
        float value = 0f;
        if (StringUtils.isEmpty(str)) return value;
        try {
            value = Float.valueOf(str);
        } catch (Exception e) {
            value = 0;
        }
        return value;
    }


    /**
     * @param necessaryField necessaryField就是 equals方法中涉及到的每一个域的集合，并且该类equals方法通过递归地调用equals的方式来比较这个域
     * @return 返回这些字符串递归的调用hashCode的和。
     */
    public static int getHashCode(Object value, String... necessaryField) {
        int result = 17;
        if (value != null) {
            if (value instanceof Integer) {
                result = (Integer) value;
            } else {
                result = 31 * result + value.hashCode();
            }
        }
        for (int i = 0; i < necessaryField.length; i++) {
            result = 31 * result + (necessaryField[i] == null ? 0 : necessaryField[i].hashCode());
        }
        return result;
    }

    /**
     * @param value          已经算到的一部分hashcode值
     * @param necessaryField 如果该域是long类型，则计算(int)(f^(f>>>32));
     * @return
     */
    public static int getHashCode(Object value, long... necessaryField) {
        int result = 17;
        if (value != null) {
            if (value instanceof Integer) {
                result = (Integer) value;
            } else {
                result = 31 * result + value.hashCode();
            }
        }
        for (int i = 0; i < necessaryField.length; i++) {
            result = 31 * result + (int) (necessaryField[0] ^ (necessaryField[0] >>> 32));
        }
        return result;
    }


    public static String printMap(Map map) {
        if (map == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            sb.append(entry.getKey() + ":" + entry.getValue() + ", ");
        }
        return sb.toString();
    }

    /**
     * 判断输入的是否是表情
     * 在UTF-8中一个汉字的长度是1，占三个字节，一个表情的长度是2，占四个字节
     *
     * @param ch
     * @return
     */
    public static boolean isEmoji(char ch) {
        return !((ch == 0x0)   //
                || (ch == 0x9) //
                || (ch == 0xA) //
                || (ch == 0xD) //
                || ((ch >= 0x20) && (ch <= 0xD7FF)) //
                || ((ch >= 0xE000) && (ch <= 0xFFFD))); //
//                || ((ch >= 0x10000) && (ch <= 0x10FFFF)));
    }


    /**
     * 得到字符串所占字节的长度
     *
     * @param str
     * @return
     */
    public static int getBytes(String str) {
        int len = 0;
        try {
            len = str.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            len = str.length();
        }
        return len;

    }


    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 判断是否是中文
     *
     * @param c
     * @return
     */
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS//
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS //
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A//
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B //
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION //
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS//
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    /**
     * 根据传递的字节数，得到字符串，不能有乱码，
     *
     * @param str
     * @param bytesLength
     * @return
     */
    public static String subStr(String str, int bytesLength) {
        if (str == null)
            return "";
        else {
            int orignalBytesLength = getBytes(str);
            if (orignalBytesLength < bytesLength) {
                return str;
            }
            //说明要截取的长度比原始的要短
            int tempSubLength = 0;//临时变量
            int index = 0;
            char c;
            while (bytesLength > tempSubLength && index < str.length()) {
                c = str.charAt(index);
                if (isEmoji(c)) {//表情占四个字节，长度是2
                    tempSubLength += 4;
                    index++;
                } else if (isChinese(c)) {
                    tempSubLength += 3;//中文字符占三个字节，长度是1
                } else {//其他占一个字节，长度是1
                    tempSubLength++;
                }
                index++;
            }
            return str.substring(0, index);
        }
    }


    public static String formatMonthToInt(String month) {
        String monthStr = null;
        if (month.equals("Jan")) {
            monthStr = "1";
        } else if (month.equals("Feb")) {
            monthStr = "2";
        } else if (month.equals("Mar")) {
            monthStr = "3";
        } else if (month.equals("Apr")) {
            monthStr = "4";
        } else if (month.equals("May")) {
            monthStr = "5";
        } else if (month.equals("Jun")) {
            monthStr = "6";
        } else if (month.equals("Jul")) {
            monthStr = "7";
        } else if (month.equals("Aug")) {
            monthStr = "8";
        } else if (month.equals("Sep")) {
            monthStr = "9";
        } else if (month.equals("Oct")) {
            monthStr = "10";
        } else if (month.equals("Nov")) {
            monthStr = "11";
        } else if (month.equals("Dec")) {
            monthStr = "12";

        }
        return monthStr;
    }

    public static List<Integer> getYearMonthDay(String str, Context context) {
        Calendar c = Calendar.getInstance();
        List<Integer> nums = new ArrayList<Integer>();
        if (!Utils.isNotChinese(context)) {
            int curYear = c.get(Calendar.YEAR);
            int curMonth = c.get(Calendar.MONTH) + 1;// 通过Calendar算出的月数要+1
            int curDay = c.get(Calendar.DATE);//

            String[] tempStr = null;
            str = str.split(" ")[0];
            tempStr = str.split(MONTH);
            if (tempStr != null && tempStr.length > 1) {
                int selectMonth = parseStringtoInt(tempStr[0]);
                String dayStr = tempStr[1];
                tempStr = dayStr.split(DAY);
                int selectDay = parseStringtoInt(tempStr[0]);

                if (selectMonth > curMonth) {
                    nums.add(curYear - 1);
                } else {
                    if (selectMonth == curMonth && selectDay > curDay) {
                        nums.add(curYear - 1);
                    } else {
                        nums.add(curYear);
                    }
                }
                nums.add(selectMonth);
                nums.add(selectDay);
            }
        } else {

            int curYear = c.get(Calendar.YEAR);
            int curMonth = c.get(Calendar.MONTH) + 1;// 通过Calendar算出的月数要+1
            int curDay = c.get(Calendar.DATE);//

            String[] tempStr = null;
            str = str.split(" ")[0];
            tempStr = str.split(MONTH);
            if (tempStr != null && tempStr.length > 1) {
                int selectMonth = parseStringtoInt(formatMonthToInt(tempStr[0]));
                String dayStr = tempStr[1];
                tempStr = dayStr.split(DAY);
                int selectDay = parseStringtoInt(tempStr[0]);

                if (selectMonth > curMonth) {
                    nums.add(curYear - 1);
                } else {
                    if (selectMonth == curMonth && selectDay > curDay) {
                        nums.add(curYear - 1);
                    } else {
                        nums.add(curYear);
                    }
                }
                nums.add(selectMonth);
                nums.add(selectDay);
            }
        }
        return nums;
    }


    /**
     * @param year
     * @param month
     * @return
     */
    public static int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }


    public static List<String> getMinuteNum() {
        List<String> minutes = new ArrayList<String>();

        for (int i = 0; i < 10; i += 5) {
            minutes.add(ZERO + String.valueOf(i));
        }
        for (int i = 10; i < 60; i += 5) {
            minutes.add(String.valueOf(i));

        }
        return minutes;
    }

    public static List<String> getMinuteNum(int space) {
        List<String> minutes = new ArrayList<String>();

        for (int i = 0; i < 10; i += space) {
            minutes.add(ZERO + String.valueOf(i));
        }
        for (int i = 10; i < 60; i += space) {
            minutes.add(String.valueOf(i));

        }
        return minutes;
    }

    public static List<String> getHourNum() {
        return getNums().subList(0, 24);
    }

    public static List<String> getNums() {
        List<String> nums = new ArrayList<String>();

        for (int i = 0; i < 10; i++) {
            nums.add(ZERO + String.valueOf(i));
        }
        for (int i = 10; i < 60; i++) {
            nums.add(String.valueOf(i));
        }
        return nums;
    }

    public static List<String> getYearNums() {
        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        List<String> nums = new ArrayList<String>();
        for (int i = 1900; i < curYear; i++) {
            nums.add(String.valueOf(i));
        }
        return nums;
    }

    public static List<String> getMonthNum() {
        return getNums().subList(1, 13);
    }

    public static List<String> getDayNum(int year, int mounths) {
        return getNums().subList(1, getDay(year, mounths));
    }

    public static String mapToJSONStr(Map<String, String> param) {
        if (param != null && param.size() > 0) {
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> entry : param.entrySet()) {
                try {
                    String value = entry.getValue();
                    try {
                        int num = Integer.parseInt(value);
                        json.put(entry.getKey(), num);
                    } catch (Exception e) {
                        json.put(entry.getKey(), value);
                    }
                } catch (JSONException e) {
                }
            }
            return json.toString();
        }
        return "";
    }

    public static Map<String, String> stringToMap(String json) {
        if (StringUtils.isEmpty(json)) return null;
        try {
            Map<String, String> params = new HashMap<String, String>();

            JSONObject object = new JSONObject(json);
            if (object != null) {
                String key = null;
                Iterator<String> iterator = object.keys();
                while (iterator.hasNext()) {
                    key = iterator.next();
                    params.put(key, object.optString(key));
                }
            }
            return params;
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static void writeToSd(String str) {

        AppManager appManager = AppManager.getAppManager();
        if (appManager == null) {
            return;
        }
        final Activity activity = appManager.currentActivity();
        if (activity == null) {
            return;
        }

        if (!isPermissionsGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (System.currentTimeMillis() - lastClickTime < 2000) {
                return;
            }
            android.util.Log.e("Utils", "无SDCARD权限");
            startPermissionSetting(activity);
            lastClickTime = System.currentTimeMillis();
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionRequest.SDCARD);
            return;
        }

        if (TextUtils.isEmpty(str)) {
            return;
        }
        synchronized (str) {
            String sdFileName = getSDCardPath() + "/log.txt";
            writeToSd(str, sdFileName);
        }
    }

    public static void writeToSd(String str, String sdFileName) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        File SDFile = new File(sdFileName);
        BufferedWriter writer = null;
        try {
            StringBuilder sbContent = new StringBuilder();
            sbContent.append("  [").append(new Date().toLocaleString()).append("." + (System.currentTimeMillis() % 1000) + "]  ");
            sbContent.append(str);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(SDFile, true), StringUtils.ENCODE);
            writer = new BufferedWriter(outputStreamWriter);
            writer.write(sbContent.toString() + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("aaaaaa", str);
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
     * 给界面添加透明度
     *
     * @param activity
     * @param bgAlpha
     */
    public static void backgroundAlpha(Activity activity, float bgAlpha) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }


    public static void showSoftInput(final View view) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() { //让软键盘延时弹出，以更好的加载Activity
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(view, 0);
            }

        }, 300);
    }

    public static boolean isNotChinese(Context context) {
        return !context.getResources().getConfiguration().locale.getCountry().equals("CN") &&
                !context.getResources().getConfiguration().locale.getCountry().equals("TW");
    }

    public static long getbirth(int age) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -age);

        return calendar.getTime().getTime() / 1000;
    }


    public static void printFileDirState(boolean flag) {
        if (!flag) {
            Log.d("Files_or_Dirs", "创建或者删除失败");
        }
    }

    public static void startPermissionSetting(Context context) {
//        Intent intent = new Intent();
//        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
//        context.startActivity(intent);
//        UIHelper.ToastMessage(context, R.string.permission_denial);
    }

    public static boolean isPermissionsGranted(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            //版本23以上 并且非授权状态
            return false;
        }
        return true;
    }

    //是否能正确获取IMEI
    public static boolean isIMEIPermissionsGranted(Context context, String permission) {
        if (!Utils.isPermissionsGranted(context, permission)) {
            //无权限
            return false;
        }

        //有权限
//        String imei = StringUtils.getIMEI(context);
//        if (TextUtils.isEmpty(imei) || imei.equals("000000000000000")) {
//            //没能正确获取IMEI
//            return false;
//        }

        return true;
    }


    public static void callPhone(Activity activity, String phoneNum) {
        if (isPermissionsGranted(activity, Manifest.permission.CALL_PHONE)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNum));
            activity.startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, PermissionRequest.CALL_PHONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean canWrite(Context activity) {
        return Settings.System.canWrite(activity);// 检查是否被授予了WRITE_SETTINGS权限
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean canDrawOverlays(Context activity) {
        return Settings.canDrawOverlays(activity);//检查是否被授予了SYSTEM_ALERT_WINDOW权限
    }


    public static void requestWriteSettings(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23 && !canWrite(activity)) {
            startWriteSettings(activity);
        }
    }

    private static void startWriteSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, PermissionRequest.WRITE_SETTINGS);
    }

    public static void requestAlertWindowPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23 && !canDrawOverlays(activity)) {
            startAlertWindowPermission(activity);
        }
    }

    private static void startAlertWindowPermission(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivityForResult(intent, PermissionRequest.ALERT_WINDOW);
    }


    private static float getBmiValue(int height, int weight) {
        float bmiValue = weight * 10000.0f / height / height;
        return bmiValue;
    }


    public static int getCurrentWeekday() {
        Calendar calendar = new GregorianCalendar();
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        if (weekday < 0) {
            weekday = 0;
        }
        if (weekday - Calendar.MONDAY < 0) {
            return 6;
        }
        return weekday - Calendar.MONDAY;
    }

    public static int getCurrentMonthDay() {
        Calendar calendar = new GregorianCalendar();
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        return monthDay;

    }

    //获得当前的小时
    public static int getCurrentHour() {
        Calendar calendar = new GregorianCalendar();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        return currentHour;
    }

    //获得当前的分钟
    public static int getCurrentMinite() {
        Calendar calendar = new GregorianCalendar();
        int currentMinute = calendar.get(Calendar.MINUTE);
        return currentMinute;
    }

    public static HashMap<String, String> rebuildMap(Map<String, String> param, User user) {
        HashMap<String, String> map = new HashMap<String, String>();
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        map.put("ctime", time);
        String token = "";
        if (user != null) {
            token = user.getAccessToken();
        }
        map.put("token", "27784477c686d0dbde135ed0629b4ddf");
        // 把param 中的转换成json 字符串
        String result = Utils.mapToJSONStr(param);
        if (!StringUtils.isNull(result)) {
            map.put("cdata", result);
        }
        map.put("csign", StringUtils.MD5(StringUtils.MD5(time + result + token) + BaseURLs.KEY));
        return map;
    }

}
