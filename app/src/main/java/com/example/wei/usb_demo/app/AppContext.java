/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.example.wei.usb_demo.common.broatcast.UIBroadcastReceiver;
import com.example.wei.usb_demo.common.module.ModBase;
import com.example.wei.usb_demo.common.utils.StringPool;
import com.example.wei.usb_demo.user.UserMod;
import com.example.wei.usb_demo.user.db.bean.User;
import com.mhealth365.osdk.EcgOpenApiCallback;
import com.mhealth365.osdk.EcgOpenApiHelper;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by ygc on 14-10-19.
 */
public class AppContext extends MultiDexApplication {
    private static final String TAG = "AppContext";
    private ArrayList<ModBase> mods = new ArrayList<ModBase>();
    private User activeUser = null;
    private static String USER_AGENT = null;
    private UIBroadcastReceiver.OnActiveReceive activeReceive;
    private UIBroadcastReceiver broadcastReceiver;
    private static AppContext app;
    public static float dpi = 0;
    public SharedPreferences mSharedPreferences;
    private static Context mAppContext;

    public String thirdPartyId = "631f9e24f18423b17ba9d2578d98cf1a";
    public String appId = "bc0fbcd6c8423ee21b4ed972d86b3a1d";
    public String pkgName = "com.mdm";
    public String UserOrgName = "北京糖护科技";

    public final static String KEY_THIRD_PARTY_ID = "KEY_THIRD_PARTY_ID";
    public final static String KEY_APP_ID = "KEY_APP_ID";
    public final static String KEY_APP_PKG_NAME = "KEY_APP_PKG_NAME";

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        mSharedPreferences = getSharedPreferences("mhealth365", Context.MODE_PRIVATE);
        readValue();
        mAppContext = getApplicationContext();

        WindowManager wm = (WindowManager) mAppContext.getSystemService(Context.WINDOW_SERVICE);
        Display dis = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();

        dis.getMetrics(dm);
        dpi = dm.ydpi;
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        init();

        EcgOpenApiHelper mHelper = EcgOpenApiHelper.getInstance();
        Log.i("App", "--- thirdPartyId:" + thirdPartyId);
        Log.i("App", "--- appId:" + appId);
        Log.i("App", "--- pkgName:" + pkgName);
        try {
            mHelper.initOsdk(mAppContext, thirdPartyId, appId, "", UserOrgName, mOsdkCallback, pkgName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {

        Config.init(this);

        fillMods();

        // call init
        for (ModBase module : mods) {
            module.onAppInit(this);
        }

        // call after init
        for (ModBase module : mods) {
            module.onAfterAppInit(this);
        }

        if (broadcastReceiver == null) {
            broadcastReceiver = new UIBroadcastReceiver();
        }
        registerReceiver(broadcastReceiver, UIBroadcastReceiver.getIntentFilter(this));
    }

    private void fillMods() {
        if (mods.isEmpty()) {
            synchronized (AppContext.class) {
                mods.add(UserMod.getInstance(this));
            }
        }
    }

    public ArrayList<ModBase> getMods() {
        fillMods();
        return mods;
    }

    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        if (info == null) {
            info = new PackageInfo();
        }
        return info;
    }

    public User getActiveUser() {
        if (activeUser == null) {
            User temp = new User();
            temp.setTemp(true);
            temp.setActived(false);
            return temp;
        }
        return activeUser;
    }

    public void setActiveUser(User user) {
        activeUser = user;
    }

    public static String getUSER_AGENT() {
        return USER_AGENT;
    }

    private void setUserAgent() {
        // 格式改为： com.dnurse/3.0.1_50/Android/4.3/R2017/
        try {
            String packageName = getPackageName();
            PackageInfo info = getPackageManager().getPackageInfo(packageName, 0);
            USER_AGENT = packageName + StringPool.PATH_SPLITTER + info.versionName + "_" + info.versionCode + "/Android/" + android.os.Build.VERSION.RELEASE + StringPool.PATH_SPLITTER + android.os.Build.MODEL;
            USER_AGENT += ("/lang=" + getResources().getConfiguration().locale.getCountry());
            Log.i("USER_AGENT", "USER_AGENT = " + USER_AGENT);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setOnActiveReceive(UIBroadcastReceiver.OnActiveReceive activeReceive) {
        this.activeReceive = activeReceive;
        if (this.activeReceive != null && broadcastReceiver != null) {
            broadcastReceiver.setOnActiveReceive(this.activeReceive);
        }
    }

    public static AppContext getApp() {
        return app;
    }

    EcgOpenApiCallback.OsdkCallback displayMessage;

    public void setOsdkCallback(EcgOpenApiCallback.OsdkCallback osdkCallback) {
        displayMessage = osdkCallback;
    }

    public void onTerminate() {
        try {
            app.finishSdk();// 释放sdk所有资源【不可恢复】
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 心电sdk初始化
     */
    public void setValue(String thirdPartyId, String appId, String pkgName) {
        mSharedPreferences.edit().putString(KEY_THIRD_PARTY_ID, thirdPartyId).commit();
        mSharedPreferences.edit().putString(KEY_APP_ID, appId).commit();
        mSharedPreferences.edit().putString(KEY_APP_PKG_NAME, pkgName).commit();
        readValue();
    }

    private void readValue() {
        this.thirdPartyId = mSharedPreferences.getString(KEY_THIRD_PARTY_ID, thirdPartyId);
        this.appId = mSharedPreferences.getString(KEY_APP_ID, appId);
        this.pkgName = mSharedPreferences.getString(KEY_APP_PKG_NAME, getPackageName());
    }

    public void setDefaultValue() {
        setValue(thirdPartyId, appId, getPackageName());
    }

    public static void finishSdk() throws IOException {
        EcgOpenApiHelper mHelper = EcgOpenApiHelper.getInstance();
        mHelper.finishSdk();
    }

    EcgOpenApiCallback.OsdkCallback mOsdkCallback = new EcgOpenApiCallback.OsdkCallback() {

        @Override
        public void deviceSocketLost() {
            if (displayMessage != null)
                displayMessage.deviceSocketLost();
        }

        @Override
        public void deviceSocketConnect() {
            if (displayMessage != null)
                displayMessage.deviceSocketConnect();
        }

        @Override
        public void devicePlugOut() {
            if (displayMessage != null)
                displayMessage.devicePlugOut();
        }

        @Override
        public void devicePlugIn() {
            if (displayMessage != null)
                displayMessage.devicePlugIn();
        }

        @Override
        public void deviceReady(int sample) {
            if (displayMessage != null)
                displayMessage.deviceReady(sample);
        }

        @Override
        public void deviceNotReady(int msg) {
            if (displayMessage != null)
                displayMessage.deviceNotReady(msg);
        }
    };
}
