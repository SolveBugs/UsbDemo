/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.common.database.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.example.wei.usb_demo.app.AppContext;
import com.example.wei.usb_demo.common.config.AppConfig;
import com.example.wei.usb_demo.common.database.DnurseAuthority;
import com.example.wei.usb_demo.common.database.DnurseDatabaseHelper;
import com.example.wei.usb_demo.common.module.ModBase;
import com.example.wei.usb_demo.common.module.UriMatcherInfo;

import java.util.ArrayList;

/**
 * Created by ygc on 14-10-20.
 */
public class DnurseProvider extends ContentProvider {
    private static final UriMatcher sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    public static final String UNKNOW_URI = "Unknow URI:";
    private ArrayList<ModBase> modlist = null;
    private SQLiteDatabase sqlDB;
    private DnurseDatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        AppContext appContext = (AppContext) getContext().getApplicationContext();

        int dbver = AppConfig.BASE_DB_V;
        ArrayList<ModBase> mods = appContext.getMods();
        for (ModBase mod : mods) {

            // add matcher
            ArrayList<UriMatcherInfo> infos = mod.getUriMatchers();

            for (UriMatcherInfo uriInfo : infos) {
                sMatcher.addURI(DnurseAuthority.AUTHORITY, uriInfo.getPath(), uriInfo.getCode());
            }

            // cal current db version
            dbver += mod.getDatabaseVersion();
        }
        modlist = mods;
        // open db
        try {
            databaseHelper = DnurseDatabaseHelper.getInstance(getContext(), dbver);
            sqlDB = databaseHelper.getWritableDatabase();
        } catch (RuntimeException e) {
            Log.e("ygc", "cant start provider     --->" + e.getMessage());
        }
        return true;
    }

    private String getTableNameByCode(Uri uri) {
        int code = sMatcher.match(uri);
        for (ModBase mod : modlist) {
            String table = mod.getDBTableName(code);
            if (table != null) {
                return table;
            }
        }

        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String talbeName = getTableNameByCode(uri);
        if (talbeName == null) {
            throw new IllegalArgumentException(UNKNOW_URI + uri);
        }

        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        sqb.setTables(talbeName);
        // Get the database and run the query
        //Utils.writeToSd("97 - query : uri = " + uri + " , projection = " + projection + " , selection = " + selection);
        Cursor c = sqb.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder);
        //Utils.writeToSd("99 - query ");

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        // get table name
        String talbeName = getTableNameByCode(uri);
        if (talbeName == null) {
            throw new IllegalArgumentException(UNKNOW_URI + uri);
        }

        //Utils.writeToSd("116 - delete : talbeName = " + talbeName + " , s = " + s + " , strings = " + strings);
        int count = sqlDB.delete(talbeName, s, strings);
        //Utils.writeToSd("118 - delete ");
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // get table name
        String talbeName = getTableNameByCode(uri);
        if (talbeName == null) {
            throw new IllegalArgumentException(UNKNOW_URI + uri);
        }
        // insert
        //Utils.writeToSd("133 - insert : talbeName = " + talbeName + " , contentValues = " + contentValues);
        long rowId = sqlDB.insert(talbeName, talbeName, contentValues);
        //Utils.writeToSd("135 - insert ");

        if (rowId > 0) {
            // insert new id
            Uri ret = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(ret, null);
            return ret;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        // get table name
        String talbeName = getTableNameByCode(uri);
        if (talbeName == null) {
            throw new IllegalArgumentException(UNKNOW_URI + uri);
        }

        // update
        //Utils.writeToSd("156 - update : talbeName = " + talbeName + " , contentValues = " + contentValues + " , s = " + s + " , strings = " + strings);
        int count = sqlDB.update(talbeName, contentValues, s, strings);
        //Utils.writeToSd("158 - update ");
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        sqlDB.beginTransaction();//开始事务
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            sqlDB.setTransactionSuccessful();//设置事务标记为successful
            return results;
        } finally {
            sqlDB.endTransaction();//结束事务
        }
    }
}
