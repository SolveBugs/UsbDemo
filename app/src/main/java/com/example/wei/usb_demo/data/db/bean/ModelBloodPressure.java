package com.example.wei.usb_demo.data.db.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.wei.usb_demo.common.database.model.ModelDataBase;
import com.example.wei.usb_demo.common.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhenqiang on 2017/2/10.
 */

/**
 * 血压数据模型
 */

public class ModelBloodPressure extends ModelDataBase {
    public final static String TABLE = "pressure_data_table";
    public final static String VIEW_TABLE = "data_view";
    public static final Parcelable.Creator<ModelBloodPressure> CREATOR = new Parcelable.Creator<ModelBloodPressure>() {
        public ModelBloodPressure createFromParcel(Parcel in) {
            return new ModelBloodPressure(in);
        }

        public ModelBloodPressure[] newArray(int size) {
            return new ModelBloodPressure[size];
        }
    };

    private long dataTime;
    private int diastolic;//舒张压
    private int systolic;//收缩压
    private int pulse;//脉搏

    public static ModelBloodPressure newInstance() {
        return new ModelBloodPressure();
    }

    public ModelBloodPressure() {
        super();
        dataTime = System.currentTimeMillis() / 1000;
    }

    protected ModelBloodPressure(Parcel in) {
        super(in);
        dataTime = in.readLong();
        diastolic = in.readInt();
        systolic = in.readInt();
        pulse = in.readInt();
    }

    public static void getValuesFromCursor(ModelBloodPressure entity, Cursor cursor) {

        int index = cursor.getColumnIndex(ModelBloodPressure.Columns.COLUMNS_DATA_TIME);
        if (index > -1) {
            entity.setDataTime(cursor.getLong(index) * 1000);
        }

        index = cursor.getColumnIndex(ModelBloodPressure.Columns.COLUMNS_DIASTOLIC);
        if (index > -1) {
            entity.setDiastolic(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(ModelBloodPressure.Columns.COLUMNS_SYSTOLIC);
        if (index > -1) {
            entity.setSystolic(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(ModelBloodPressure.Columns.COLUMNS_PULSE);
        if (index > -1) {
            entity.setPulse(cursor.getInt(index));
        }

        ModelDataBase.getValuesFromCursor(entity, cursor);
    }

    public static ArrayList<ModelBloodPressure> arrayFromJson(JSONArray array, String uid) {
        ArrayList<ModelBloodPressure> list = new ArrayList<ModelBloodPressure>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            if (jsonObject != null) {
                ModelBloodPressure data = new ModelBloodPressure();
                data.setModified(false);
                data.setUid(uid);
                data.dataFromJson(jsonObject);
                list.add(data);
            }
        }
        return list;
    }

    public static ModelBloodPressure getFromCusor(Cursor cursor) {
        ModelBloodPressure modelBloodPressure = ModelBloodPressure.newInstance();
        modelBloodPressure.getValuesFromCursor(cursor);
        return modelBloodPressure;
    }

    public static String getCreateSql() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(" CREATE TABLE IF NOT EXISTS ").append(ModelBloodPressure.TABLE).append("(");
        sBuilder.append(ModelDataBase.getCommSql());
        sBuilder.append(ModelBloodPressure.Columns.COLUMNS_DATA_TIME).append(" LONG,");
        sBuilder.append(ModelBloodPressure.Columns.COLUMNS_DIASTOLIC).append(" SMALLINT,");
        sBuilder.append(ModelBloodPressure.Columns.COLUMNS_SYSTOLIC).append(" SMALLINT,");
        sBuilder.append(ModelBloodPressure.Columns.COLUMNS_PULSE).append(" SMALLINT)");
        return sBuilder.toString();
    }


    public ContentValues getValues() {
        ContentValues values = super.getValues();
        values.put(ModelBloodPressure.Columns.COLUMNS_DATA_TIME, dataTime);
        values.put(ModelBloodPressure.Columns.COLUMNS_DIASTOLIC, diastolic);
        values.put(ModelBloodPressure.Columns.COLUMNS_SYSTOLIC, systolic);
        values.put(ModelBloodPressure.Columns.COLUMNS_PULSE, pulse);
        return values;
    }


    public static ModelBloodPressure fromCursor(Cursor cursor) {
        ModelBloodPressure data = new ModelBloodPressure();
        data.getValuesFromCursor(cursor);
        return data;
    }

    public void getValuesFromCursor(Cursor cursor) {
        super.getValuesFromCursor(cursor);

        int index = cursor.getColumnIndex(ModelBloodPressure.Columns.COLUMNS_DATA_TIME);
        if (index > -1) {
            setDataTime(cursor.getLong(index) * 1000);
        }

        index = cursor.getColumnIndex(ModelBloodPressure.Columns.COLUMNS_DIASTOLIC);
        if (index > -1) {
            setDiastolic(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(ModelBloodPressure.Columns.COLUMNS_SYSTOLIC);
        if (index > -1) {
            setSystolic(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(ModelBloodPressure.Columns.COLUMNS_PULSE);
        if (index > -1) {
            setPulse(cursor.getInt(index));
        }
    }

    @Override
    public void dataFromJson(JSONObject jsonObject) {
        super.dataFromJson(jsonObject);

        String did = jsonObject.optString(ModelBloodPressure.Columns.DID);
        if (did != null) {
            setDid(did);
        }

        int delete = jsonObject.optInt(ModelBloodPressure.Columns.DELETED);
        setDeleted(delete > 0);

        long modify = jsonObject.optLong("modif");
        if (modify > 0) {
            setModifyTime(modify);
        }

        long time = jsonObject.optLong("time");
        if (time > 0) {
            this.dataTime = time;
        }

        int dias = jsonObject.optInt("diastolic_pressure");
        if (dias > 0) {
            this.diastolic = dias;
        }

        int sys = jsonObject.optInt("systolic_pressure");
        if (sys > 0) {
            this.systolic = sys;
        }

        int pul = jsonObject.optInt("pulse_rate");
        if (pul > 0) {
            this.pulse = pul;
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(dataTime);
        parcel.writeInt(diastolic);
        parcel.writeInt(systolic);
        parcel.writeInt(pulse);
    }


    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        } else {
            ModelBloodPressure data = (ModelBloodPressure) o;
            if (data != null && data.getUid() != null && data.getDid() != null) {
                return data.getUid().equals(getUid()) && data.getDid().equals(getDid());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Utils.getHashCode(getUid(), getDid());
    }


    public class Columns extends ModelDataBase.DataColumns {
        public final static String COLUMNS_DATA_TIME = "data_time";
        public final static String COLUMNS_DIASTOLIC = "diastolic";
        public final static String COLUMNS_SYSTOLIC = "systolic";
        public final static String COLUMNS_PULSE = "pulse";
    }

    public long getDataTime() {
        return dataTime * 1000;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime / 1000;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    @Override
    public String toString() {
        return "ModelBloodPressure{" +
                "dataTime=" + dataTime +
                ", diastolic=" + diastolic +
                ", systolic=" + systolic +
                ", pulse=" + pulse +
                '}';
    }
}
