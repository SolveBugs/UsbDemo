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
    private String sn = "";
    private String accessToken = "";
    private boolean temp;
    private boolean actived;
    private long registerTime;
    private int tag;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public static User newInstance() {
        return new User();
    }

    public User() {
        registerTime = System.currentTimeMillis() / 1000;
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

    public long getRegisterTime() {
        return registerTime * 1000;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime / 1000;
    }

    public static String getCreateSql() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("CREATE TABLE ").append(TABLE).append(" (");
        sBuilder.append(getCommSql());
        sBuilder.append(Columns.NAME).append(" TEXT,");
        sBuilder.append(Columns.REGISTER_TIME).append(" LONG,");
        sBuilder.append(Columns.TEMP).append(" INTEGER,");
        sBuilder.append(Columns.ACTIVIED).append(" INTEGER,");
        sBuilder.append(Columns.TAG).append(" INTEGER,");
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

        if (accessToken != null) {
            values.put(Columns.ACCESS_TOKEN, accessToken);
        }

        if (sn != null) {
            values.put(Columns.SN, sn);
        }

        values.put(Columns.REGISTER_TIME, registerTime);
        values.put(Columns.TAG, tag);
        return values;
    }

    @Override
    public void getValuesFromCursor(Cursor cursor) {
        super.getValuesFromCursor(cursor);

        int index = cursor.getColumnIndex(Columns.NAME);
        if (index > -1) {
            setName(cursor.getString(index));
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
        index = cursor.getColumnIndex(Columns.REGISTER_TIME);
        if (index > -1) {
            setRegisterTime(cursor.getLong(index) * 1000);
        }
        index = cursor.getColumnIndex(Columns.TAG);
        if (index > -1) {
            setTag(cursor.getInt(index));
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", sn='" + sn + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", temp=" + temp +
                ", actived=" + actived +
                ", registerTime=" + registerTime +
                ", tag=" + tag +
                '}';
    }

    public static class Columns extends DNUColumns {
        public final static String NAME = "nickname";
        public final static String TEMP = "temp";
        public final static String ACTIVIED = "actived";
        public final static String ACCESS_TOKEN = "access";
        public final static String SN = "sn";
        public final static String REGISTER_TIME = "register_time";
        public final static String TAG = "tag";

        private Columns() {

        }
    }
}
