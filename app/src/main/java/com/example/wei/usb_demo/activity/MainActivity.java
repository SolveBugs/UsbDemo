package com.example.wei.usb_demo.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.DeviceListView;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.customviews.IndicateView;
import com.example.wei.usb_demo.usb_device.UsbHandle;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private UsbHandle handel;
    private DeviceListView deviceListView;
    Map<String, UsbDevice> _deviceList = new HashMap<>();
    private String bloodOxygenDeviceKey = null, bloodPressureDeviceKey, bloodSugarDeviceKey;
    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.hideBack(true);
        hint = (TextView) findViewById(R.id.hint);
        deviceListView = (DeviceListView) findViewById(R.id.deviceListView);
        deviceListView.setOnItemClickListener(cellClickListener);
        handel = UsbHandle.ShareHandle(this);
        IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        registerReceiver(handel, intentFilter);         //注册通知
        handel.setUsbDeviceChangeListener(usbDeviceChangeListener);

        IndicateView bloodPressure = (IndicateView) findViewById(R.id.blood_pressure);
        bloodPressure.setOnClickListener(btnOnClickListener);

        IndicateView bloodOxygen = (IndicateView) findViewById(R.id.blood_oxygen);
        bloodOxygen.setOnClickListener(btnOnClickListener);

        IndicateView bloodSugar = (IndicateView) findViewById(R.id.blood_sugar);
        bloodSugar.setOnClickListener(btnOnClickListener);

        IndicateView read_card = (IndicateView) findViewById(R.id.read_card_main);
        read_card.setOnClickListener(btnOnClickListener);

        IndicateView heartRate = (IndicateView) findViewById(R.id.heart_rate_btn);
        heartRate.setOnClickListener(btnOnClickListener);

        IndicateView printer = (IndicateView) findViewById(R.id.print_btn_main);
        printer.setOnClickListener(btnOnClickListener);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(handel);
        super.onDestroy();
    }

    /**
     * 列表行点击事件
     */
    private AdapterView.OnItemClickListener cellClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i("MainActivity", "onItemClick: " + position);

//            if (selectDeviceType == null) {
//                return;
//            }
//
//            Intent intent = new Intent();
//            Class<?> activity = null;
//            switch (selectDeviceType) {
//                case BloodOxygenDevice: {
//                    activity = BloodOxygenLineActivity.class;
//                    break;
//                }
//                case BloodPressureDevice: {
//                    activity = BloodPressureActivity.class;
//                    break;
//                }
//                case BloodSugarDevice: {
//                    activity = BloodSugarActivity.class;
//                    break;
//                }
//                default: {
//                    Log.i("点击cell", "未知错误");
//                    return;
//                }
//            }
//            intent.setClass(MainActivity.this, activity);
//            intent.putExtra("USB_DEVICE_KEY", ((TextView) view).getText().toString());
//            intent.putExtra("USB_DEVICE_DISCERNED", false);
//            MainActivity.this.startActivity(intent);
        }
    };

    /**
     * 设备插入事件
     */
    private UsbHandle.USBDeviceChangeListener usbDeviceChangeListener = new UsbHandle.USBDeviceChangeListener() {
        @Override
        public void onUSBDeviceChanged(Map<String, UsbDevice> deviceList) {
            _deviceList = deviceList;
            final String[] array = _deviceList.keySet().toArray(new String[_deviceList.keySet().size()]);
            deviceListView.reloadData(array);
            if (array.length > 0) {
                hint.setVisibility(View.INVISIBLE);
                deviceListView.setVisibility(View.VISIBLE);
            } else {
                hint.setVisibility(View.VISIBLE);
                deviceListView.setVisibility(View.INVISIBLE);
            }
        }
    };

    /**
     * 按钮点击事件
     */
    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {

        public void onClick(View v) {
            int btn_id = v.getId();
            Intent intent = new Intent();
            if (btn_id == R.id.blood_pressure) {
                intent.putExtra("USB_DEVICE_KEY", bloodPressureDeviceKey);
                intent.setClass(MainActivity.this, BloodPressureActivity.class);
            } else if (btn_id == R.id.blood_oxygen) {
                intent.putExtra("USB_DEVICE_KEY", bloodOxygenDeviceKey);
                intent.setClass(MainActivity.this, BloodOxygenLineActivity.class);
            } else if (btn_id == R.id.blood_sugar) {
                intent.putExtra("USB_DEVICE_KEY", bloodSugarDeviceKey);
                intent.setClass(MainActivity.this, BloodSugarActivity.class);
            } else if (btn_id == R.id.read_card_main) {
                intent.setClass(MainActivity.this, ReadCardActivity.class);
            } else if (btn_id == R.id.print_btn_main) {
                intent.setClass(MainActivity.this, PrinterActivity.class);
            } else if (btn_id == R.id.heart_rate_btn) {
                intent.setClass(MainActivity.this, HeartRateActivity.class);
            }
            MainActivity.this.startActivity(intent);
        }
    };

    @Override
    protected void onDeviceDiscernFinish(int type, String usbKey, int state) {
        super.onDeviceDiscernFinish(type, usbKey, state);

        switch (type) {
            case 0: {
                bloodOxygenDeviceKey = usbKey;
                Log.i("血氧设备", "识别成功");
                break;
            }
            case 1: {
                bloodPressureDeviceKey = usbKey;
                Log.i("血压设备", "识别成功");
                break;
            }
            case 2: {
                bloodSugarDeviceKey = usbKey;
                Log.i("血糖设备", "识别成功");
                break;
            }
            default: {
                Log.i("点击cell", "未知错误");
                break;
            }
        }
    }
}
