package com.example.wei.usb_demo.activity;

import android.app.ProgressDialog;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.AppManager;
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
    private ProgressDialog progressDialog;
    /**
     * 解析完数据包后的回调接口
     */
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
    private Random random;
    private boolean usbDeviceDiscerned = false;

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
            final String finalStateStr = stateStr;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tvInfo.setText(finalStateStr);
                }
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_sugar);

        timer = new Timer();
        random = new Random();

        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        usbDeviceDiscerned = intentData.getBoolean("USB_DEVICE_DISCERNED");
        bloodSugarDeviceHandle = new BloodSugarDeviceHandle(this, deviceKey);
        usbHandle = UsbHandle.ShareHandle(this);
        usbHandle.setUSBDetachedListener(usbDetachedListener);
        bloodSugarDeviceHandle.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
        bloodSugarDeviceHandle.setBaudRate(115200);
        bloodSugarDeviceHandle.setUsbDeviceDiscernSucessListener(AppManager.getAppManager().getMainActivity().deviceDiscernSucessListener);
        bloodSugarDeviceHandle.setUsbDeviceDiscernTimeOutListener(listener);
        bloodSugarDeviceHandle.setHandShakePackeData(getHandshakeCommand());
        bloodSugarDeviceHandle.start();

        tvInfo = (TextView) findViewById(R.id.info);

        if (usbDeviceDiscerned) {//已识别的设备
            intiSimulatedData();
        } else {
            progressDialog = new ProgressDialog(BloodSugarActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("识别中");
            progressDialog.show();
        }
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


    private byte[] getHandshakeCommand() {
        String dataHead = "aa600201";
        String dataStr = dataHead;
        byte[] data = StringUtil.hexStringToBytes(dataStr);
        char crc = CrcUtil.get_crc_code(data);
        byte[] data_n = new byte[data.length + 1];
        System.arraycopy(data, 0, data_n, 0, data.length);
        data_n[data_n.length - 1] = (byte) crc;
        return data_n;
    }

    private UsbDeviceHandle.USBDeviceInputDataListener usbDeviceInputDataListener = new UsbDeviceHandle.USBDeviceInputDataListener() {
        @Override
        public void onUSBDeviceInputData(byte[] data, String deviceKey) {
            if (!usbDeviceDiscerned) {
                usbDeviceDiscerned = true;
                bloodSugarDeviceHandle.usbDeviceDiscernSucessListener.onUSBDeviceInputData(UsbDeviceHandle.DeviceType.BloodSugarDevice, deviceKey);
                intiSimulatedData();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Toast.makeText(BloodSugarActivity.this, "识别成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            String ret_str = StringUtil.bytesToHexString(data);
            Log.i("Write", "包数据：" + ret_str);
            BSDataDispatchUtils.dispatch(data, iBloodSugarDataResultCallback);
        }
    };

    /**
     * 模拟假数据
     */
    private void intiSimulatedData() {
        /**
         * 握手成功，每隔1秒模拟发送测量结果报文
         */
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
        }, 1000, 1000);
    }

    private UsbHandle.USBDetachedListener usbDetachedListener = new UsbHandle.USBDetachedListener() {
        @Override
        public void onUSBDetached(UsbDevice device) {
            if (device.getDeviceName().equals(deviceKey)) {
                Log.i("USB拔出", "onUSBDetached: " + device.getDeviceName());
                finish();
            }
        }
    };

    private UsbDeviceHandle.USBDeviceDiscernTimeOutListener listener = new UsbDeviceHandle.USBDeviceDiscernTimeOutListener() {
        @Override
        public void onUsbDeviceDiscerning() {
            if (!usbDeviceDiscerned) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(BloodSugarActivity.this, "识别超时", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        }
    };
}
