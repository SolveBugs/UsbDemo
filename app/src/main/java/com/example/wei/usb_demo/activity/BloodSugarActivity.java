package com.example.wei.usb_demo.activity;

import android.app.ProgressDialog;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.common.broatcast.UIBroadcastReceiver;
import com.example.wei.usb_demo.data.TimePoint;
import com.example.wei.usb_demo.data.db.DataDBM;
import com.example.wei.usb_demo.data.db.bean.ModelBloodSugar;
import com.example.wei.usb_demo.usb_device.BloodSugarDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;
import com.example.wei.usb_demo.utils.BSDataDispatchUtils;
import com.example.wei.usb_demo.utils.CrcUtil;
import com.example.wei.usb_demo.utils.IDGenerator;
import com.example.wei.usb_demo.utils.StringUtil;

public class BloodSugarActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BloodSugarActivity";

    private String deviceKey = "";
    private UsbHandle usbHandle;
    private BloodSugarDeviceHandle bloodSugarDeviceHandle;
    private TextView tvInfo;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;
    private boolean usbDeviceDiscerned;
    private Button btnConnect, btnStartTest, btnTestRandom, btnTestData;
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
                    ModelBloodSugar bloodSugar = new ModelBloodSugar();
                    bloodSugar.setUid("1");
                    bloodSugar.setTimePoint(TimePoint.Time_Breakfast_After);
                    bloodSugar.setValue(result);
                    bloodSugar.setDid(IDGenerator.newIdWithTag("BG"));
                    long l = DataDBM.getInstance(BloodSugarActivity.this).insertModelBloodSugar(bloodSugar);
                    if (l > 0) {
                        Log.i(TAG, "run: 插入血糖数据成功");
                    }
                }
            });
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
            case 18:
                stateStr = "测量成功";
            case 19:
                stateStr = "开始测量";
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
        setNeedBroadcast(true);
        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        usbHandle = UsbHandle.ShareHandle(this);
        usbHandle.setUSBDetachedListener(usbDetachedListener);

        progressDialog = new ProgressDialog(BloodSugarActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("识别中");
        progressDialog.show();

        bloodSugarDeviceHandle = new BloodSugarDeviceHandle(this);
        bloodSugarDeviceHandle.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
        bloodSugarDeviceHandle.setUsbDeviceDiscernFalseListener(listener);
        bloodSugarDeviceHandle.setBaudRate(115200);
        bloodSugarDeviceHandle.start();

        tvInfo = (TextView) findViewById(R.id.info);
        btnConnect = (Button) findViewById(R.id.btn_connct);
        btnStartTest = (Button) findViewById(R.id.btn_start_test);
        btnTestRandom = (Button) findViewById(R.id.btn_test_random);
        btnTestData = (Button) findViewById(R.id.btn_test_data);

        btnConnect.setOnClickListener(this);
        btnStartTest.setOnClickListener(this);
        btnTestRandom.setOnClickListener(this);
        btnTestData.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("测量血糖");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bloodSugarDeviceHandle.stop();
        bloodSugarDeviceHandle.release();
        usbHandle.setUSBDetachedListener(null);
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


    private byte[] getCommand(int type) {
        String dataHead = "";
        if (type == 1) {//连接血糖模块
            dataHead = "aa600205";
        } else if (type == 2) {//启动测量
            dataHead = "aa600206";
        }
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
                Log.i(TAG, "onUSBDeviceInputData: 第一次收到数据");
                progressDialog.dismiss();
                usbDeviceDiscerned = true;
            }

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

    private UsbDeviceHandle.USBDeviceDiscernFalseListener listener = new UsbDeviceHandle.USBDeviceDiscernFalseListener() {
        @Override
        public void onUsbDeviceDiscerning() {
            if (progressDialog != null && progressDialog.isShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(BloodSugarActivity.this, "识别血糖设备失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }
    };

    @Override
    protected void onDeviceDiscernFinish(int type, String usbKey, int state) {
        super.onDeviceDiscernFinish(type, usbKey, state);
        Toast.makeText(BloodSugarActivity.this, "识别血糖设备成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActionReceive(int action, Bundle bundle) {
        super.onActionReceive(action, bundle);
        if (action == UIBroadcastReceiver.BROADCAST_ACTION_DISCERN_TIME_OUT) {
            if (!usbDeviceDiscerned) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(BloodSugarActivity.this, "连接血糖设备失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connct:
                byte[] bytes = getCommand(1);
                bloodSugarDeviceHandle.sendToUsb(bytes);
                break;
            case R.id.btn_start_test:
                byte[] bytes1 = getCommand(2);
                bloodSugarDeviceHandle.sendToUsb(bytes1);
                break;
            case R.id.btn_test_random:
                startTestRandom();
                break;
            case R.id.btn_test_data:
                bloodSugarDeviceHandle.sendToUsb(getStateCommand(10));
                break;
            default:
                break;
        }
    }

    private void startTestRandom() {

        if (stateType > 10) {
            stateType = 1;
        }
        bloodSugarDeviceHandle.sendToUsb(getStateCommand(stateType));
        stateType++;
    }


    /**
     * 模拟命令
     */
    int stateType = 1;

    private byte[] getStateCommand(int type) {
        String dataHead = "";
        if (type == 1) {//开始测量
            dataHead = "aa600213";
        } else if (type == 2) {//过期试纸
            dataHead = "aa600207";
        } else if (type == 3) {
            dataHead = "aa600208";//试纸拨出
        } else if (type == 4) {
            dataHead = "aa60020a";//设备休眠
        } else if (type == 5) {
            dataHead = "aa60020b";//设备低电
        } else if (type == 6) {
            dataHead = "aa60020c";//环境温度过高
        } else if (type == 7) {
            dataHead = "aa60020d";//环境温度过低
        } else if (type == 8) {
            dataHead = "aa600210";//吸样不畅
        } else if (type == 9) {
            dataHead = "aa600211";//测量失败
        } else if (type == 10) {//测量成功
            dataHead = "aa600412" + "0" + String.valueOf((int) (Math.random() * 10)) + "0" + String.valueOf((int) (Math.random() * 10));
        }
        String dataStr = dataHead;
        byte[] data = StringUtil.hexStringToBytes(dataStr);
        char crc = CrcUtil.get_crc_code(data);
        byte[] data_n = new byte[data.length + 1];
        System.arraycopy(data, 0, data_n, 0, data.length);
        data_n[data_n.length - 1] = (byte) crc;
        return data_n;
    }
}
