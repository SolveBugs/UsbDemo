/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.common.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.wei.usb_demo.app.AppContext;
import com.example.wei.usb_demo.app.CustomConfig;
import com.example.wei.usb_demo.common.module.ModBase;

import java.util.ArrayList;

/**
 * Created by ygc on 14-10-20.
 */
public class DnurseDatabaseHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = CustomConfig.DB_NAME;
    private static DnurseDatabaseHelper sSingleton = null;
    private Context context;

    private DnurseDatabaseHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
        this.context = context;
    }

    public static DnurseDatabaseHelper getInstance(Context context, int version) {
        synchronized (DnurseDatabaseHelper.class) {
            if (sSingleton == null) {
                sSingleton = new DnurseDatabaseHelper(context.getApplicationContext(), version);
            }
        }

        return sSingleton;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        boolean success = true;

        try {
            // create version table
            db.execSQL(ModuleVersion.getCreateSql());

            AppContext appContext = (AppContext) context.getApplicationContext();

            ArrayList<ModBase> mods = appContext.getMods();
            for (ModBase mod : mods) {
                if (mod.getDatabaseVersion() != 0) {
                    if (mod.onDatabaseCreate(db)) {
                        ModuleVersion version = new ModuleVersion();
                        version.setVersion(mod.getDatabaseVersion());
                        version.setName(mod.getName());

                        if (db.insert(ModuleVersion.TABLE, null, version.getValues()) == 0) {
                            success = false;
                            break;
                        }
                    } else {
                        success = false;
                        break;
                    }
                }
            }

            if (success) {
                db.setTransactionSuccessful();
                Log.d(getClass().getName(), "---> 创建数据库完成");
            }
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.beginTransaction();
        boolean success = true;

        try {
            String sql = ModuleVersion.VersionColumns.NAME + " = ?";
            AppContext appContext = (AppContext) context.getApplicationContext();

            ArrayList<ModBase> mods = appContext.getMods();
            for (ModBase mod : mods) {
                if (mod.getDatabaseVersion() != 0) {
                    ModuleVersion oldVersion = getVersionByName(db, mod.getName());
                    if (oldVersion != null) {
                        if (oldVersion.getVersion() != mod.getDatabaseVersion()) {
                            if (mod.onDatabaseUpgrade(db, oldVersion.getVersion(), mod.getDatabaseVersion())) {
                                oldVersion.setVersion(mod.getDatabaseVersion());
                                String[] args = new String[]{mod.getName()};
                                if (db.update(ModuleVersion.TABLE, oldVersion.getValues(), sql, args) == 0) {
                                    success = false;
                                    break;
                                }
                            } else {
                                success = false;
                                break;
                            }
                        }
                    } else {
                        if (mod.onDatabaseCreate(db)) {
                            ModuleVersion version = new ModuleVersion();
                            version.setVersion(mod.getDatabaseVersion());
                            version.setName(mod.getName());

                            if (db.insert(ModuleVersion.TABLE, null, version.getValues()) == 0) {
                                success = false;
                                break;
                            }
                        } else {
                            success = false;
                            break;
                        }
                    }
                }
            }

            if (success) {
                db.setTransactionSuccessful();
            }
        } finally {
            db.endTransaction();
        }
    }

    private ModuleVersion getVersionByName(SQLiteDatabase db, String name) {
        if (db == null || name == null)
            return null;

        String sql = ModuleVersion.VersionColumns.NAME + " = ?";
        String[] args = new String[]{name};

        Cursor cursor = db.query(ModuleVersion.TABLE, null, sql, args, null, null, null);
        try {
            if (cursor.moveToNext()) {
                return ModuleVersion.getFromCursor(cursor);
            }

            return null;
        } finally {
            cursor.close();
        }
    }
}
