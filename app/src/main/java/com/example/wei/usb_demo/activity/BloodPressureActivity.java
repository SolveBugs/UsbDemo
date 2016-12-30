package com.example.wei.usb_demo.activity;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.usb_device.BloodOxygenDeviceHandle;
import com.example.wei.usb_demo.usb_device.BloodPressureDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;
import com.example.wei.usb_demo.utils.StringUtil;

public class BloodPressureActivity extends BaseActivity {
    private static final String TAG = "BloodPressureActivity";

    private Button btnSend;
    private EditText etSendData;

    private BloodPressureDeviceHandle bloodPressureDeviceHandle;
    private String deviceKey = "";
    private UsbHandle handel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);

        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        bloodPressureDeviceHandle = new BloodPressureDeviceHandle(this, deviceKey);
        handel = UsbHandle.ShareHandle(this);
        handel.setUSBDetachedListener(usbDetachedListener);
        bloodPressureDeviceHandle.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
        bloodPressureDeviceHandle.setBaudRate(115200);
        bloodPressureDeviceHandle.start();

        Button btn1 = (Button) findViewById(R.id.record_btn);
        btn1.setOnClickListener(btnOnClickListener);

        Button btn2 = (Button) findViewById(R.id.real_time_btn);
        btn2.setOnClickListener(btnOnClickListener);

        etSendData = (EditText) findViewById(R.id.send_data_et);
        btnSend = (Button) findViewById(R.id.send_btn);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testData = "aa80010301010101";
                byte[] data = StringUtil.hexStringToBytes(etSendData.getText().toString());
                if (data != null) {
                    bloodPressureDeviceHandle.sendToUsb(data);
                }
            }
        });

    }


    private UsbDeviceHandle.USBDeviceInputDataListener usbDeviceInputDataListener = new UsbDeviceHandle.USBDeviceInputDataListener() {
        @Override
        public void onUSBDeviceInputData(byte[] data, String deviceKey) {
            String ret_str = StringUtil.bytesToHexString(data);
            Log.i("Write", "包数据：" + ret_str);

        }
    };

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int btn_id = v.getId();
            Intent intent = new Intent();
            if (btn_id == R.id.record_btn) {
                intent.setClass(BloodPressureActivity.this, BloodRecordActivity.class);
            } else if (btn_id == R.id.real_time_btn) {
                intent.setClass(BloodPressureActivity.this, RealtimeActivity.class);
            }
            BloodPressureActivity.this.startActivity(intent);
        }
    };

    private UsbHandle.USBDetachedListener usbDetachedListener = new UsbHandle.USBDetachedListener() {
        @Override
        public void onUSBDetached(UsbDevice device) {
            if (device.getDeviceName().equals(deviceKey)) {
                Log.i("USB拔出", "onUSBDetached: " + device.getDeviceName());
                finish();
            }
        }
    };
}
