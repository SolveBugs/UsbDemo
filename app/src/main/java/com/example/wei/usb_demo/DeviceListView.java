package com.example.wei.usb_demo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Wei on 2016/12/20.
 */

public class DeviceListView extends ListView {

    private final DeviceListAdapter adapter;
    private String[] _deviceTitleList = null;

    public DeviceListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        adapter = new DeviceListAdapter();
        this.setAdapter(adapter);
    }

    public void reloadData(String[] deviceTitleListList) {
        _deviceTitleList = new String[deviceTitleListList.length];
        _deviceTitleList = deviceTitleListList;
        adapter.notifyDataSetChanged();
    }

    private class DeviceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (_deviceTitleList == null) {
                return 0;
            }
            return _deviceTitleList.length;
        }

        @Override
        public Object getItem(int position) {
            return _deviceTitleList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView cell = new TextView(getContext());
            cell.setHeight(110);
            cell.setText(_deviceTitleList[position]);
            return cell;
        }
    }
}
