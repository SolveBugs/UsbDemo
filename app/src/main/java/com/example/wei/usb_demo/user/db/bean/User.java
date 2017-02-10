/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.user.db.bean;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.wei.usb_demo.common.database.model.DNUColumns;
import com.example.wei.usb_demo.common.database.model.ModelBase;

import org.json.JSONObject;

/**
 * Created by ygc on 14-10-20.
 */
public class User extends ModelBase {
    public final static String TABLE = "users";
    private String name = "";
    private String password;
    private String sn = "";
    private String accessToken = "";
    private boolean temp;
    private boolean actived;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public static User newInstance() {
        return new User();
    }

    public boolean isTemp() {
        return temp;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }

    public boolean isActived() {
        return actived;
    }

    public void setActived(boolean actived) {
        this.actived = actived;
    }

    public static User fromJSON(JSONObject json) {
        if (json == null) {
            return null;
        }
        User user = new User();

        String name = json.optString("name");
        String sn = json.optString("sn");
        return user;
    }

    public static User getFromCusor(Cursor cursor) {
        User user = User.newInstance();
        user.getValuesFromCursor(cursor);
        return user;
    }

    public static String getCreateSql() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("CREATE TABLE ").append(TABLE).append(" (");
        sBuilder.append(getCommSql());
        sBuilder.append(Columns.NAME).append(" TEXT,");
        sBuilder.append(Columns.PASSWORD).append(" TEXT,");
        sBuilder.append(Columns.TEMP).append(" INTEGER,");
        sBuilder.append(Columns.ACTIVIED).append(" INTEGER,");
        sBuilder.append(Columns.ACCESS_TOKEN).append(" TEXT,");
        sBuilder.append(Columns.SN).append(" INTEGER )");
        return sBuilder.toString();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }


    @Override
    public ContentValues getValues() {
        ContentValues values = super.getValues();

        values.put(Columns.ACTIVIED, actived ? 1 : 0);
        values.put(Columns.TEMP, temp ? 1 : 0);
        if (name != null) {
            values.put(Columns.NAME, name);
        }

        if (password != null) {
            values.put(Columns.PASSWORD, password);
        }

        if (accessToken != null) {
            values.put(Columns.ACCESS_TOKEN, accessToken);
        }

        if (sn != null) {
            values.put(Columns.SN, sn);
        }

        return values;
    }

    @Override
    public void getValuesFromCursor(Cursor cursor) {
        super.getValuesFromCursor(cursor);

        int index = cursor.getColumnIndex(Columns.NAME);
        if (index > -1) {
            setName(cursor.getString(index));
        }

        index = cursor.getColumnIndex(Columns.PASSWORD);
        if (index > -1) {
            setPassword(cursor.getString(index));
        }

        index = cursor.getColumnIndex(Columns.SN);
        if (index > -1) {
            setSn(cursor.getString(index));
        }

        index = cursor.getColumnIndex(Columns.ACCESS_TOKEN);
        if (index > -1) {
            setAccessToken(cursor.getString(index));
        }
        index = cursor.getColumnIndex(Columns.ACTIVIED);
        if (index > -1) {
            setActived(cursor.getInt(index) != 0);
        }

        index = cursor.getColumnIndex(Columns.TEMP);
        if (index > -1) {
            setTemp(cursor.getInt(index) != 0);
        }
    }

    @Override
    public String toString() {
        StringBuilder sBuilder = new StringBuilder("User--");
        sBuilder.append("name:").append(name);
        sBuilder.append(",password:").append(password);
        sBuilder.append(",sn:").append(sn);
        return sBuilder.toString();
    }

    public static class Columns extends DNUColumns {
        public final static String NAME = "nickname";
        public final static String PASSWORD = "password";
        public final static String TEMP = "temp";
        public final static String ACTIVIED = "actived";
        public final static String ACCESS_TOKEN = "access";
        public final static String REFRESH_TOKEN = "access2";
        public final static String SIGNATURE = "signature";
        public final static String SN = "sn";
        public final static String MIGRATE = "migrate";
        public final static String LOGIN_TYPE = "logintype";
        public final static String LOGIN_TIME = "logintime";

        public final static String THIRD_ID = "thridid";
        public final static String THIRD_NICK = "thridnick";
        public final static String THIRD_TOKEN = "thridtoken";
        public final static String THIRD_RETOKEN = "thridretoken";


        private Columns() {

        }
    }
}
