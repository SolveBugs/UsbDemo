package com.example.wei.usb_demo.activity;

import android.hardware.usb.UsbDevice;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.usb_device.BloodOxygenDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;
import com.example.wei.usb_demo.utils.StringUtil;

import lecho.lib.hellocharts.model.PointValue;

public class PrinterActivity extends BaseActivity {

    private static final String TAG = "PrinterActivity";

    private BloodOxygenDeviceHandle reader = null;
    private UsbHandle handel = null;
    private String deviceKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);

        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        handel = UsbHandle.ShareHandle(this);
        handel.setUSBDetachedListener(usbDetachedListener);
        reader = new BloodOxygenDeviceHandle(this, deviceKey);
        reader.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
        reader.start();
    }

    private UsbDeviceHandle.USBDeviceInputDataListener usbDeviceInputDataListener = new UsbDeviceHandle.USBDeviceInputDataListener() {
        @Override
        public void onUSBDeviceInputData(byte[] data, String deviceKey) {
            String ret_str = StringUtil.bytesToHexString(data);
            Log.i(TAG, "包数据：" + ret_str);
        }
    };

    private UsbHandle.USBDetachedListener usbDetachedListener = new UsbHandle.USBDetachedListener() {
        @Override
        public void onUSBDetached(UsbDevice device) {
            if (device.getDeviceName().equals(deviceKey)) {
                Log.i(TAG, "onUSBDetached: "+device.getDeviceName());
                finish();
            }
        }
    };
}
