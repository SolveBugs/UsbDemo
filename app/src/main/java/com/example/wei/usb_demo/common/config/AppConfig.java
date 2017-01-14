package com.example.wei.usb_demo.common.config;

import android.content.Context;

/**
 * Created by zhenqiang on 2017/1/12.
 */

public class AppConfig {
    public final static int BASE_DB_V = 0;
    /*Add guidance flag key. End */
    private Context context;
    private SharePreferenceManager shareManager;
    private static AppConfig singleton = null;

    private static final String DEVELOPE_MODE = "develope_mode";

    public AppConfig(Context context) {
        this.context = context;
        shareManager = SharePreferenceManager.newInstance(context);

    }

    public static AppConfig getInstance(Context context) {
        synchronized (AppConfig.class) {
            if (singleton == null) {
                singleton = new AppConfig(context.getApplicationContext());
            }
        }
        return singleton;
    }

    public void setDevelopeMode(boolean flag) {
        shareManager.setValue(DEVELOPE_MODE, flag);
    }

    public boolean isDevelopeMode() {
        return shareManager.getBooeanValue(DEVELOPE_MODE);
    }
}
