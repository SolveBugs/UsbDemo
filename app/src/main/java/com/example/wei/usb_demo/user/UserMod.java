/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.user;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.wei.usb_demo.common.module.ModBase;
import com.example.wei.usb_demo.common.module.ModuleID;
import com.example.wei.usb_demo.common.module.ModuleNames;
import com.example.wei.usb_demo.common.module.UriMatcherInfo;
import com.example.wei.usb_demo.user.db.Authorities;
import com.example.wei.usb_demo.user.db.UserDBM;
import com.example.wei.usb_demo.user.db.bean.User;
import com.example.wei.usb_demo.user.db.bean.UserInfo;

import java.util.ArrayList;

/**
 * Created by ygc on 14-10-19.
 */
public class UserMod extends ModBase {
    private final static String TAG = "UserMod";
    private final static String ACTION_KICK = "KICK";

    private final static int DB_VER = 1;
    // for provider
    private final static int CODE_USERS = ModuleID.User;
    private final static int CODE_USER_INFO = CODE_USERS + 1;
    private static UserMod sSingleton;
    private UserDBM userDBM;

    private UserMod(Context context) {
        super(context, ModuleNames.User, DB_VER);
        userDBM = UserDBM.getInstance(getContext());

    }

    public static UserMod getInstance(Context context) {
        if (sSingleton == null) {
            sSingleton = new UserMod(context.getApplicationContext());
        }
        return sSingleton;
    }

    @Override
    public ArrayList<UriMatcherInfo> getUriMatchers() {
        ArrayList<UriMatcherInfo> rets = super.getUriMatchers();
        rets.add(new UriMatcherInfo(Authorities.Users.PATH, CODE_USERS));
        rets.add(new UriMatcherInfo(Authorities.UserInfo.PATH, CODE_USER_INFO));
        return rets;
    }

    @Override
    public String getDBTableName(int code) {
        switch (code) {
            case CODE_USERS:
                return User.TABLE;
            case CODE_USER_INFO:
                return UserInfo.TABLE;
        }
        return super.getDBTableName(code);
    }

    @Override
    public boolean onDatabaseCreate(SQLiteDatabase db) {
        try {
            db.execSQL(User.getCreateSql());
            Log.i(TAG, "onDatabaseCreate: careate table User");
            db.execSQL(UserInfo.getCreateSql());
            Log.i(TAG, "onDatabaseCreate: carete tabe UserInfo");
            return true;
        } catch (SQLException e) {
        }
        return false;
    }

    @Override
    public boolean onDatabaseUpgrade(SQLiteDatabase db, int oldVer, int newVer) {

        return false;
    }


    @Override
    public int getDatabaseVersion() {
        return DB_VER;
    }


}