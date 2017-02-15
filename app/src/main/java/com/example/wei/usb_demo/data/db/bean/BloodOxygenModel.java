package com.example.wei.usb_demo.data.db.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.wei.usb_demo.common.database.model.ModelDataBase;
import com.example.wei.usb_demo.utils.Utils;
import com.example.wei.usb_demo.utils.file.Spo2hFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
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

    public static final Parcelable.Creator<BloodOxygenModel> CREATOR = new Parcelable.Creator<BloodOxygenModel>() {
        public BloodOxygenModel createFromParcel(Parcel in) {
            return new BloodOxygenModel(in);
        }

        public BloodOxygenModel[] newArray(int size) {
            return new BloodOxygenModel[size];
        }
    };

    public static BloodOxygenModel newInstance() {
        return new BloodOxygenModel();
    }

    public BloodOxygenModel() {
        super();

        dataTime = System.currentTimeMillis() / 1000;
        dataFileName = "";
    }

    public BloodOxygenModel(Parcel in) {
        super(in);
        dataFileName = in.readString();
        dataTime = in.readDouble();
    }

    public boolean appendData(byte[] d) {
        return sporhData.add(d);
    }

    public ArrayList<byte[]> getSporhData() {
        return sporhData;
    }

    public String[] getStrDataArray() {
        byte[] data = Spo2hFile.read(new File(Utils.getSDCardPath()+"/mdm_data/Spo2h/"+getDataFileName()));
        String dataStr = null;
        try {
            dataStr = new String(data, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (dataStr == null) {
            return null;
        }

        return dataStr.split("\\n\\r");
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
        values.put(Columns.COLUMNS_FILE_NAME, dataFileName);
        values.put(Columns.COLUMNS_DATA_TIME, dataTime);
        return values;
    }

    public static BloodOxygenModel getFromCusor(Cursor cursor) {
        BloodOxygenModel modelBloodOxygen = BloodOxygenModel.newInstance();
        modelBloodOxygen.getValuesFromCursor(cursor);
        return modelBloodOxygen;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(dataFileName);
        parcel.writeDouble(dataTime);
    }

    public void getValuesFromCursor(Cursor cursor) {
        super.getValuesFromCursor(cursor);

        int index = cursor.getColumnIndex(Columns.COLUMNS_FILE_NAME);
        if (index > -1) {
            setDataFileName(cursor.getString(index));
        }

        index = cursor.getColumnIndex(Columns.COLUMNS_DATA_TIME);
        if (index > -1) {
            setDataTime(cursor.getLong(index) * 1000);
        }
    }

    public class Columns extends DataColumns {
        public final static String COLUMNS_FILE_NAME = "file_name";
        public final static String COLUMNS_DATA_TIME = "data_time";
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
