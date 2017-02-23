/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.user.db;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.wei.usb_demo.app.AppContext;
import com.example.wei.usb_demo.user.db.bean.User;
import com.example.wei.usb_demo.user.db.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ygc on 14-10-22.
 */
public class UserDBM {

    private static final String TAG = "UserDBM";
    private static UserDBM sSingleton = null;
    private Context context;
    private AppContext appContext;
    private String sn;

    private UserDBM(Context context) {
        this.context = context;
        appContext = (AppContext) context.getApplicationContext();
    }

    public static UserDBM getInstance(Context context) {
        synchronized (UserDBM.class) {
            if (sSingleton == null && context != null) {
                sSingleton = new UserDBM(context);
            }
        }
        ;
        return sSingleton;
    }

    public long addUser(User user) {
        if (user != null) {
            if (user.getId() == 0) {
                Uri uri = context.getContentResolver().insert(Authorities.Users.AUTHORITY_URI, user.getValues());
                if (uri != null) {
                    try {
                        return ContentUris.parseId(uri);
                    } catch (Exception e) {
                    }
                }
            }
        }

        return 0;
    }

    public List<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<User>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Authorities.Users.AUTHORITY_URI, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                User user = User.getFromCusor(cursor);
                if (user != null) {
                    users.add(user);
                }
            }
            return users;
        } catch (Exception e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 添加用户信息
     *
     * @param info
     */
    public void addUserInfo(UserInfo info) {
        if (info != null) {
            int tag = info.getTag();
            UserInfo i = getUserInfoByTag(tag);
            if (i != null) {
                updateUserInfo(info);
            } else {
                context.getContentResolver().insert(Authorities.UserInfo.AUTHORITY_URI, info.getContentValues());
            }
        }
    }

    /**
     * 根据tag查询对应的用户信息
     *
     * @param tag
     * @return
     */
    public UserInfo getUserInfoByTag(int tag) {
        StringBuilder builder = new StringBuilder();
        builder.append(UserInfo.Column.TAG);
        builder.append("= ? ");
        String[] args = new String[]{String.valueOf(tag)};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Authorities.UserInfo.AUTHORITY_URI, null, builder.toString(), args, null);
            if (cursor != null && cursor.moveToNext()) {
                UserInfo info = UserInfo.fromCursor(cursor);
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    public boolean updateUserInfo(UserInfo info) {
        if (info == null) {
            return false;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(UserInfo.Column.TAG);
        builder.append("=?");

        String[] where = new String[]{String.valueOf(info.getTag())};
        int changedRows = context.getContentResolver().update(Authorities.UserInfo.AUTHORITY_URI, info.getContentValues(), builder.toString(), where);
        if (changedRows > 0) {
            return true;
        }
        return false;
    }

    /**
     * 查询当前已经存在的最大的tag
     *
     * @return
     */
    public int queryCurrentMaxTag() {
        int currentMaxTag = -1;
        StringBuilder where = new StringBuilder();
        String[] projection = new String[]{"MAX(" + User.Columns.TAG + ")"};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Authorities.Users.AUTHORITY_URI, projection, where.toString(), null, null);
            if (cursor != null && cursor.moveToNext()) {
                currentMaxTag = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return currentMaxTag;
    }
}
