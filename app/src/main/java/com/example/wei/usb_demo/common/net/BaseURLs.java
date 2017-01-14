/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.common.net;

import android.text.TextUtils;

import com.example.wei.usb_demo.app.Config;
import com.example.wei.usb_demo.app.CustomConfig;


/**
 * Created by ygc on 14-10-22.
 */
public class BaseURLs {
    public final static String HTTP = "http://";
    public final static String KEY = "cd6b50097a858a9f6375ac48a0e02771";


    public final static String ACT_BASE_URL = HTTP + getActBaseUrl();
    public final static String HOST_URL = HTTP + getHost_3();
    public final static String WEB_HOST = HTTP + getWebHost();
    public final static String HOST_URL_OLD_1 = HTTP + getHost();
    public final static String HOST_URL_OLD_2 = HTTP + getHost_2();

    public final static String getBaseHeadUrl(String userSn) {
        StringBuilder builder = new StringBuilder(User_Head);
        if (TextUtils.isEmpty(userSn)) {
            userSn = "21494d8f35046d42f6bbc7602749f51a";//特定头像 护士
        }
        builder.append("&sn=").append(userSn);
        return builder.toString();
    }

    private static String getActBaseUrl() {
        if (Config.isDevelopeMode()) {
            return CustomConfig.HOST_DEV_ACT;
        } else {
            return CustomConfig.HOST_ACT;
        }
    }

    private static String getHost_3() {
        if (Config.isDevelopeMode()) {
            return CustomConfig.HOST_DEV_3;
        } else {
            return CustomConfig.HOST_3;
        }
    }

    private static String getWebHost() {
        if (Config.isDevelopeMode()) {
            return CustomConfig.WebHostDev;
        } else {
            return CustomConfig.WebHost;
        }
    }

    private static String getHost() {
        if (Config.isDevelopeMode()) {
            return CustomConfig.HOST_DEV;
        } else {
            return CustomConfig.HOST;
        }
    }

    private static String getHost_2() {
        if (Config.isDevelopeMode()) {
            return CustomConfig.HOST_DEV_2;
        } else {
            return CustomConfig.HOST_2;
        }
    }

    private final static String User_Head = HOST_URL_OLD_1 + "user.php?act=getUserPic";


    //TODO 使用新链接  魅族PRO经常加载失败 还未找到根本原因 先使用原接口
    public final static String User_Head_new = HOST_URL + "user/get_avatar/";

    public static String getBaseHeadUrl_new(String userSn) {
        StringBuilder builder = new StringBuilder(User_Head_new);
        builder.append(userSn);
        return builder.toString();
    }
}
