/** 
 * 南京熙健 ecg 开发支持库 
 * Copyright (C) 2015 mhealth365.com
 * create by lc  2015年8月31日 上午11:07:37 
 */

package com.example.wei.usb_demo.utils.file;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.wei.usb_demo.common.database.model.ModelDataBase;
import com.mhealth365.osdk.ecgbrowser.DataSourceEcgBrowser.DataSourceReader;

import java.util.ArrayList;

public class EcgDataSource extends ModelDataBase implements DataSourceReader {

	public final static String TABLE = "ecg";

	long startTime = 0;
	private String dataFileName;
	int sample = 0;
	private int countEcg = 0;
	private ArrayList<int[]> packageList = new ArrayList<int[]>();

	public EcgDataSource(long startTime, int sample) throws Exception {
		super();
		if (sample <= 0)
			throw new Exception("EcgDataSource: sample<=0");
		this.startTime = startTime;
		this.sample = sample;
		countEcg = 0;
	}

	public static final Parcelable.Creator<EcgDataSource> CREATOR = new Parcelable.Creator<EcgDataSource>() {
		public EcgDataSource createFromParcel(Parcel in) {
			return new EcgDataSource(in);
		}

		public EcgDataSource[] newArray(int size) {
			return new EcgDataSource[size];
		}
	};

	public static EcgDataSource newInstance() {
		return new EcgDataSource();
	}

	public EcgDataSource() {
		super();

		startTime = System.currentTimeMillis();
		dataFileName = "";
	}

	public EcgDataSource(Parcel in) {
		super(in);
		dataFileName = in.readString();
		startTime = in.readLong();
	}

	public void fillPackage(ArrayList<int[]> data) {
		if (data == null)
			return;
		packageList.addAll(data);
		countEcg += data.size();
	}

	public void addPackage(int[] data) {
		packageList.add(data);
		countEcg++;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	@Override
	public long getDataStartTime() {
		return startTime;
	}

	@Override
	public int getSample() {
		return sample;
	}

	@Override
	public long getPackageNum() {
		return packageList.size();
	}

	public int getSeconds() {
		if (sample == 0)
			return -1;
		float size = getPackageNum();
		int seconds = (int) Math.floor(size / sample);
		return seconds;
	}

	@Override
	public ArrayList<int[]> read(long index, int num) {
		// Log.i("EcgDataSource", "index:"+index+",num:"+num);
		ArrayList<int[]> copy = new ArrayList<int[]>();
		long end = index + num;
		for (long i = index; i < end; i++) {
			copy.add(packageList.get((int) i));
		}
		return copy;
	}

	@Override
	public void updateIndex(int indexSecond) {
	}

	@Override
	public String toString() {
		float seconds = 0;
		float hz = sample;
		if (hz > 0) {
			seconds = getPackageNum() / hz;
		}
		return "EcgDataSource[" + "startTime:" + startTime + ",sample:" + sample + ",size:" + getPackageNum() + ",seconds:" + seconds + ",countEcg:" + countEcg
				+ "]";
	}

	public ArrayList<int[]> getEcgData() {
		return packageList;
	}

	public static String getCreateSql() {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(" CREATE TABLE IF NOT EXISTS ").append(EcgDataSource.TABLE).append("(");
		sBuilder.append(ModelDataBase.getCommSql());
		sBuilder.append(Columns.COLUMNS_FILE_NAME).append(" VARCHAR(32),");
		sBuilder.append(Columns.COLUMNS_DATA_TIME).append(" LONG)");
		return sBuilder.toString();
	}

	public ContentValues getValues() {
		ContentValues values = super.getValues();
		values.put(Columns.COLUMNS_FILE_NAME, dataFileName);
		values.put(Columns.COLUMNS_DATA_TIME, startTime/1000);
		return values;
	}

	public static EcgDataSource getFromCusor(Cursor cursor) {
		EcgDataSource modelBloodOxygen = EcgDataSource.newInstance();
		modelBloodOxygen.getValuesFromCursor(cursor);
		return modelBloodOxygen;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		super.writeToParcel(parcel, i);
		parcel.writeString(dataFileName);
		parcel.writeLong(startTime);
	}

	public void getValuesFromCursor(Cursor cursor) {
		super.getValuesFromCursor(cursor);

		int index = cursor.getColumnIndex(Columns.COLUMNS_FILE_NAME);
		if (index > -1) {
			setDataFileName(cursor.getString(index));
		}

		index = cursor.getColumnIndex(Columns.COLUMNS_DATA_TIME);
		if (index > -1) {
			setStartTime(cursor.getLong(index)*1000);
		}
	}

	public class Columns extends DataColumns {
		public final static String COLUMNS_FILE_NAME = "file_name";
		public final static String COLUMNS_DATA_TIME = "data_time";
	}
}