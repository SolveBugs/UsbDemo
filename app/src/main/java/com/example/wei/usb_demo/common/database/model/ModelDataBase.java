package com.example.wei.usb_demo.common.database.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

import org.json.JSONObject;


public class ModelDataBase extends ModelBase implements Cloneable {

    public static final Creator<ModelDataBase> CREATOR = new Creator<ModelDataBase>() {
        public ModelDataBase createFromParcel(Parcel in) {
            return new ModelDataBase(in);
        }

        public ModelDataBase[] newArray(int size) {
            return new ModelDataBase[size];
        }
    };
    private String uid;
    private String did;


    public ModelDataBase() {
        super();
    }

    public ModelDataBase(Parcel in) {
        super(in);
        uid = in.readString();
        did = in.readString();
    }


    public static void getValuesFromCursor(ModelDataBase entity, Cursor cursor) {
        int index = cursor.getColumnIndex(DataColumns.UID);
        if (index > -1) {
            entity.uid = cursor.getString(index);
        }

        index = cursor.getColumnIndex(DataColumns.DID);
        if (index > -1) {
            entity.did = cursor.getString(index);
        }
        ModelBase.getValuesFromCursor(entity, cursor);
    }

    protected static String getCommSql() {
        StringBuilder sBuilder = new StringBuilder(ModelBase.getCommSql());
        sBuilder.append(DataColumns.UID).append(" VARCHAR, ");
        sBuilder.append(DataColumns.DID).append(" VARCHAR, ");
        return sBuilder.toString();
    }

    public ContentValues getValues() {
        ContentValues values = super.getValues();
        if (uid != null) {
            values.put(DataColumns.UID, uid);
        }
        if (did != null) {
            values.put(DataColumns.DID, did);
        }
        return values;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public void getValuesFromCursor(Cursor cursor) {
        super.getValuesFromCursor(cursor);
        int index = cursor.getColumnIndex(DataColumns.UID);
        if (index > -1) {
            setUid(cursor.getString(index));
        }

        index = cursor.getColumnIndex(DataColumns.DID);
        if (index > -1) {
            setDid(cursor.getString(index));
        }
    }

    public void dataFromJson(JSONObject jsonObject) {
    }

    public JSONObject jsonFormat() {
        return null;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeString(uid);
        parcel.writeString(did);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public class DataColumns extends DNUColumns {
        public final static String UID = "uid";
        public final static String DID = "did";
    }
}
