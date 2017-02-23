package com.example.wei.usb_demo.data.db.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.wei.usb_demo.common.database.model.DNUColumns;
import com.example.wei.usb_demo.common.database.model.ModelDataBase;

/**
 * Created by zhenqiang on 2017/2/22.
 */

public class ModelReport extends ModelDataBase {
    public final static String TABLE = "data_report_table";
    private String bloodGlucoseDid;
    private String ectDid;
    private String bloodPressureDid;
    private String spo2hDid;
    private long dataTime;
    private String content;


    public static final Parcelable.Creator<ModelReport> CREATOR = new Parcelable.Creator<ModelReport>() {
        public ModelReport createFromParcel(Parcel in) {
            return new ModelReport(in);
        }

        public ModelReport[] newArray(int size) {
            return new ModelReport[size];
        }
    };

    public ModelReport() {
        super();
        dataTime = System.currentTimeMillis() / 1000;
    }

    public static String getTable() {
        return TABLE;
    }

    public static String getCreateSql() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE " + TABLE + " (");
        buffer.append(ModelDataBase.getCommSql());
        buffer.append(Column.BLOOD_GLUCOSE_DID + " TEXT,");
        buffer.append(Column.ECT_DID + " TEXT,");
        buffer.append(Column.BLOOD_PRESSURE_DID + " TEXT,");
        buffer.append(Column.SPO2H_DID + " TEXT,");
        buffer.append(Column.DATA_TIME + " LONG,");
        buffer.append(Column.CONTENT + " TEXT)");

        return buffer.toString();
    }

    public static class Column extends DNUColumns {
        public static final String BLOOD_GLUCOSE_DID = "bloodGlucoseDid";
        public static final String ECT_DID = "ectDid";
        public static final String BLOOD_PRESSURE_DID = "bloodPressureDid";
        public static final String SPO2H_DID = "spo2hDid";
        public static final String DATA_TIME = "dataTime";
        public static final String CONTENT = "content";
    }

    public String getBloodGlucoseDid() {
        return bloodGlucoseDid;
    }

    public void setBloodGlucoseDid(String bloodGlucoseDid) {
        this.bloodGlucoseDid = bloodGlucoseDid;
    }

    public String getEctDid() {
        return ectDid;
    }

    public void setEctDid(String ectDid) {
        this.ectDid = ectDid;
    }

    public String getBloodPressureDid() {
        return bloodPressureDid;
    }

    public void setBloodPressureDid(String bloodPressureDid) {
        this.bloodPressureDid = bloodPressureDid;
    }

    public String getSpo2hDid() {
        return spo2hDid;
    }

    public void setSpo2hDid(String spo2hDid) {
        this.spo2hDid = spo2hDid;
    }

    public long getDataTime() {
        return dataTime * 1000;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime / 1000;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    protected ModelReport(Parcel in) {
        super(in);

        bloodGlucoseDid = in.readString();
        ectDid = in.readString();
        bloodPressureDid = in.readString();
        spo2hDid = in.readString();
        dataTime = in.readLong();
        content = in.readString();

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);

        parcel.writeString(bloodGlucoseDid);
        parcel.writeString(ectDid);
        parcel.writeString(bloodPressureDid);
        parcel.writeString(spo2hDid);
        parcel.writeLong(dataTime);
        parcel.writeString(content);

    }
}
