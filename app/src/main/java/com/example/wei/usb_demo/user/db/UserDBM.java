/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.user.db;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.wei.usb_demo.app.AppContext;
import com.example.wei.usb_demo.user.db.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ygc on 14-10-22.
 */
public class UserDBM {

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


}
