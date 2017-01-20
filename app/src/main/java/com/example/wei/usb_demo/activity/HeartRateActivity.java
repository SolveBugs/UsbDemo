package com.example.wei.usb_demo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.app.AppContext;
import com.example.wei.usb_demo.common.utils.UIHelper;
import com.example.wei.usb_demo.utils.file.EcgDataSource;
import com.example.wei.usb_demo.utils.file.EcgFile;
import com.mhealth365.osdk.EcgOpenApiCallback;
import com.mhealth365.osdk.EcgOpenApiHelper;
import com.mhealth365.osdk.ecgbrowser.EcgBrowserInteractive;
import com.mhealth365.osdk.ecgbrowser.RealTimeEcgBrowser;
import com.mhealth365.osdk.ecgbrowser.Scale;

import java.io.File;
import java.io.IOException;

import static com.mhealth365.osdk.EcgOpenApiCallback.EcgConstant.ECG_BATTERY;
import static com.mhealth365.osdk.EcgOpenApiCallback.EcgConstant.ECG_HEART;
import static com.mhealth365.osdk.EcgOpenApiCallback.EcgConstant.ECG_RR;

public class HeartRateActivity extends BaseActivity {

    private static final String TAG = "HeartRateActivity";

    private Button mButtonRecordStart, mButtonRecordStop;
    private EcgOpenApiHelper mOsdkHelper;
    private AlertDialog mRecordTimeDialog;
    public final String[] items = { "30秒", "60秒", "10分钟", "30分钟", "60分钟" };
    private boolean isUsbPlugIn = false;
    private TextView hr, rr, result, mDeviceStatusTV, counter;
    private int ecgSample = 0;
    private int countEcg = 0;
    private RealTimeEcgBrowser mEcgBrowser;
    private EcgDataSource demoData = null;
    private String showDataFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        mEcgBrowser = (RealTimeEcgBrowser) findViewById(R.id.ecgBrowser);
        mEcgBrowser.setEcgBrowserInteractive(mEcgBrowserInteractive);
        mButtonRecordStart = (Button) findViewById(R.id.button_record_start);
        mButtonRecordStop = (Button) findViewById(R.id.button_record_stop);

        mButtonRecordStart.setOnClickListener(btnClickListener);
        mButtonRecordStop.setOnClickListener(btnClickListener);

        mRecordTimeDialog = new AlertDialog.Builder(this)
                .setTitle("选择测量时间")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EcgOpenApiHelper.RECORD_MODE mode = EcgOpenApiHelper.RECORD_MODE.RECORD_MODE_3600;
                        switch (which) {
                            case 0:
                                mode = EcgOpenApiHelper.RECORD_MODE.RECORD_MODE_30;
                                break;
                            case 1:
                                mode = EcgOpenApiHelper.RECORD_MODE.RECORD_MODE_60;
                                break;
                            case 2:
                                mode = EcgOpenApiHelper.RECORD_MODE.RECORD_MODE_600;
                                break;
                            case 3:
                                mode = EcgOpenApiHelper.RECORD_MODE.RECORD_MODE_1800;
                                break;
                            case 4:
                                mode = EcgOpenApiHelper.RECORD_MODE.RECORD_MODE_3600;
                                break;
                        }
                        startRecord(mode);
                        mRecordTimeDialog.dismiss();
                    }
                }).create();

        initLable();
        initSdk();
        initEcg();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
    }

    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.button_record_start:
                    if (TextUtils.isEmpty(mOsdkHelper.getDeviceSN())) {
                        UIHelper.ToastMessage(HeartRateActivity.this, "请先连接设备");
                        return;
                    }
                    if (mOsdkHelper.isRunningRecord()) {
                        UIHelper.ToastMessage(HeartRateActivity.this, "正在记录中，请等待记录完成后再操作");
                        return;
                    }
                    mRecordTimeDialog.show();
                    break;
                case R.id.button_record_stop:
                    if (mOsdkHelper.isRunningRecord()) {
                        try {
                            UIHelper.ToastMessage(HeartRateActivity.this, "【停止记录】");
                            mOsdkHelper.stopRecord();
                        } catch (IOException e) {
                            e.printStackTrace();
                            UIHelper.ToastMessage(HeartRateActivity.this, "【关闭记录】文件异常,开始记录失败！");
                        }
                    } else {
                        UIHelper.ToastMessage(HeartRateActivity.this, "没有开始记录！");
                    }
                    break;
            }
        }
    };

    public void initLable() {
        hr = (TextView) findViewById(R.id.label_heartrate_realtime);
        rr = (TextView) findViewById(R.id.label_rr_value);

        result = (TextView) findViewById(R.id.label_result);
        counter = (TextView) findViewById(R.id.label_counter);

        mDeviceStatusTV = (TextView) findViewById(R.id.label_device_value);
    }

    public void initSdk() {
        mOsdkHelper = EcgOpenApiHelper.getInstance();
        mOsdkHelper.setDeviceType(EcgOpenApiHelper.DEVICE.CONNECT_TYPE_BLUETOOTH_DUAL);
        mOsdkHelper.setDeviceType(EcgOpenApiHelper.DEVICE.CONNECT_TYPE_USB);
        EcgOpenApiHelper.getInstance().login("1", mLoginCallback);
        AppContext.getApp().setOsdkCallback(mOsdkCallback);
    }

    private void init() {
        mOsdkHelper.notifyUSBDeviceAttach();
        if (mOsdkHelper.isDeviceReady()) {
            setEcgSample(mOsdkHelper.getEcgSample());
        }
    }

    public void setEcgSample(int sample) {
        this.ecgSample = sample;
        mEcgBrowser.setSample(sample);
    }

    public void initEcg() {
        mEcgBrowser.setSpeedAndGain(Scale.SPEED_25MM_S, Scale.GAIN_10MM_MV);// 设置增益和走速
        mEcgBrowser.setSample(500);
        mEcgBrowser.showFps(true);
        mEcgBrowser.setScreenDPI(mEcgBrowser.getDisplayDPI());
        mEcgBrowser.clearEcg();
    }

    EcgBrowserInteractive mEcgBrowserInteractive = new EcgBrowserInteractive() {

        @Override
        public void onChangeGainAndSpeed(int gain, int speed) {
            displayMessage.obtainMessage(ECG_GAIN_SPEED, gain, speed).sendToTarget();
        }
    };
    
    EcgOpenApiCallback.OsdkCallback mOsdkCallback = new EcgOpenApiCallback.OsdkCallback() {

        @Override
        public void devicePlugIn() {
            isUsbPlugIn = true;
            UIHelper.ToastMessage(HeartRateActivity.this, "设备插入！");
            mDeviceStatusTV.setText("设备插入");
        }

        @Override
        public void devicePlugOut() {
            isUsbPlugIn = false;
            UIHelper.ToastMessage(HeartRateActivity.this, "设备拔出！");
            mDeviceStatusTV.setText("设备拔出");
        }

        @Override
        public void deviceSocketConnect() {
            UIHelper.ToastMessage(HeartRateActivity.this, "设备已连接！");
            mDeviceStatusTV.setText("已连接");
        }

        @Override
        public void deviceSocketLost() {
            UIHelper.ToastMessage(HeartRateActivity.this, "设备连接断开！");
            mDeviceStatusTV.setText("已断开");
        }

        @Override
        public void deviceReady(int sample) {
            UIHelper.ToastMessage(HeartRateActivity.this, "心电设备已准备好！");
            mDeviceStatusTV.setText("准备就绪,设备号:" + mOsdkHelper.getDeviceSN());
            ecgSample = sample;
            mEcgBrowser.setSample(sample);
        }

        @Override
        public void deviceNotReady(int msg) {
            switch (msg) {
                case EcgOpenApiCallback.DEVICE_NOT_READY_NOT_SUPPORT_DEVICE:// sdk不支持设备
                    UIHelper.ToastMessage(HeartRateActivity.this, "当前sdk设备无法使用此型号设备");// sdk不支持型号
                    mDeviceStatusTV.setText("型号不支持");
                    break;
                case EcgOpenApiCallback.DEVICE_NOT_READY_UNKNOWN_DEVICE:// 未知设备
                    UIHelper.ToastMessage(HeartRateActivity.this, "设备无法使用");// 设备故障或者非熙健产品
                    mDeviceStatusTV.setText("无法使用");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 开始记录
     */
    public void startRecord(EcgOpenApiHelper.RECORD_MODE mode) {
        try {
            result.setText("");
            countEcg = 0;
            mOsdkHelper.startRecord(mode, mRecordCallback);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            UIHelper.ToastMessage(HeartRateActivity.this, "【开始记录】文件异常,开始记录失败！");
        } catch (Exception e) {
            e.printStackTrace();
            UIHelper.ToastMessage(HeartRateActivity.this, "【开始记录】文件异常,开始记录失败！");
        }
    }

    public float getDisplayDPI(float inch, int width, int height) {
        /** 对角线像素数 */
        float len = (float) Math.sqrt(width * width + height * height);
        /** 通过对角线直接计算的密度 */
        float DPI = len / inch;
        return DPI;
    }

    static final int ECG_GAIN_SPEED = 10001;
    static final int TOAST_TEXT = 10002;
    static final int CPU_STATE = 10003;
    static final int DEBUG_STATE = 10004;
    static final int LIB_NAME = 10005;
    static final int ECG_COUNTER = 10006;
    static final int ECG_SHOW_DATA = 10007;
    static final int ECG_STAISTICS_RESULT = 10008;
    static final int ECG_ACC = 10009;

    /** 显示刷新 */
    Handler displayMessage = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case ECG_HEART:
                    int hrValue = msg.arg1;
                    if (hrValue >= 1 && hrValue <= 355) {
                        hr.setText("" + hrValue);
                    } else {
                        hr.setText("---");
                    }
                    break;
                case ECG_RR:
                    if (msg.arg1 >= 10000) {
                        rr.setText("---");
                    } else {
                        rr.setText("" + msg.arg1);
                    }
                    break;
                case ECG_COUNTER:
                    counter.setText(msg.arg1 + "");
                    break;
                case ECG_STAISTICS_RESULT:
                    String text = (String) msg.obj;
                    if (text != null)
                        result.setText(text);
                    break;
                case TOAST_TEXT:
                    String t = (String) msg.obj;
                    if (t != null)
                        Toast.makeText(getBaseContext(), t, Toast.LENGTH_SHORT).show();
                    break;
                case ECG_SHOW_DATA:
                    showDataFile = (String) msg.obj;
                    if (showDataFile != null) {
                        showDialog(0);
                    }
                    mEcgBrowser.clearEcg();
                    clearValue();
                    break;
                default:
                    break;
            }
        }
    };

    public void clearValue() {
        hr.setText("---");
        rr.setText("---");
    }

    EcgOpenApiCallback.RecordCallback mRecordCallback = new EcgOpenApiCallback.RecordCallback() {

        @Override
        public void recordTime(int second) {
            Log.w(getClass().getSimpleName(), "recordTime--- second=" + second);
            displayMessage.obtainMessage(ECG_COUNTER, second, -1).sendToTarget();
        }

        @Override
        public void recordStatistics(String id, int averageHeartRate, int normalRange, int suspectedRisk) {
            if (null != id) {
                // FIXME 节律异常范围，修改为节律正常范围
                String msg = "平均心率：" + averageHeartRate + "(bpm),心率正常范围：" + normalRange + "%" + ",节律正常范围：" + suspectedRisk + "%";
                displayMessage.obtainMessage(ECG_STAISTICS_RESULT, msg).sendToTarget();
                UIHelper.ToastMessage(HeartRateActivity.this, "统计分析完成");
            } else {
                String msg = "【统计数据异常】";// 一般是数据文件错误引起
                displayMessage.obtainMessage(ECG_STAISTICS_RESULT, msg).sendToTarget();
            }
        }

        @Override
        public void recordStart(String id) {
            Log.w(getClass().getSimpleName(), "recordStart--- id=" + id);
            Log.w(getClass().getSimpleName(), "recordStart--- countEcg=" + countEcg);
            try {
                demoData = new EcgDataSource(System.currentTimeMillis(), ecgSample);
            } catch (Exception e) {
                e.printStackTrace();
                UIHelper.ToastMessage(HeartRateActivity.this, "创建记录失败，ecgSample：" + ecgSample);
            }
        }

        @Override
        public void recordEnd(String id) {
            if (id == null) {
                UIHelper.ToastMessage(HeartRateActivity.this, "关闭记录，未生成有效数据");
            } else {
                UIHelper.ToastMessage(HeartRateActivity.this, "记录结束，开始统计分析");
            }
            Log.w(getClass().getSimpleName(), "recordEnd--- id=" + id);
            if (demoData != null) {
                String rootDir = getFileRoot();
                File file = new File(rootDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String filename = rootDir + System.currentTimeMillis() + ".ecg";
                File demoFile = new File(filename);
                if (demoFile.exists()) {
                    demoFile.delete();
                }
                try {
                    Log.w(getClass().getSimpleName(), "recordEnd--- demoData:" + demoData.toString());
                    boolean ok = EcgFile.write(demoFile, demoData);
                    if (ok) {
                        displayMessage.obtainMessage(ECG_SHOW_DATA, filename).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                demoData = null;
            }
        }

        @Override
        public void heartRate(int hr) {
            Log.w(getClass().getSimpleName(), "heartRate--- hr=" + hr);
            displayMessage.obtainMessage(ECG_HEART, hr, -1).sendToTarget();
        }

        @Override
        public void ecg(int[] value) {
            countEcg++;
            Log.w(getClass().getSimpleName(), "ecg--- " + value[0]);
            mEcgBrowser.ecgPackage(value);
            if (demoData != null)
                demoData.addPackage(value);
        }

        @Override
        public void RR(int ms) {
            Log.v(getClass().getSimpleName(), "RR--- rr=" + ms);
            displayMessage.obtainMessage(ECG_RR, ms, -1).sendToTarget();
        }

        @Override
        public void startFailed(EcgOpenApiCallback.RECORD_FAIL_MSG msg) {
            Log.e(getClass().getSimpleName(), "startFailed--- " + msg.name());
            String text = "";
            switch (msg) {
                case RECORD_FAIL_A_RECORD_RUNNING:
                    text = "已经开始记录了";
                    break;
                case RECORD_FAIL_DEVICE_NO_RESPOND:
                    text = "设备没有响应";// 设备没有响应控制指令，可以重试
                    break;
                case RECORD_FAIL_DEVICE_NOT_READY:
                    text = "设备没有准备好";// 设备未插入，或者未被识别
                    break;
                case RECORD_FAIL_NOT_LOGIN:
                    text = "还没有登陆";
                    break;
                case RECORD_FAIL_OSDK_INIT_ERROR:
                    text = "osdk没有初始化";
                    break;
                case RECORD_FAIL_PARAMETER:
                    text = "参数错误";
                    break;
                case RECORD_FAIL_LOW_VERSION:
                    text = "开发者验证失败,版本低,需要升级sdk";
                    break;
                case RECORD_FAIL_VALIDATE_SDK_FAILED_PACKAGE_NAME_MISMATCH:
                    text = "开发者验证失败,包名不匹配";
                    break;
                case RECORD_FAIL_VALIDATE_SDK_FAILED_ACCOUNT_FROZEN:
                    text = "开发者验证失败,账户冻结";
                    break;
                case RECORD_FAIL_VALIDATE_SDK_FAILED_NETWORK_UNAVAILABLE:
                    text = "开发者验证失败,没有网络";
                    break;
                case RECORD_FAIL_VALIDATE_SDK_FAILED:
                    text = "开发者验证失败";
                    break;
                default:
                    break;
            }
            UIHelper.ToastMessage(HeartRateActivity.this, "开始记录失败：" + text);
        }

        @Override
        public void battery(int value) {
            displayMessage.obtainMessage(ECG_BATTERY, value, -1).sendToTarget();
        }

        @Override
        public void addAccelerate(short x, short y, short z) {
            displayMessage.obtainMessage(ECG_ACC, "x:" + x + " y:" + y + " z:" + z).sendToTarget();
        }

        @Override
        public void addAccelerateVector(float arg0) {
        }
    };

    private String getFileRoot() {
        String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EcgSdkDemo/";
        return rootDir;
    }

    EcgOpenApiCallback.LoginCallback mLoginCallback = new EcgOpenApiCallback.LoginCallback() {

        @Override
        public void loginOk() {
//            UIHelper.ToastMessage(HeartRateActivity.this, "登录成功");
        }

        @Override
        public void loginFailed(EcgOpenApiCallback.LOGIN_FAIL_MSG msg) {
            String text = "";
            if (msg == null) {
                text = "未知异常";
            } else {
                switch (msg) {
                    case LOGIN_FAIL_NO_NET:
                        text = "无网络";
                        break;
                    case LOGIN_FAIL_NO_OPENID:
                        text = "OpenId为空值";
                        break;
                    case LOGIN_FAIL_NO_RESPOND:
                        text = "服务器未响应";
                        break;
                    case LOGIN_FAIL_NO_USER:
                        text = "无此用户";
                        break;
                    case LOGIN_FAIL_OSDK_INIT_ERROR:
                        text = "sdk初始化异常";
                        break;
                    case LOGIN_FAIL_UNAUTHORIZED:
                        text = "未授权";
                        break;
                    case LOGIN_FAIL_ACCOUNT_FROZEN:
                        text = "账户冻结";
                        break;
                    case LOGIN_FAIL_PACKAGE_NAME_MISMATCH:
                        text = "包名不匹配";
                        break;
                    // 20150716----------------------------
                    case SYS_0:
                        text = "系统错误";// 系统错误
                        break;
                    case SYS_USER_EXIST_E:
                        text = "注册用户已回收";// Openid存在，但是账号已回收
                        break;
                    case SYS_THIRD_PARTY_ID_CHECKING:
                        text = "公司id审核中";// thiredpartyId存在，正在审核未生效
                        break;
                    case SYS_THIRD_PARTY_ID_NOT_EXIST:
                        text = "公司id不存在";// thiredpartyId不存在
                        break;
                    case SYS_APP_ID_CHECKING:
                        text = "appid审核中";// appid存在，正在审核未生效
                        break;
                    case SYS_APP_ID_ERROR:
                        // text ="appid不存在，或者appSecret有错误";//appid不存在，或者appSecret有错误
                        text = "包名 appId 公司id 不匹配";// TODO 包名 appId 公司id 不匹配
                        break;
                    case SYS_APP_PACKAGE_ID_NOT_EXIST:
                        text = "包名不存在";// 包名不正确
                        break;
                    case SYS_LOW_VERSION:
                        text = "sdk版本低需要升级";
                        break;
                    default:
                        break;
                }
                Log.v("Login", "loginFailed:" + msg.name());
            }
            UIHelper.ToastMessage(HeartRateActivity.this, "登录失败 " + text);
        }
    };
}
