package com.example.wei.usb_demo.main;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


import com.example.wei.usb_demo.common.module.ModBase;
import com.example.wei.usb_demo.common.module.ModuleNames;
import com.example.wei.usb_demo.common.module.RouterBase;
import com.example.wei.usb_demo.common.module.UriMatcherInfo;
import com.example.wei.usb_demo.main.router.MainRouter;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ygc on 14-10-23.
 */
public class MainMod extends ModBase {
    private final static String TAG = "MainMod";
    private final static String PUSH_ACTION_HOME = "HOME";

    private final static int DB_VER = 1;
    private static MainMod sSingleton = new MainMod();

    // for provider

    private MainMod() {
        super(ModuleNames.Main, DB_VER);
    }

    public static MainMod getInstance() {
        return sSingleton;
    }


    @Override
    public RouterBase getRouter(Context context) {
        return MainRouter.getInstance(context);
    }

    @Override
    public ArrayList<UriMatcherInfo> getUriMatchers() {
        ArrayList<UriMatcherInfo> rets = super.getUriMatchers();

        return rets;
    }

    @Override
    public String getDBTableName(int code) {
        switch (code) {

        }
        return super.getDBTableName(code);
    }

    @Override
    public boolean onDatabaseCreate(SQLiteDatabase db) {
        return true;
    }

    @Override
    public boolean onDatabaseUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        return false;
    }

    @Override
    public boolean onDoWorker(int eventId, String userSn, Bundle param) {
        return false;
    }

    @Override
    public boolean onDoSync(int eventId, String userSn, boolean onlyModify, boolean force) {
        return false;
    }

    @Override
    public boolean onReceiveMessage(Context context, String modname, JSONObject jsonMessage) {
        return false;
    }
}
