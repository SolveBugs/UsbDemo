/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.app;

import android.app.Application;

import com.example.wei.usb_demo.common.module.ModBase;
import com.example.wei.usb_demo.user.UserMod;

import java.util.ArrayList;


/**
 * Created by ygc on 14-10-19.
 */
public class AppContext extends Application {
    private static final String TAG = "AppContext";
    private ArrayList<ModBase> mods = new ArrayList<ModBase>();


    @Override
    public void onCreate() {
        super.onCreate();
        init();

    }

    private void init() {

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

}
