/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.common.module;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;


import com.example.wei.usb_demo.app.AppContext;

import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by ygc on 14-10-18.
 */
public class ModBase {

    private String name = null;
    private int dbVer = 0;
    private Context context;

    public ModBase(String name, int dbVer) {
        this.name = name;
        this.dbVer = dbVer;
    }

    public ModBase(Context context, String name, int dbVer) {
        this.context = context;
        this.name = name;
        this.dbVer = dbVer;
    }

    public Context getContext() {
        return context;
    }

    /**
     * get the module database version, provider use the code to control internal database version
     *
     * @return
     */
    public int getDatabaseVersion() {
        return dbVer;
    }

    /**
     * @return module
     */
    public String getName() {
        return name;
    }

    /**
     * get the matcher to identify the module tables.
     *
     * @return uris
     */
    public ArrayList<UriMatcherInfo> getUriMatchers() {
        return new ArrayList<UriMatcherInfo>();
    }

    /**
     * get the table name by matching code which get by uri matchers
     *
     * @param code
     * @return table name or null.
     */
    public String getDBTableName(int code) {
        return null;
    }

    /**
     * create database
     *
     * @param db
     * @return true success false: failure
     */
    // create db
    public boolean onDatabaseCreate(SQLiteDatabase db) {
        return false;
    }

    /**
     * @param db      upgrade the tables of the module
     * @param oldVer  , table old version
     * @param newVer, table new version
     * @return true success false: failure
     */
    // update db
    public boolean onDatabaseUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        return false;
    }

    /**
     * @param oldDb  old tables of the module
     * @param oldVer table old version
     * @param newDb  new Database
     * @return true success false: failure
     */
    public boolean onMigrate(SQLiteDatabase oldDb, int oldVer, SQLiteDatabase newDb) {
        return false;
    }

    public boolean onMigrateFinish() {
        return false;
    }

    /**
     * do worker job
     *
     * @param eventId work id that is defined by the module
     * @param userSn  user sn used to identify user.
     * @param param   param client set when send the request
     * @return true: handled, false: not my event.
     */
    // worker thread
    public boolean onDoWorker(int eventId, String userSn, Bundle param) {
        return false;
    }

    /**
     * do sync job.
     *
     * @param eventId    sync id that is defined by the module
     * @param userSn     user sn used to identify user.
     * @param onlyModify true: only upload changed data. false: upload changed data and download data from server
     * @param force      true: do sync operation event the network is mobile. false: don't do sync operation if the network is mobile.
     * @return true:handled, false:not my event
     */
    // sync thread
    public boolean onDoSync(int eventId, String userSn, boolean onlyModify, boolean force) {
        return false;
    }


    public void onAppInit(AppContext appContext) {

    }

    public void onAfterAppInit(AppContext appContext) {

    }

    public boolean onReceiveMessage(Context context, String modname, JSONObject jsonMessage) {
        return false;
    }
}
