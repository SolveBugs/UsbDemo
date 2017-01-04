package com.example.wei.usb_demo.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.DeviceListView;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.usb_device.BloodOxygenDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private UsbHandle handel;
    private DeviceListView deviceListView;
    Map<String, UsbDevice> _deviceList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.hideBack(true);

        deviceListView = (DeviceListView) findViewById(R.id.deviceListView);
        deviceListView.setOnItemClickListener(cellClickListener);

        handel = UsbHandle.ShareHandle(this);
        IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        registerReceiver(handel, intentFilter);         //注册通知
        handel.setUsbDeviceChangeListener(usbDeviceChangeListener);

        Button bloodPressure = (Button) findViewById(R.id.blood_pressure);
        bloodPressure.setOnClickListener(btnOnClickListener);

        Button bloodSugar = (Button) findViewById(R.id.blood_sugar);
        bloodSugar.setOnClickListener(btnOnClickListener);

        Button read_card = (Button) findViewById(R.id.read_card_main);
        read_card.setOnClickListener(btnOnClickListener);

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
            Log.i("MainActivity", "onItemClick: " + position);
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, PrinterActivity.class);
            intent.putExtra("USB_DEVICE_KEY", ((TextView) view).getText().toString());
            MainActivity.this.startActivity(intent);
        }
    };

    private UsbHandle.USBDeviceChangeListener usbDeviceChangeListener = new UsbHandle.USBDeviceChangeListener() {
        @Override
        public void onUSBDeviceChanged(Map<String, UsbDevice> deviceList) {
            _deviceList = deviceList;
            final String[] array = _deviceList.keySet().toArray(new String[_deviceList.keySet().size()]);
            deviceListView.reloadData(array);
        }
    };

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int btn_id = v.getId();
            Intent intent = new Intent();
            if (btn_id == R.id.blood_pressure) {
                intent.putExtra("USB_DEVICE_KEY", deviceListView.getAdapter().getItem(0).toString());
                intent.setClass(MainActivity.this, BloodPressureActivity.class);
            } else if (btn_id == R.id.blood_oxygen) {
                intent.setClass(MainActivity.this, BloodOxygenLineActivity.class);
            } else if (btn_id == R.id.blood_sugar) {
                intent.putExtra("USB_DEVICE_KEY", deviceListView.getAdapter().getItem(0).toString());
                intent.setClass(MainActivity.this, BloodSugarActivity.class);
            } else if (btn_id == R.id.read_card_main) {
                intent.setClass(MainActivity.this, ReadCardActivity.class);
            }
            MainActivity.this.startActivity(intent);
        }
    };
}
