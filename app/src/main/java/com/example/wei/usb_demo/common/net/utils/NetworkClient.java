package com.example.wei.usb_demo.common.net.utils;

import android.content.Context;

/**
 * Created by hoyouly on 15/8/7.
 */
public class NetworkClient {

    public static AbstractNetWorkClient getClient(Context context) {
        return VolleyClient.getInstance(context);
    }
}
