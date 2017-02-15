package com.example.wei.usb_demo.data.db;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.wei.usb_demo.data.db.bean.BloodOxygenModel;
import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;
import com.example.wei.usb_demo.data.db.bean.ModelBloodSugar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhenqiang on 2017/2/10.
 */

/**
 * 血糖，血压，血氧等数据库管理类
 */
public class DataDBM {
    private static final String TAG = DataDBM.class.getName();
    public static final String SQL_AND = "=? AND ";
    public static final int ONE_THOUSAND = 1000;
    private static DataDBM sSingleton = null;
    private Context context;
    private static final int QUERY_COUNT = 500;

    private DataDBM(Context context) {
        this.context = context;
    }

    public static DataDBM getInstance(Context context) {
        synchronized (DataDBM.class) {
            if (sSingleton == null) {
                if (context != null) {
                    sSingleton = new DataDBM(context.getApplicationContext());
                }
            }
        }
        return sSingleton;
    }

    public long insertModelBloodPressure(ModelBloodPressure modelBloodPressure) {
        if (modelBloodPressure != null) {
            if (modelBloodPressure.getId() == 0) {
                Uri uri = context.getContentResolver().insert(Authorities.DataPressure.AUTHORITY_URI, modelBloodPressure.getValues());
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

    public ArrayList<ModelBloodPressure> getAllModelBloodPressure() {
        ArrayList<ModelBloodPressure> modelBloodPressures = new ArrayList<ModelBloodPressure>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Authorities.DataPressure.AUTHORITY_URI, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                ModelBloodPressure modelBloodPressure = ModelBloodPressure.getFromCusor(cursor);
                if (modelBloodPressure != null) {
                    modelBloodPressures.add(modelBloodPressure);
                }
            }
            return modelBloodPressures;
        } catch (Exception e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    public long insertModelBloodSugar(ModelBloodSugar modelBloodSugar) {
        if (modelBloodSugar != null) {
            if (modelBloodSugar.getId() == 0) {
                Uri uri = context.getContentResolver().insert(Authorities.DataSugar.AUTHORITY_URI, modelBloodSugar.getValues());
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

    public List<ModelBloodSugar> getAllModelBloodSugara() {
        ArrayList<ModelBloodSugar> modelBloodSugars = new ArrayList<ModelBloodSugar>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Authorities.DataSugar.AUTHORITY_URI, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                ModelBloodSugar modelBloodSugar = ModelBloodSugar.getFromCusor(cursor);
                if (modelBloodSugar != null) {
                    modelBloodSugars.add(modelBloodSugar);
                }
            }
            return modelBloodSugars;
        } catch (Exception e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    public long insertModelBloodOxygen(BloodOxygenModel modelBloodOxygen) {
        if (modelBloodOxygen != null) {
            if (modelBloodOxygen.getId() == 0) {
                Uri uri = context.getContentResolver().insert(Authorities.DataSpo2h.AUTHORITY_URI, modelBloodOxygen.getValues());
                if (uri != null) {
                    try {
                        return ContentUris.parseId(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return 0;
    }

    public List<BloodOxygenModel> getAllBloodOxygenModels() {
        ArrayList<BloodOxygenModel> modelBloodOxygens = new ArrayList<BloodOxygenModel>();
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Authorities.DataSpo2h.AUTHORITY_URI, null, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                BloodOxygenModel modelBloodOxygen = BloodOxygenModel.getFromCusor(cursor);
                if (modelBloodOxygen != null) {
                    modelBloodOxygens.add(modelBloodOxygen);
                }
            }
            return modelBloodOxygens;
        } catch (Exception e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }
}
