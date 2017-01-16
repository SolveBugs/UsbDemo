/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.app;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.wei.usb_demo.common.module.ModBase;
import com.example.wei.usb_demo.common.utils.StringPool;
import com.example.wei.usb_demo.user.UserMod;
import com.example.wei.usb_demo.user.db.bean.User;

import java.util.ArrayList;


/**
 * Created by ygc on 14-10-19.
 */
public class AppContext extends Application {
    private static final String TAG = "AppContext";
    private ArrayList<ModBase> mods = new ArrayList<ModBase>();
    private User activeUser = null;
    private static String USER_AGENT = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        init();
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

}
