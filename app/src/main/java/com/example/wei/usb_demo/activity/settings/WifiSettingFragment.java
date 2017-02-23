package com.example.wei.usb_demo.activity.settings;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class WifiSettingFragment extends Fragment implements AdapterView.OnItemClickListener {

    Map<String, ScanResult> wifiList = new HashMap<>();
    Map<String, WifiConfiguration> saveWifiList = new HashMap<>();
    WifiManager wifiManager;
    private LayoutInflater inflater;

    public WifiSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wifi_setting, container, false);
        this.inflater = inflater;

        wifiManager = (WifiManager) this.getActivity().getSystemService(this.getActivity().WIFI_SERVICE);
        wifiManager.startScan();
        List<ScanResult> list = wifiManager.getScanResults();
        for (ScanResult result: list) {
            if (!wifiList.containsKey(result.SSID)) {
                wifiList.put(result.SSID, result);
            }
        }

        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configurations) {
            saveWifiList.put(config.SSID, config);
        }

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ScanResult result = wifiList.get(wifiList.keySet().toArray()[position]);
        if (saveWifiList.containsKey("\""+result.SSID+"\"")) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info.getSSID().equals("\""+result.SSID+"\"")) {
                Toast.makeText(this.getActivity(), "当前网络已连接", Toast.LENGTH_SHORT).show();
            } else {
                WifiConfiguration config = saveWifiList.get("\""+result.SSID+"\"");
                wifiManager.enableNetwork(config.networkId, true);
            }
        }
    }

    private class MyAdapter extends BaseAdapter {

        int[] imgArr = {R.drawable.wifi_0_icon, R.drawable.wifi_1_icon, R.drawable.wifi_2_icon, R.drawable.wifi_3_icon};

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

            View cell = inflater.inflate(R.layout.wifi_list_cell_layout, null);
            TextView nameLabel = (TextView) cell.findViewById(R.id.wifi_name_label);
            TextView lockTypeLabel = (TextView) cell.findViewById(R.id.wifi_capabilities);
            ScanResult result = wifiList.get(wifiList.keySet().toArray()[position]);
            nameLabel.setText(result.SSID);
            lockTypeLabel.setText(result.capabilities);
            int level = wifiManager.calculateSignalLevel(result.level, 100);
            ImageView rssiImgView = (ImageView) cell.findViewById(R.id.wifi_rssi);
            ImageView lockImgView = (ImageView) cell.findViewById(R.id.wifi_lock);
            rssiImgView.setImageResource(imgArr[level/25]);
            if (result.capabilities.equals("[ESS]")) {
                lockImgView.setVisibility(View.INVISIBLE);
            } else {
                lockImgView.setVisibility(View.VISIBLE);
            }
            return cell;
        }
    }
}
