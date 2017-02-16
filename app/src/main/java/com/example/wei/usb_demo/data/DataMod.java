package com.example.wei.usb_demo.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.wei.usb_demo.common.module.ModBase;
import com.example.wei.usb_demo.common.module.ModuleID;
import com.example.wei.usb_demo.common.module.ModuleNames;
import com.example.wei.usb_demo.common.module.UriMatcherInfo;
import com.example.wei.usb_demo.data.db.Authorities;
import com.example.wei.usb_demo.data.db.DataDBM;
import com.example.wei.usb_demo.data.db.bean.BloodOxygenModel;
import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;
import com.example.wei.usb_demo.data.db.bean.ModelBloodSugar;
import com.example.wei.usb_demo.utils.file.EcgDataSource;

import java.util.ArrayList;

/**
 * Created by Wei on 2017/2/10.
 */

public class DataMod extends ModBase {

    private static final String TAG = "TAG_DataMod";

    public final static int CODE_PRESSURE_DATA = ModuleID.Data + 1;
    public final static int CODE_SUGAE_DATA = ModuleID.Data + 2;
    public final static int CODE_SPO2H_DATA = ModuleID.Data + 3;
    public final static int CODE_ECG_DATA = ModuleID.Data + 4;
    private final static int DB_VER = 1;
    private static DataMod sSingleton;
    private final DataDBM dataDBM;


    private DataMod(Context context) {
        super(context, ModuleNames.Data, DB_VER);
        dataDBM = DataDBM.getInstance(getContext());
    }

    public static DataMod getInstance(Context context) {
        synchronized (context) {
            if (sSingleton == null) {
                sSingleton = new DataMod(context.getApplicationContext());
            }
            return sSingleton;
        }
    }

    @Override
    public ArrayList<UriMatcherInfo> getUriMatchers() {
        ArrayList<UriMatcherInfo> rets = super.getUriMatchers();
        rets.add(new UriMatcherInfo(Authorities.DataPressure.PATH, CODE_PRESSURE_DATA));
        rets.add(new UriMatcherInfo(Authorities.DataSugar.PATH, CODE_SUGAE_DATA));
        rets.add(new UriMatcherInfo(Authorities.DataSpo2h.PATH, CODE_SPO2H_DATA));
        rets.add(new UriMatcherInfo(Authorities.DataEcg.PATH, CODE_ECG_DATA));
        return rets;
    }

    @Override
    public String getDBTableName(int code) {
        switch (code) {
            case CODE_PRESSURE_DATA:
                return ModelBloodPressure.TABLE;
            case CODE_SUGAE_DATA:
                return ModelBloodSugar.TABLE;
            case CODE_SPO2H_DATA:
                return BloodOxygenModel.TABLE;
            case CODE_ECG_DATA:
                return EcgDataSource.TABLE;
        }
        return super.getDBTableName(code);
    }

    @Override
    public boolean onDatabaseCreate(SQLiteDatabase db) {
        try {
            db.execSQL(ModelBloodPressure.getCreateSql());
            Log.d(TAG, "create data table sql ---> " + ModelBloodPressure.getCreateSql());

            db.execSQL(ModelBloodSugar.getCreateSql());
            Log.d(TAG, "create data view sql ---> " + ModelBloodSugar.getCreateSql());

            db.execSQL(ModelBloodSugar.getCreateViewSql());
            Log.d(TAG, "create data view sql ---> " + ModelBloodSugar.getCreateViewSql());

            db.execSQL(BloodOxygenModel.getCreateSql());
            Log.d(TAG, "create data table sql ---> " + BloodOxygenModel.getCreateSql());

            db.execSQL(EcgDataSource.getCreateSql());
            Log.d(TAG, "create data table sql ---> " + EcgDataSource.getCreateSql());

            return true;
        } catch (SQLException e) {
            Log.i(TAG, "onDatabaseCreate: " + e.toString());
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
