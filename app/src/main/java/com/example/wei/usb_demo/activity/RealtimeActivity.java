package com.example.wei.usb_demo.activity;

import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.usb_device.BloodPressureDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;
import com.example.wei.usb_demo.utils.BPDataDispatchUtils;
import com.example.wei.usb_demo.utils.CrcUtil;
import com.example.wei.usb_demo.utils.StringUtil;
import com.example.wei.usb_demo.utils.XorUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class RealtimeActivity extends BaseActivity {

    private static final String TAG = "RealtimeActivity";
    private LineChartData lineChartData;
    private LineChartView lineChartView;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private List<PointValue> points;
    private int position = 0;
    private Timer timer;
    private boolean isFinish = false;
    private Axis axisY, axisX;
    private Random random = new Random();

    private BloodPressureDeviceHandle bloodPressureDeviceHandle;
    private String deviceKey = "";
    private UsbHandle handel;
    private Handler handler = new Handler();

    BPDataDispatchUtils.IMeasureDataResultCallback iMeasureDataResultCallback = new BPDataDispatchUtils.IMeasureDataResultCallback() {

        @Override
        public void onResult(final String result) {
            // TODO Auto-generated method stub
            handler.post(new Runnable() {
                @Override
                public void run() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);

        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        bloodPressureDeviceHandle = new BloodPressureDeviceHandle(this, deviceKey);
        handel = UsbHandle.ShareHandle(this);
        handel.setUSBDetachedListener(usbDetachedListener);
        bloodPressureDeviceHandle.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
        bloodPressureDeviceHandle.setBaudRate(115200);
        bloodPressureDeviceHandle.start();

        initView();
        showChangeLineChart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("实时压力");
    }

    /**
     * 模拟实时获取数据的一个计时器
     */
    private void showChangeLineChart() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String dataHead = "aa800104010500";
                String str = "0123456789abcdef";
                int datapos11 = random.nextInt(11);
                int datapos12 = random.nextInt(16);
                String dataStr11 = str.toCharArray()[datapos11] + "";
                String dataStr12 = str.toCharArray()[datapos12] + "";
                String dataStr = dataHead + dataStr11 + dataStr12;

                byte[] data = StringUtil.hexStringToBytes(dataStr);
                int cur_len = data.length;
                byte[] content = new byte[cur_len - 2];
                System.arraycopy(data, 2, content, 0, content.length);

                byte xor = XorUtils.getXor(content);
                byte[] data_n = new byte[data.length + 1];
                System.arraycopy(data, 0, data_n, 0, data.length);
                data_n[data_n.length - 1] = xor;
                bloodPressureDeviceHandle.sendToUsb(data_n);
                Log.i(TAG, "run: " + StringUtil.bytesToHexString(data_n));
            }
        }, 300, 300);
    }

    private void initView() {
        lineChartView = (LineChartView) findViewById(R.id.show_lineChart);
        pointValueList = new ArrayList<>();
        linesList = new ArrayList<>();

        //初始化坐标轴
        axisY = new Axis();
        //添加坐标轴的名称
        axisY.setName("实时压力");
        axisY.setLineColor(Color.parseColor("#aab2bd"));
        axisY.setTextColor(Color.parseColor("#aab2bd"));
        axisX = new Axis();
        axisX.setLineColor(Color.parseColor("#aab2bd"));
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
        points = new ArrayList<>();
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
        timer.cancel();
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
            String ret_str = StringUtil.bytesToHexString(data);
            BPDataDispatchUtils.dispatch(data, iMeasureDataResultCallback);
            Log.i("Write", "包数据：" + ret_str);
        }
    };
}
