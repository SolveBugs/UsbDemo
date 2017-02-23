package com.example.wei.usb_demo.user.db.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.wei.usb_demo.common.database.model.DNUColumns;
import com.example.wei.usb_demo.common.database.model.ModelBase;

/**
 * Created by zhenqiang on 2017/2/22.
 */

public class UserInfo extends ModelBase {
    public final static String TABLE = "user_info";

    private String uid;
    private String realName;
    private int gender;
    private long birth;
    private int height;
    private int weight;
    private int disease;
    private int tag;

    public static final Parcelable.Creator<UserInfo> CREATOR = new Parcelable.Creator<UserInfo>() {
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    public static String getTable() {
        return TABLE;
    }

    public UserInfo() {
    }

    public static UserInfo fromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        UserInfo info = new UserInfo();
        int index = cursor.getColumnIndex(Column.REAL_NAME);
        if (index > -1) {
            info.setRealName(cursor.getString(index));
        }

        index = cursor.getColumnIndex(Column.GENDER);
        if (index > -1) {
            info.setGender(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(Column.BIRTH);
        if (index > -1) {
            info.setBirth(cursor.getLong(index));
        }

        index = cursor.getColumnIndex(Column.HEIGHT);
        if (index > -1) {
            info.setHeight(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(Column.WEIGHT);
        if (index > -1) {
            info.setWeight(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(Column.DISEASE);
        if (index > -1) {
            info.setDisease(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(Column.TAG);
        if (index > -1) {
            info.setTag(cursor.getInt(index));
        }

        index = cursor.getColumnIndex(Column.UID);
        if (index > -1) {
            info.setUid(cursor.getString(index));
        }

        return info;
    }

    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (realName != null) {
            values.put(Column.REAL_NAME, realName);
        }
        values.put(Column.BIRTH, birth);
        values.put(Column.GENDER, gender);
        values.put(Column.HEIGHT, height);
        values.put(Column.WEIGHT, weight);
        values.put(Column.DISEASE, disease);
        values.put(Column.TAG, tag);
        values.put(Column.UID, uid);

        return values;
    }

    public static String getCreateSql() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("CREATE TABLE " + TABLE + " (");
        buffer.append(Column.REAL_NAME + " TEXT,");
        buffer.append(Column.UID + " TEXT,");
        buffer.append(Column.GENDER + " INTEGER,");
        buffer.append(Column.BIRTH + " LONG,");
        buffer.append(Column.HEIGHT + " INTEGER,");
        buffer.append(Column.WEIGHT + " INTEGER,");
        buffer.append(Column.DISEASE + " INTEGER,");
        buffer.append(Column.TAG + " INTEGER)");
        return buffer.toString();
    }

    public static class Column extends DNUColumns {
        public static final String REAL_NAME = "real_name";
        public static final String GENDER = "gender";
        public static final String BIRTH = "birth";
        public static final String WEIGHT = "weight";
        public static final String HEIGHT = "height";
        public static final String DISEASE = "disease";
        public static final String TAG = "tag";
        public static final String UID = "uid";
    }

    protected UserInfo(Parcel in) {
        super(in);

        realName = in.readString();
        gender = in.readInt();
        birth = in.readLong();
        height = in.readInt();
        weight = in.readInt();
        disease = in.readInt();
        tag = in.readInt();
        uid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);

        parcel.writeString(realName);
        parcel.writeInt(gender);
        parcel.writeLong(birth);
        parcel.writeInt(height);
        parcel.writeInt(weight);
        parcel.writeInt(disease);
        parcel.writeInt(tag);
        parcel.writeString(uid);
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getBirth() {
        return birth;
    }

    public void setBirth(long birth) {
        this.birth = birth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getDisease() {
        return disease;
    }

    public void setDisease(int disease) {
        this.disease = disease;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "uid='" + uid + '\'' +
                ", realName='" + realName + '\'' +
                ", gender=" + gender +
                ", birth=" + birth +
                ", height=" + height +
                ", weight=" + weight +
                ", disease=" + disease +
                ", tag=" + tag +
                '}';
    }
}
