package com.example.wei.usb_demo.data.db.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import com.example.wei.usb_demo.common.database.model.ModelDataBase;
import com.example.wei.usb_demo.common.utils.Utils;
import com.example.wei.usb_demo.data.TimePoint;
import com.example.wei.usb_demo.data.common.DataCommon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zhenqiang on 2017/2/10.
 */

/**
 * 血压数据模型
 */

public class ModelBloodSugar extends ModelDataBase {
    public final static String TABLE = "sugar_data_table";
    public final static String VIEW_TABLE = "sugar_data_view";
    public static final Creator<ModelBloodSugar> CREATOR = new Creator<ModelBloodSugar>() {
        public ModelBloodSugar createFromParcel(Parcel in) {
            return new ModelBloodSugar(in);
        }

        public ModelBloodSugar[] newArray(int size) {
            return new ModelBloodSugar[size];
        }
    };

    private float value;
    private long dataTime;
    private TimePoint timePoint;//测量时间点

    public static ModelBloodSugar newInstance() {
        return new ModelBloodSugar();
    }

    public ModelBloodSugar() {
        super();
        dataTime = System.currentTimeMillis() / 1000;
        timePoint = TimePoint.Time_Breakfast_Before;
    }

    protected ModelBloodSugar(Parcel in) {
        super(in);
        value = in.readFloat();
        dataTime = in.readLong();
        timePoint = TimePoint.getTimePointById(in.readInt());
    }

    public static void getValuesFromCursor(ModelBloodSugar entity, Cursor cursor) {

        int index = cursor.getColumnIndex(Columns.COLUMNS_VALUE);
        if (index > -1) {
            entity.setValue(cursor.getFloat(index));
        }

        index = cursor.getColumnIndex(Columns.COLUMNS_DATA_TIME);
        if (index > -1) {
            entity.setDataTime(cursor.getLong(index) * 1000);
        }

        index = cursor.getColumnIndex(Columns.COLUMNS_TIME_POINT);
        if (index > -1) {
            entity.setTimePoint(TimePoint.getTimePointById(cursor.getInt(index)));
        }

        ModelDataBase.getValuesFromCursor(entity, cursor);
    }

    public static ArrayList<ModelBloodSugar> arrayFromJson(JSONArray array, String uid) {
        ArrayList<ModelBloodSugar> list = new ArrayList<ModelBloodSugar>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.optJSONObject(i);
            if (jsonObject != null) {
                ModelBloodSugar data = new ModelBloodSugar();
                data.setModified(false);
                data.setUid(uid);
                data.dataFromJson(jsonObject);
                list.add(data);
            }
        }
        return list;
    }

    public static ModelBloodSugar getFromCusor(Cursor cursor) {
        ModelBloodSugar modelBloodPressure = ModelBloodSugar.newInstance();
        modelBloodPressure.getValuesFromCursor(cursor);
        return modelBloodPressure;
    }

    public static String getCreateSql() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(" CREATE TABLE IF NOT EXISTS ").append(ModelBloodSugar.TABLE).append("(");
        sBuilder.append(ModelDataBase.getCommSql());
        sBuilder.append(Columns.COLUMNS_VALUE).append(" DECIMAL(5,1),");
        sBuilder.append(Columns.COLUMNS_DATA_TIME).append(" LONG,");
        sBuilder.append(Columns.COLUMNS_UP_ID).append(" LONG,");
        sBuilder.append(Columns.COLUMNS_TIME_POINT).append(" SMALLINT)");
        return sBuilder.toString();
    }

    public static String getCreateViewSql() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("CREATE VIEW ").append(VIEW_TABLE).append(" AS SELECT * FROM ");
        sBuilder.append(TABLE).append(" WHERE (");
        sBuilder.append(Columns.COLUMNS_VALUE).append(" >= ").append(DataCommon.MIN_VAlUE).append(" AND ");
        sBuilder.append(Columns.COLUMNS_VALUE).append(" <").append(33.4f).append(") AND ");
        sBuilder.append(Columns.DELETED).append(" = 0");
        return sBuilder.toString();
    }

    public static String getDeleteViewsSql() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("drop view ").append(VIEW_TABLE);
        return sBuilder.toString();
    }

    public ContentValues getValues() {
        ContentValues values = super.getValues();
        values.put(Columns.COLUMNS_VALUE, value);
        values.put(Columns.COLUMNS_DATA_TIME, dataTime);
        values.put(Columns.COLUMNS_TIME_POINT, timePoint.getPointId());
        return values;
    }


    public static ModelBloodSugar fromCursor(Cursor cursor) {
        ModelBloodSugar data = new ModelBloodSugar();
        data.getValuesFromCursor(cursor);
        return data;
    }

    public void getValuesFromCursor(Cursor cursor) {
        super.getValuesFromCursor(cursor);

        int index = cursor.getColumnIndex(Columns.COLUMNS_VALUE);
        if (index > -1) {
            setValue(cursor.getFloat(index));
        }

        index = cursor.getColumnIndex(Columns.COLUMNS_DATA_TIME);
        if (index > -1) {
            setDataTime(cursor.getLong(index) * 1000);
        }


        index = cursor.getColumnIndex(Columns.COLUMNS_TIME_POINT);
        if (index > -1) {
            setTimePoint(TimePoint.getTimePointById(cursor.getInt(index)));
        }

    }

    @Override
    public void dataFromJson(JSONObject jsonObject) {
        super.dataFromJson(jsonObject);

        String did = jsonObject.optString(ModelBloodSugar.Columns.DID);
        if (did != null) {
            setDid(did);
        }

        int delete = jsonObject.optInt(ModelBloodSugar.Columns.DELETED);
        setDeleted(delete > 0);

        long modify = jsonObject.optLong("modif");
        if (modify > 0) {
            setModifyTime(modify);
        }

        long time = jsonObject.optLong("time");
        if (time > 0) {
            this.dataTime = time;
        }

        int point = jsonObject.optInt("point");
        this.timePoint = TimePoint.getTimePointById(point);

    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeFloat(value);
        parcel.writeLong(dataTime);
        parcel.writeInt(timePoint.getPointId());
    }


    @Override
    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        } else {
            ModelBloodSugar data = (ModelBloodSugar) o;
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


    public class Columns extends DataColumns {
        public final static String COLUMNS_VALUE = "value";
        public final static String COLUMNS_DATA_TIME = "data_time";
        public final static String COLUMNS_UP_ID = "_upId";
        public final static String COLUMNS_TIME_POINT = "time_point";
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public long getDataTime() {
        return dataTime * 1000;
    }

    public void setDataTime(long dataTime) {
        this.dataTime = dataTime / 1000;
    }

    public TimePoint getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(TimePoint timePoint) {
        this.timePoint = timePoint;
    }

    @Override
    public String toString() {
        return "ModelBloodSugar{" +
                "value=" + value +
                ", dataTime=" + dataTime +
                ", timePoint=" + timePoint +
                '}';
    }
}
