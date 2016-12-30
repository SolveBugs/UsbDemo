package com.example.wei.usb_demo;

import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private PL2303Handle handel;
    private DeviceListView deviceListView;
    Map<String, UsbDevice> _deviceList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceListView = (DeviceListView) findViewById(R.id.deviceListView);
        deviceListView.setOnItemClickListener(cellClickListener);

        handel = PL2303Handle.ShareHandle(this);
        IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        registerReceiver(handel, intentFilter);         //注册通知
        handel.setUsbDeviceChangeListener(usbDeviceChangeListener);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(handel);
        super.onDestroy();
    }

    private AdapterView.OnItemClickListener cellClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("MainActivity", "onItemClick: "+position);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, BloodOxygenLineActivity.class);
            intent.putExtra("USB_DEVICE_KEY", ((TextView)view).getText().toString());
            MainActivity.this.startActivity(intent);
        }
    };

    private PL2303Handle.USBDeviceChangeListener usbDeviceChangeListener = new PL2303Handle.USBDeviceChangeListener() {
        @Override
        public void onUSBDeviceChanged(Map<String, UsbDevice> deviceList) {
            _deviceList = deviceList;
            final String[] array = _deviceList.keySet().toArray(new String[_deviceList.keySet().size()]);
            deviceListView.reloadData(array);
        }
    };
}
