package com.example.wei.usb_demo.activity;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.usb_device.BloodSugarDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;
import com.example.wei.usb_demo.utils.BSDataDispatchUtils;
import com.example.wei.usb_demo.utils.CrcUtil;
import com.example.wei.usb_demo.utils.StringUtil;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BloodSugarActivity extends BaseActivity {

    private static final String TAG = "BloodSugarActivity";

    private String deviceKey = "";
    private UsbHandle usbHandle;
    private BloodSugarDeviceHandle bloodSugarDeviceHandle;
    private TextView tvInfo;
    private Handler handler = new Handler();
    BSDataDispatchUtils.IBloodSugarDataResultCallback iBloodSugarDataResultCallback = new BSDataDispatchUtils.IBloodSugarDataResultCallback() {

        @Override
        public void onMeasuring(int arg0) {
            setState(arg0);
        }


        @Override
        public void onSucess(final float result) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tvInfo.setText("血糖值为" + result + "mmol/L");
                }
            });
        }
    };
    private Timer timer;

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
        if (!TextUtils.isEmpty(stateStr)) {
            tvInfo.setText(stateStr);
        }
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

        timer = new Timer();
        final Random random = new Random();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String dataHead = "aa600412";
                String str = "0123456789abcdef";
                int datapos11 = random.nextInt(16);
                int datapos12 = random.nextInt(16);
                int datapos21 = random.nextInt(16);
                int datapos22 = random.nextInt(16);
                String dataStr11 = str.toCharArray()[datapos11] + "";
                String dataStr12 = str.toCharArray()[datapos12] + "";
                String dataStr21 = str.toCharArray()[datapos21] + "";
                String dataStr22 = str.toCharArray()[datapos22] + "";
                String dataStr = dataHead + dataStr11 + dataStr12 + dataStr21 + dataStr22;
                byte[] data = StringUtil.hexStringToBytes(dataStr);
                char crc = CrcUtil.get_crc_code(data);
                byte[] data_n = new byte[data.length + 1];
                System.arraycopy(data, 0, data_n, 0, data.length);
                data_n[data_n.length - 1] = (byte) crc;
                bloodSugarDeviceHandle.sendToUsb(data_n);
                Log.i(TAG, "run: " + StringUtil.bytesToHexString(data_n));
            }
        }, 500, 500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("测量血糖");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        bloodSugarDeviceHandle.stop();
        bloodSugarDeviceHandle.release();
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
