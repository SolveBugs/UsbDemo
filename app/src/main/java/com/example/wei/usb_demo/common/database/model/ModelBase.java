/*
 * Copyright (c) 2014 Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.common.database.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.example.wei.usb_demo.common.bean.JsonBase;


/**
 * Created by ygc on 14-10-20.
 */
public class ModelBase extends JsonBase implements Parcelable {
    public static final Creator<ModelBase> CREATOR = new Creator<ModelBase>() {
        public ModelBase createFromParcel(Parcel in) {
            return new ModelBase(in);
        }

        public ModelBase[] newArray(int size) {
            return new ModelBase[size];
        }
    };
    private long id = 0;
    private boolean modified = false;
    private long modifyTime = 0;
    private boolean deleted = false;

    public ModelBase() {
        markModify();
    }

    public ModelBase(Parcel in) {
        id = in.readLong();
        modified = in.readInt() == 1;
        modifyTime = in.readLong();
        deleted = in.readInt() == 1;
    }

    public static void getValuesFromCursor(ModelBase entity, Cursor cursor) {
        int index = cursor.getColumnIndex(DNUColumns.ID);
        if (index > -1) {
            entity.setId(cursor.getLong(index));
        }

        index = cursor.getColumnIndex(DNUColumns.MODIFIED);
        if (index > -1) {
            entity.setModified(cursor.getInt(index) != 0);
        }

        index = cursor.getColumnIndex(DNUColumns.MODIFY_TIME);
        if (index > -1) {
            entity.setModifyTime(cursor.getLong(index));
        }

        index = cursor.getColumnIndex(DNUColumns.DELETED);
        if (index > -1) {
            entity.setDeleted(cursor.getInt(index) != 0);
        }
    }

    protected static String getCommSql() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
        sBuilder.append(DNUColumns.MODIFIED).append(" INTEGER,");
        sBuilder.append(DNUColumns.MODIFY_TIME).append(" INTEGER,");
        sBuilder.append(DNUColumns.DELETED).append(" INTEGER, ");
        return sBuilder.toString();
    }

    public void markModify() {
        this.modified = true;
        this.modifyTime = System.currentTimeMillis() / 1000;
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        if (id > 0) {
            values.put(DNUColumns.ID, id);
        }

        values.put(DNUColumns.MODIFIED, modified);
        values.put(DNUColumns.DELETED, deleted);
        if (modifyTime > 0) {
            values.put(DNUColumns.MODIFY_TIME, modifyTime);
        }
        return values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    //以秒为单位
    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void getValuesFromCursor(Cursor cursor) {
        int index = cursor.getColumnIndex(DNUColumns.ID);
        if (index > -1) {
            setId(cursor.getLong(index));
        }

        index = cursor.getColumnIndex(DNUColumns.MODIFIED);
        if (index > -1) {
            setModified(cursor.getInt(index) != 0);
        }

        index = cursor.getColumnIndex(DNUColumns.MODIFY_TIME);
        if (index > -1) {
            setModifyTime(cursor.getLong(index));
        }

        index = cursor.getColumnIndex(DNUColumns.DELETED);
        if (index > -1) {
            setDeleted(cursor.getInt(index) != 0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeInt(modified ? 1 : 0);
        parcel.writeLong(modifyTime);
        parcel.writeInt(deleted ? 1 : 0);
    }
}
