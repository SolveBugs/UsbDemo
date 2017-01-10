package com.example.wei.usb_demo.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.AppManager;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.utils.CrcUtil;
import com.example.wei.usb_demo.utils.StringUtil;
import com.example.wei.usb_demo.usb_device.BloodOxygenDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Wei on 2016/12/26.
 */

public class BloodOxygenLineActivity extends BaseActivity {

    LineChartView _spo2LineView, _prLineView;
    private ArrayList<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private ArrayList<AxisValue> spo2AxisYValues = new ArrayList<AxisValue>();
    private ArrayList<AxisValue> prAxisYValues = new ArrayList<AxisValue>();
    private LineChartData spo2Data, prData;
    private Axis spo2AxisY, prAxisY;
    private View linView;

    private BloodOxygenDeviceHandle reader = null;
    private UsbHandle handel = null;
    private String deviceKey = "";

    private final int MIN_SPO2_VALUE = 84;      //spo2最小值
    private final int SAMPLING_FREQUENCY = 1;      //采样频率
    private final int VALUE_SHOW_TIME = 10;     //显示时长
    private final float MAX_X_VALUE = 60.0f * VALUE_SHOW_TIME * SAMPLING_FREQUENCY;
    private long startValue = 0;
    private Timer timer;
    private boolean deviceDiscerned = false;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_oxygen_line_activity);

        _spo2LineView = (LineChartView) findViewById(R.id.spo2_line);
        _spo2LineView.setViewportCalculationEnabled(false);
        _spo2LineView.setMaximumViewport(new Viewport(0.0f, 16.0f, MAX_X_VALUE, 0.0f));
        _spo2LineView.setCurrentViewport(new Viewport(0.0f, 16.0f, MAX_X_VALUE, 0.0f));
        _spo2LineView.setZoomEnabled(false);

        _prLineView = (LineChartView) findViewById(R.id.pr_line);
        _prLineView.setViewportCalculationEnabled(false);
        _prLineView.setMaximumViewport(new Viewport(0.0f, 200.0f, MAX_X_VALUE, 0.0f));
        _prLineView.setCurrentViewport(new Viewport(0.0f, 200.0f, MAX_X_VALUE, 0.0f));
        _prLineView.setZoomEnabled(false);

        linView = findViewById(R.id.line_view);

        initYAxisValues();
        resetAxisXValues();

        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        deviceDiscerned = intentData.getBoolean("USB_DEVICE_DISCERNED");
        handel = UsbHandle.ShareHandle(this);
        handel.setUSBDetachedListener(usbDetachedListener);
        reader = new BloodOxygenDeviceHandle(this, deviceKey);
        reader.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
        reader.setUsbDeviceDiscernSucessListener(AppManager.getAppManager().getMainActivity().deviceDiscernSucessListener);
        reader.setUsbDeviceDiscernTimeOutListener(listener);
        reader.start();
        if (deviceDiscerned) {//已识别的设备
            startReadData();
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("识别中");
            progressDialog.show();
        }
    }

    private void startReadData() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            final String headStr = "AA55530701";

            @Override
            public void run() {
                String spo2 = Integer.toHexString((int) (7 + Math.random() * 4));
                if (spo2.length() < 2) {
                    spo2 = 0 + spo2;
                }
                String pr = Integer.toHexString((int) (100 + Math.random() * 20));
                if (pr.length() < 2) {
                    pr = 0 + pr;
                }
                String str = headStr + spo2 + pr + "030405";
                Log.i("TAG", "run 发送数据: " + str);
                byte[] data = StringUtil.hexStringToBytes(str);
                char crc = CrcUtil.get_crc_code(data);
                byte[] data_n = new byte[data.length + 1];
                System.arraycopy(data, 0, data_n, 0, data.length);
                data_n[data_n.length - 1] = (byte) crc;
                reader.sendToUsb(data_n);
            }
        }, 1, 1000 / SAMPLING_FREQUENCY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        reader.stop();
        reader.release();
        handel.setUSBDetachedListener(null);
    }

    private void initYAxisValues() {
        String[] spo2Title = {"", "86", "88", "90", "92", "94", "96", "98", "100"};
        int value = MIN_SPO2_VALUE;
        for (String aSpo2Title : spo2Title) {
            spo2AxisYValues.add(new AxisValue(value - MIN_SPO2_VALUE).setLabel(aSpo2Title));
            value += 2;
        }
        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        spo2AxisY = new Axis();  //Y轴
        spo2AxisY.setName("SpO₂");//y轴标注
        spo2AxisY.setTextSize(10);//设置字体大小
        spo2AxisY.setHasLines(true); //x 轴分割线
        spo2AxisY.setValues(spo2AxisYValues);

        String[] prTitle = {"", "50", "100", "150", "200"};
        value = 0;
        for (String aPrTitle : prTitle) {
            prAxisYValues.add(new AxisValue(value).setLabel(aPrTitle));
            value += 50;
        }
        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
        prAxisY = new Axis();  //Y轴
        prAxisY.setName("PR");//y轴标注
        prAxisY.setTextSize(10);//设置字体大小
        prAxisY.setHasLines(true); //x 轴分割线
        prAxisY.setValues(prAxisYValues);
    }

    private void resetAxisXValues() {
        long curTimeMillis = System.currentTimeMillis();
        startValue = (curTimeMillis % 60000) / (1000 / SAMPLING_FREQUENCY);
        Date cur_date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        mAxisXValues.clear();
        for (int index = 0; index < 10; index++) {
            cur_date.setTime(curTimeMillis);
            mAxisXValues.add(new AxisValue(60 * index * SAMPLING_FREQUENCY).setLabel(format.format(cur_date)));
            curTimeMillis += 60000;
        }

        Line spo2Line = new Line().setColor(Color.GREEN);  //折线的颜色（橙色）
        List<Line> spo2Lines = new ArrayList<Line>();
        spo2Line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        spo2Line.setStrokeWidth(1);
        spo2Line.setPointRadius(0);
        spo2Line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        spo2Line.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        spo2Lines.add(spo2Line);

        spo2Data = new LineChartData();
        spo2Data.setLines(spo2Lines);
        //坐标轴
        Axis spo2AxisX = new Axis(); //X轴
        spo2AxisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        spo2AxisX.setTextColor(Color.RED);  //设置字体颜色
        spo2AxisX.setTextSize(7);//设置字体大小
        spo2AxisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        spo2AxisX.setHasLines(true); //x 轴分割线
        spo2AxisX.setTextSize(0);
        spo2Data.setAxisXBottom(spo2AxisX); //x 轴在底部

        spo2Data.setAxisYLeft(spo2AxisY);  //Y轴设置在左边

        _spo2LineView.setLineChartData(spo2Data);


        Line prLine = new Line().setColor(Color.GREEN);  //折线的颜色（橙色）
        List<Line> prLines = new ArrayList<Line>();
        prLine.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        prLine.setStrokeWidth(1);
        prLine.setPointRadius(0);
        prLine.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        prLine.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        prLines.add(prLine);

        prData = new LineChartData();
        prData.setLines(prLines);
        //坐标轴
        Axis prAxisX = new Axis(); //X轴
        prAxisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        prAxisX.setTextColor(Color.RED);  //设置字体颜色
        prAxisX.setTextSize(7);//设置字体大小
        prAxisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        prAxisX.setHasLines(true); //x 轴分割线
        prData.setAxisXBottom(prAxisX); //x 轴在底部

        prData.setAxisYLeft(prAxisY);  //Y轴设置在左边

        _prLineView.setLineChartData(prData);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) linView.getLayoutParams();
//        Log.i("TAG", "resetAxisXValues: "+startValue);
//        Log.i("TAG", "resetAxisXValues: "+_prLineView.getChartComputator().computeRawX(startValue));
        layoutParams.leftMargin = (int) _prLineView.getChartComputator().computeRawX(startValue * -1);
        linView.setLayoutParams(layoutParams);
    }

    int data_index = 0;
    private UsbDeviceHandle.USBDeviceInputDataListener usbDeviceInputDataListener = new UsbDeviceHandle.USBDeviceInputDataListener() {
        @Override
        public void onUSBDeviceInputData(byte[] data, String deviceKey) {
            String ret_str = StringUtil.bytesToHexString(data);
            Log.i("Write", "包数据：" + ret_str);

            if (!deviceDiscerned) {
                reader.usbDeviceDiscernSucessListener.onUSBDeviceInputData(UsbDeviceHandle.DeviceType.BloodOxygenDevice, deviceKey);
                deviceDiscerned = true;
                progressDialog.dismiss();
                Toast.makeText(BloodOxygenLineActivity.this, "设备连接成功", Toast.LENGTH_SHORT).show();
                startReadData();
            }

            if (data[2] == 0x53) {      //主动上传参数
                data_index++;
                long cur_x = startValue + data_index;
                if (cur_x > MAX_X_VALUE) {
                    startValue = 0;
                    data_index = 0;
                    resetAxisXValues();
                } else {
                    _spo2LineView.addLineToPoint(new PointValue(cur_x, data[5] >= 0 ? data[5] : data[5] + 256));
                    _prLineView.addLineToPoint(new PointValue(cur_x, data[6] >= 0 ? data[6] : data[6] + 256));

                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) linView.getLayoutParams();
                    layoutParams.leftMargin = (int) _prLineView.getChartComputator().computeRawX(cur_x);
                    linView.setLayoutParams(layoutParams);
                }
            }
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

    private UsbDeviceHandle.USBDeviceDiscernTimeOutListener listener = new UsbDeviceHandle.USBDeviceDiscernTimeOutListener() {
        @Override
        public void onUsbDeviceDiscerning() {
            if (!deviceDiscerned) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(BloodOxygenLineActivity.this, "识别超时", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        }
    };
}
