package com.example.wei.usb_demo.activity;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    Map<String, ScanResult> wifiList = new HashMap<>();
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> list = wifiManager.getScanResults();
        for (ScanResult result: list) {
            if (!wifiList.containsKey(result.SSID)) {
                wifiList.put(result.SSID, result);
            }
        }

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ScanResult result = wifiList.get(wifiList.keySet().toArray()[position]);
        wifiManager.getWifiState();
    }

    private class MyAdapter extends BaseAdapter {

        public MyAdapter() {
            super();
        }

        @Override
        public int getCount() {
            if (wifiList == null) {
                return 0;
            }
            return wifiList.size();
        }

        @Override
        public Object getItem(int position) {
            return wifiList.get(wifiList.keySet().toArray()[position]);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView cell = new TextView(SettingsActivity.this);
            cell.setHeight(110);
            cell.setText(wifiList.get(wifiList.keySet().toArray()[position]).SSID);
            return cell;
        }
    }
}
