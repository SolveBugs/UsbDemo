package com.example.wei.usb_demo.data;

import android.content.Context;

import com.example.wei.usb_demo.common.module.ModBase;

/**
 * Created by Wei on 2017/2/10.
 */

public class DataMod extends ModBase {

    private static final String TAG = "TAG_DataMod";

    public DataMod(Context context, String name, int dbVer) {
        super(context, name, dbVer);
    }

    public DataMod(String name, int dbVer) {
        super(name, dbVer);
    }
}
