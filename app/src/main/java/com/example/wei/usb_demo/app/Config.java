/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.app;

import android.content.Context;

import com.example.wei.usb_demo.common.config.AppConfig;
import com.example.wei.usb_demo.common.utils.Utils;


/**
 * Created by ygc on 14-10-22.
 */
public class Config {
    private static boolean developeMode = false;
    private static Context context;

    public static boolean isDevelopeMode() {
        AppConfig appConfig = AppConfig.getInstance(context);

        if (developeMode) {
            return developeMode;
        }
        return appConfig.isDevelopeMode();
        //return DevelopeMode;
    }

    public static void init(Context context) {
        Config.context = context;
        String serverMode = Utils.getMetaValue(context, "SERVER_MODE");
        if (serverMode.equalsIgnoreCase("release")) {
            developeMode = false;
        }
    }

}
