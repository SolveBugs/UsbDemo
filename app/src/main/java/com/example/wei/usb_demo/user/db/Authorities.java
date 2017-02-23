/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.user.db;

import android.net.Uri;

import com.example.wei.usb_demo.common.database.DnurseAuthority;


/**
 * Created by ygc on 14-10-20.
 */
public final class Authorities extends DnurseAuthority {

    public static final class Users {
        private Users() {
        }

        public static final String PATH = "user";
        public static final Uri AUTHORITY_URI = Uri.parse("content://"
                + AUTHORITY + "/" + PATH);
    }

    public static final class UserInfo {
        private UserInfo() {
        }

        public static final String PATH = "user_info";
        public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);
    }

}
