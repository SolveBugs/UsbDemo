/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.common.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by ygc on 14-10-18.
 */
public class ModuleVersion {

    public final static String TABLE = "version";

    private String name = null;
    private int version = 0;

    public static ModuleVersion getFromCursor(Cursor cursor) {
        ModuleVersion version = new ModuleVersion();
        int index = cursor.getColumnIndex(VersionColumns.NAME);
        if (index > -1) {
            version.setName(cursor.getString(index));
        }

        index = cursor.getColumnIndex(VersionColumns.VERSION);
        if (index > -1) {
            version.setVersion(cursor.getInt(index));
        }

        return version;
    }

    public static String getCreateSql() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("CREATE TABLE ").append(TABLE).append(" (");
        sBuilder.append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        sBuilder.append(VersionColumns.NAME).append(" TEXT,");
        sBuilder.append(VersionColumns.VERSION).append(" INTEGER)");

        return sBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ContentValues getValues() {
        if (name == null || version == 0) {
            return null;
        }

        ContentValues values = new ContentValues();
        values.put(VersionColumns.NAME, name);
        values.put(VersionColumns.VERSION, version);

        return values;
    }

    public interface VersionColumns extends BaseColumns {
        public final static String NAME = "name";
        public final static String VERSION = "version";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(VersionColumns.NAME);
        builder.append(":").append(name).append(",");
        builder.append(VersionColumns.VERSION).append(":").append(version);
        return builder.toString();
    }
}
