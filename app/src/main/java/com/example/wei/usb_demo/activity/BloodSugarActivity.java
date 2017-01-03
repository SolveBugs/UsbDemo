package com.example.wei.usb_demo.activity;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.usb_device.BloodSugarDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;
import com.example.wei.usb_demo.utils.BSDataDispatchUtils;
import com.example.wei.usb_demo.utils.StringUtil;

public class BloodSugarActivity extends AppCompatActivity {

    private static final String TAG = "BloodSugarActivity";

    private String deviceKey = "";
    private UsbHandle usbHandle;
    private BloodSugarDeviceHandle bloodSugarDeviceHandle;
    private TextView tvInfo;

    BSDataDispatchUtils.IBloodSugarDataResultCallback iBloodSugarDataResultCallback = new BSDataDispatchUtils.IBloodSugarDataResultCallback() {

        @Override
        public void onMeasuring(int arg0) {
            setState(arg0);
        }


        @Override
        public void onSucess(float result) {
            tvInfo.setText("血糖值为" + result + "mmol/L");
        }
    };

    private void setState(int arg0) {
        String stateStr = "";
        switch (arg0) {
            case 7:
                stateStr = "过期试纸";
                break;
            case 8:
                stateStr = "试纸拨出";
                break;
            case 9:
                stateStr = "有效试纸";
                break;
            case 10:
                stateStr = "设备休眠";
                break;
            case 11:
                stateStr = "设备低电";
                break;
            case 12:
                stateStr = "环境温度过高";
                break;
            case 13:
                stateStr = "环境温度过低";
                break;
            case 14:
                stateStr = "设备超时未响应";
                break;
            case 15:
                stateStr = "不支持老版本设备";
                break;
            case 16:
                stateStr = "吸样不畅";
                break;
            case 17:
                stateStr = "测量失败";
                break;
            default:
                break;

        }
        tvInfo.setText(stateStr);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_sugar);
        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        bloodSugarDeviceHandle = new BloodSugarDeviceHandle(this, deviceKey);
        usbHandle = UsbHandle.ShareHandle(this);
        usbHandle.setUSBDetachedListener(usbDetachedListener);
        bloodSugarDeviceHandle.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
        bloodSugarDeviceHandle.setBaudRate(115200);
        bloodSugarDeviceHandle.start();

        tvInfo = (TextView) findViewById(R.id.info);
    }


    private UsbDeviceHandle.USBDeviceInputDataListener usbDeviceInputDataListener = new UsbDeviceHandle.USBDeviceInputDataListener() {
        @Override
        public void onUSBDeviceInputData(byte[] data, String deviceKey) {
            String ret_str = StringUtil.bytesToHexString(data);
            Log.i("Write", "包数据：" + ret_str);
            BSDataDispatchUtils.dispatch(data, iBloodSugarDataResultCallback);
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
