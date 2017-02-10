package com.example.wei.usb_demo.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.wei.usb_demo.common.database.model.ModelDataBase;

import java.util.ArrayList;

/**
 * Created by Wei on 2017/2/10.
 */

public class BloodOxygenModel extends ModelDataBase {

    private static final String TAG = "TAG_BloodOxygenModel";
    public final static String TABLE = "blood_oxygen";

    private String dataFileName;        //文件名
    private double dataTime;        //测量时间

    private ArrayList<byte[]> sporhData = new ArrayList<byte[]>();

    public BloodOxygenModel() {
        super();

        dataTime = System.currentTimeMillis() / 1000;
        dataFileName = "";
    }

    public boolean appendData(byte[] d) {
        return sporhData.add(d);
    }

    public ArrayList<byte[]> getSporhData() {
        return sporhData;
    }

    public static String getCreateSql() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(" CREATE TABLE IF NOT EXISTS ").append(BloodOxygenModel.TABLE).append("(");
        sBuilder.append(ModelDataBase.getCommSql());
        sBuilder.append(Columns.COLUMNS_FILE_NAME).append(" VARCHAR(32),");
        sBuilder.append(Columns.COLUMNS_DATA_TIME).append(" LONG)");
        return sBuilder.toString();
    }

    public ContentValues getValues() {
        ContentValues values = super.getValues();
        values.put(Columns.COLUMNS_FILE_NAME, dataTime);
        values.put(Columns.COLUMNS_DATA_TIME, dataFileName);
        return values;
    }

    public class Columns extends DataColumns {
        public final static String COLUMNS_FILE_NAME = "file_name";
        public final static String COLUMNS_DATA_TIME = "data_time";
    }

    public static void getValuesFromCursor(BloodOxygenModel entity, Cursor cursor) {
        ModelDataBase.getValuesFromCursor(entity, cursor);

        int index = cursor.getColumnIndex(Columns.COLUMNS_DATA_TIME);
        if (index > -1) {
            entity.setDataTime(cursor.getLong(index) * 1000);
        }

        index = cursor.getColumnIndex(Columns.COLUMNS_FILE_NAME);
        if (index > -1) {
            entity.setDataFileName(cursor.getString(index));
        }
    }

    public String getDataFileName() {
        return dataFileName;
    }

    public double getDataTime() {
        return dataTime;
    }

    public void setDataTime(double dataTime) {
        this.dataTime = dataTime;
    }

    public void setDataFileName(String dataFileName) {
        this.dataFileName = dataFileName;
    }
}
