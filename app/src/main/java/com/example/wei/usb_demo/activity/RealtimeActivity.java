package com.example.wei.usb_demo.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.AppManager;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.usb_device.BloodPressureDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;
import com.example.wei.usb_demo.utils.BPDataDispatchUtils;
import com.example.wei.usb_demo.utils.StringUtil;
import com.example.wei.usb_demo.utils.XorUtils;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class RealtimeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RealtimeActivity";
    private LineChartData lineChartData;
    private LineChartView lineChartView;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private int position = 0;
    private Axis axisY, axisX;

    private BloodPressureDeviceHandle bloodPressureDeviceHandle;
    private String deviceKey = "";
    private UsbHandle handel;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;
    private ArrayList<AxisValue> axisValuesX;

    private Button btnConnct, btnStartTest, btnStopTest, btnShutdown;

    BPDataDispatchUtils.IMeasureDataResultCallback iMeasureDataResultCallback = new BPDataDispatchUtils.IMeasureDataResultCallback() {

        @Override
        public void onResult(final String result) {
            // TODO Auto-generated method stub
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RealtimeActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onPressure(final int pressure) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //实时添加新的点
                    PointValue value1 = new PointValue(position * 5, pressure);
                    value1.setLabel("00:00");
                    pointValueList.add(value1);

                    AxisValue axisValue = new AxisValue(position * 5).setLabel(position * 5 + "");
                    axisValuesX.add(axisValue);
                    axisX.setValues(axisValuesX);
                    float x = value1.getX();
                    //根据新的点的集合画出新的线
                    Line line = new Line(pointValueList);
                    line.setColor(Color.RED);
                    line.setShape(ValueShape.CIRCLE);
                    line.setCubic(true);//曲线是否平滑，即是曲线还是折线

                    linesList.clear();
                    linesList.add(line);
                    lineChartData = initDatas(linesList);
                    lineChartView.setLineChartData(lineChartData);
                    //根据点的横坐实时变幻坐标的视图范围
                    Viewport port;
                    if (x > 50) {
                        port = initViewPort(x - 50, x);
                    } else {
                        port = initViewPort(0, 50);
                    }
                    lineChartView.setCurrentViewport(port);//当前窗口

                    Viewport maPort = initMaxViewPort(x);
                    lineChartView.setMaximumViewport(maPort);//最大窗口
                    position++;
                }
            });
        }

        @Override
        public void onData(int[] datas) {

        }
    };
    private boolean usbDeviceDiscerned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);

        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        usbDeviceDiscerned = intentData.getBoolean("USB_DEVICE_DISCERNED");
        bloodPressureDeviceHandle = new BloodPressureDeviceHandle(this, deviceKey);
        handel = UsbHandle.ShareHandle(this);
        handel.setUSBDetachedListener(usbDetachedListener);
        bloodPressureDeviceHandle.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
        bloodPressureDeviceHandle.setBaudRate(115200);
        bloodPressureDeviceHandle.setUsbDeviceDiscernSucessListener(AppManager.getAppManager().getMainActivity().deviceDiscernSucessListener);
        bloodPressureDeviceHandle.setUsbDeviceDiscernTimeOutListener(listener);
        bloodPressureDeviceHandle.setHandShakePackeData(getHandshakeCommand());
        bloodPressureDeviceHandle.setChipType(UsbDeviceHandle.ChipType.CH340);
        bloodPressureDeviceHandle.start();

        initView();

        if (usbDeviceDiscerned) {
        } else {
            progressDialog = new ProgressDialog(RealtimeActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("识别中");
            progressDialog.show();
        }
    }

    private byte[] getHandshakeCommand() {
        String dataHead = "cc800303010100";//连接血压计命令
        String dataStr = dataHead;

        byte[] data = StringUtil.hexStringToBytes(dataStr);
        int cur_len = data.length;
        byte[] content = new byte[cur_len - 2];
        System.arraycopy(data, 2, content, 0, content.length);

        byte xor = XorUtils.getXor(content);
        byte[] data_n = new byte[data.length + 1];
        System.arraycopy(data, 0, data_n, 0, data.length);
        data_n[data_n.length - 1] = xor;
        return data_n;
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("实时压力");
    }

    private void initView() {
        lineChartView = (LineChartView) findViewById(R.id.show_lineChart);
        pointValueList = new ArrayList<>();
        linesList = new ArrayList<>();
        axisValuesX = new ArrayList<>();
        //初始化坐标轴
        axisY = new Axis();
        //添加坐标轴的名称
        axisY.setName("实时压力");
        axisY.setLineColor(Color.parseColor("#aab2bd"));
        axisY.setTextColor(Color.parseColor("#aab2bd"));
        axisX = new Axis();
        axisX.setLineColor(Color.parseColor("#aab2bd"));
        axisX.setAutoGenerated(false);
        lineChartData = initDatas(null);
        lineChartView.setLineChartData(lineChartData);

        Viewport port = initViewPort(0, 50);
        lineChartView.setCurrentViewportWithAnimation(port);
        lineChartView.setInteractive(false);
        lineChartView.setScrollEnabled(true);
        lineChartView.setValueTouchEnabled(true);
        lineChartView.setFocusableInTouchMode(true);
        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.startDataAnimation();


        btnConnct = (Button) findViewById(R.id.btn_connct);
        btnStartTest = (Button) findViewById(R.id.btn_start_test);
        btnStopTest = (Button) findViewById(R.id.btn_stop_test);
        btnShutdown = (Button) findViewById(R.id.btn_shut_down);

        btnConnct.setOnClickListener(this);
        btnStartTest.setOnClickListener(this);
        btnStopTest.setOnClickListener(this);
        btnShutdown.setOnClickListener(this);
    }

    private LineChartData initDatas(List<Line> lines) {
        LineChartData data = new LineChartData(lines);
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        return data;
    }

    private Viewport initViewPort(float left, float right) {
        Viewport port = new Viewport();
        port.top = 185;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }

    private Viewport initMaxViewPort(float right) {
        Viewport port = new Viewport();
        port.top = 185;
        port.bottom = 0;
        port.left = 0;
        port.right = right + 50;
        return port;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bloodPressureDeviceHandle.stop();
        bloodPressureDeviceHandle.release();
        handel.setUSBDetachedListener(null);
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

    private UsbDeviceHandle.USBDeviceInputDataListener usbDeviceInputDataListener = new UsbDeviceHandle.USBDeviceInputDataListener() {
        @Override
        public void onUSBDeviceInputData(byte[] data, String deviceKey) {

            if (!usbDeviceDiscerned) {
                usbDeviceDiscerned = true;
                bloodPressureDeviceHandle.usbDeviceDiscernSucessListener.onUSBDeviceInputData(UsbDeviceHandle.DeviceType.BloodPressureDevice, deviceKey);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            Toast.makeText(RealtimeActivity.this, "识别成功", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            String ret_str = StringUtil.bytesToHexString(data);
            BPDataDispatchUtils.dispatch(data, iMeasureDataResultCallback);
            Log.i("Write", "包数据：" + ret_str);
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
                            Toast.makeText(RealtimeActivity.this, "识别超时", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connct:
                bloodPressureDeviceHandle.sendToUsb(getHandshakeCommand());
                break;
            case R.id.btn_start_test:
                bloodPressureDeviceHandle.sendToUsb(getCommand(1));
                break;
            case R.id.btn_stop_test:
                bloodPressureDeviceHandle.sendToUsb(getCommand(2));
                break;
            case R.id.btn_shut_down:
                bloodPressureDeviceHandle.sendToUsb(getCommand(3));
                break;
            default:
                break;
        }
    }

    private byte[] getCommand(int type) {
        String dataHead = "";
        if (type == 1) {
            dataHead = "cc800303010200";//启动测量
        } else if (type == 2) {
            dataHead = "cc800303010300";//停止测量
        } else if (type == 3) {
            dataHead = "cc800303010400";//关机
        }
        String dataStr = dataHead;

        byte[] data = StringUtil.hexStringToBytes(dataStr);
        int cur_len = data.length;
        byte[] content = new byte[cur_len - 2];
        System.arraycopy(data, 2, content, 0, content.length);

        byte xor = XorUtils.getXor(content);
        byte[] data_n = new byte[data.length + 1];
        System.arraycopy(data, 0, data_n, 0, data.length);
        data_n[data_n.length - 1] = xor;
        return data_n;
    }
}
