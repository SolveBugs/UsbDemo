package com.example.wei.usb_demo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.DeviceListView;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.app.MainRouter;
import com.example.wei.usb_demo.customviews.IndicateView;
import com.example.wei.usb_demo.main.router.MainUI;
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
//    private MHandler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.hideBack(true);
        hint = (TextView) findViewById(R.id.hint);
        deviceListView = (DeviceListView) findViewById(R.id.deviceListView);
        handel = UsbHandle.ShareHandle(this);
        IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_DEVICE_ATTACHED");
        intentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
        registerReceiver(handel, intentFilter);         //注册通知
        handel.setUsbDeviceChangeListener(usbDeviceChangeListener);
        registerReceiver(deviceDetachedReceiver, intentFilter);

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

//        mHandler = new MHandler(this);
//        WorkService.addHandler(mHandler);
//
//        if (null == WorkService.workThread) {
//            Intent intent = new Intent(this, WorkService.class);
//            startService(intent);
//        }
//
//        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
//        Map<String, UsbDevice> usbList = usbManager.getDeviceList();
//        for (Object o : usbList.entrySet()) {
//            Map.Entry entry = (Map.Entry) o;
//            String key = (String) entry.getKey();
//            UsbDevice val = (UsbDevice) entry.getValue();
//            if (val.getVendorId() == 1659 && val.getProductId() == 8963) {
//                if (!key.equals(bloodOxygenDeviceKey)) {
//                    WorkService.workThread.connectUsb(usbManager,
//                            val);
//                }
//            }
//        }
    }

//    static class MHandler extends Handler {
//
//        WeakReference<MainActivity> mActivity;
//
//        MHandler(MainActivity activity) {
//            mActivity = new WeakReference<MainActivity>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            MainActivity theActivity = mActivity.get();
//            switch (msg.what) {
//
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(handel);
        unregisterReceiver(deviceDetachedReceiver);
        super.onDestroy();
    }

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
            Bundle bundle = new Bundle();
            int id = -1;
            if (btn_id == R.id.blood_pressure) {
                bundle.putString("USB_DEVICE_KEY", bloodPressureDeviceKey);
                id = MainUI.BLOOD_PRESS;
            } else if (btn_id == R.id.blood_oxygen) {
                bundle.putString("USB_DEVICE_KEY", bloodOxygenDeviceKey);
                id = MainUI.BLOOD_OXYGEN;
            } else if (btn_id == R.id.blood_sugar) {
                bundle.putString("USB_DEVICE_KEY", bloodSugarDeviceKey);
                id = MainUI.BLOOD_SUGAR;
            } else if (btn_id == R.id.read_card_main) {
                id = MainUI.READ_CARD;
            } else if (btn_id == R.id.print_btn_main) {
                id = MainUI.PRINTER;
            } else if (btn_id == R.id.heart_rate_btn) {
                id = MainUI.HEART_RATE;
            }
            MainRouter.getInstance(MainActivity.this).showActivity(id, bundle);
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

    BroadcastReceiver deviceDetachedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
            if (action.equalsIgnoreCase("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
                Log.i("Handel", "onReceive: " + "拔出设备");
                String key = device.getDeviceName();
                if (key.equals(bloodOxygenDeviceKey)) {
                    bloodOxygenDeviceKey = null;
                } else if (key.equals(bloodPressureDeviceKey)) {
                    bloodPressureDeviceKey = null;
                } else if (key.equals(bloodSugarDeviceKey)) {
                    bloodSugarDeviceKey = null;
                }
            }
        }
    };
}
