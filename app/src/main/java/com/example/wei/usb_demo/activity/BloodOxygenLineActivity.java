package com.example.wei.usb_demo.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.activity.base.ToolBarHelper;
import com.example.wei.usb_demo.data.db.bean.BloodOxygenModel;
import com.example.wei.usb_demo.usb_device.BloodOxygenDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbDeviceHandle;
import com.example.wei.usb_demo.usb_device.UsbHandle;
import com.example.wei.usb_demo.utils.CrcUtil;
import com.example.wei.usb_demo.utils.IDGenerator;
import com.example.wei.usb_demo.utils.ImageUtils;
import com.example.wei.usb_demo.utils.StringUtil;
import com.example.wei.usb_demo.utils.Utils;
import com.example.wei.usb_demo.utils.file.Spo2hFile;
import com.example.wei.usb_demo.utils.printer_utils.myprinter.Global;
import com.example.wei.usb_demo.utils.printer_utils.myprinter.WorkService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    LineChartView _spo2LineView, _prLineView, _waveLineView;
    private ArrayList<AxisValue> mAxisXValues = new ArrayList<>();
    private ArrayList<AxisValue> spo2AxisYValues = new ArrayList<>();
    private ArrayList<AxisValue> prAxisYValues = new ArrayList<>();
    private LineChartData spo2Data, prData, waveData;
    private Axis spo2AxisY, prAxisY, waveAxisY;
    private View linView;

    private BloodOxygenDeviceHandle reader = null;
    private UsbHandle handel = null;
    private String deviceKey = null;

    private final int MIN_SPO2_VALUE = 84;      //spo2最小值
    private final int SAMPLING_FREQUENCY = 1;      //采样频率
    private final int VALUE_SHOW_TIME = 10;     //显示时长
    private final int MIN_WAVE_Y_VALUE = 0;
    private final int MAX_WAVE_Y_VALUE = 127;
    private final int WAVE_X_MILLISECONDS = 10000;
    private final float MAX_X_VALUE = 60.0f * VALUE_SHOW_TIME * SAMPLING_FREQUENCY;
    private long startValue = 0;
//    private boolean deviceDiscerned = false;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();

    private int _pr, _spo2, _pi;
    private TextView prView, spo2View, piView;
    private Button send_data;

    private BloodOxygenModel boModel = null;

    private long minMilliseconds = 0, curMilliseconds = 0;

    private static final String TAG = "BloodOxygenLineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blood_oxygen_line_activity);

        this.mToolBarHelper.setTvRight("打印", new ToolBarHelper.onTextViewClickListener() {
            @Override
            public void onClick() {
                Bundle data = new Bundle();
                Bitmap mBitmap = ImageUtils.getViewBitmap(getWindow().findViewById(Window.ID_ANDROID_CONTENT));
                data.putParcelable(Global.PARCE1, mBitmap);
                data.putInt(Global.INTPARA1, 384);
                data.putInt(Global.INTPARA2, 0);
                WorkService.workThread.handleCmd(
                        Global.CMD_POS_PRINTPICTURE, data);
            }
        });

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

        _waveLineView = (LineChartView) findViewById(R.id.wave_line);
        _waveLineView.setViewportCalculationEnabled(false);
        _waveLineView.setMaximumViewport(new Viewport(0, MAX_WAVE_Y_VALUE, WAVE_X_MILLISECONDS, MIN_WAVE_Y_VALUE));
        _waveLineView.setCurrentViewport(new Viewport(0, MAX_WAVE_Y_VALUE, WAVE_X_MILLISECONDS, MIN_WAVE_Y_VALUE));
        _waveLineView.setZoomEnabled(false);

        prView = (TextView) findViewById(R.id.pr_value);
        spo2View = (TextView) findViewById(R.id.spo2_value);
        piView = (TextView) findViewById(R.id.pi_value);

        linView = findViewById(R.id.line_view);

        send_data = (Button) findViewById(R.id.send_data);
        send_data.setOnClickListener(btnOnClickListener);

        initYAxisValues();
        resetAxisXValues();
        initWaveLineView();

        final Bundle intentData = getIntent().getExtras();
        deviceKey = intentData.getString("USB_DEVICE_KEY");
        handel = UsbHandle.ShareHandle(this);
        handel.setUSBDetachedListener(usbDetachedListener);
        if (deviceKey != null) {//已识别的设备
            reader = new BloodOxygenDeviceHandle(this, deviceKey);
            reader.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
            reader.setUsbDeviceDiscernFalseListener(listener);
            reader.start();
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("识别中");
            progressDialog.show();
            reader = new BloodOxygenDeviceHandle(this);
            reader.setUSBDeviceInputDataListener(usbDeviceInputDataListener);
            reader.setUsbDeviceDiscernFalseListener(listener);
            reader.startDiscernDevice();
        }
    }

//    private void startReadData() {
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            final String headStr = "AA55530701";
//
//            @Override
//            public void run() {
//                String spo2 = Integer.toHexString((int) (7 + Math.random() * 4));
//                if (spo2.length() < 2) {
//                    spo2 = 0 + spo2;
//                }
//                String pr = Integer.toHexString((int) (100 + Math.random() * 20));
//                if (pr.length() < 2) {
//                    pr = 0 + pr;
//                }
//                String str = headStr + spo2 + pr + "030405";
//                Log.i("TAG", "run 发送数据: " + str);
//                byte[] data = StringUtil.hexStringToBytes(str);
//                char crc = CrcUtil.get_crc_code(data);
//                byte[] data_n = new byte[data.length + 1];
//                System.arraycopy(data, 0, data_n, 0, data.length);
//                data_n[data_n.length - 1] = (byte) crc;
//                reader.sendToUsb(data_n);
//            }
//        }, 1, 1000 / SAMPLING_FREQUENCY);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reader.stop();
        reader.release();
        handel.setUSBDetachedListener(null);
        if (boModel != null && boModel.getSporhData().size() > 0) {
            Spo2hFile.writeData(new File(Utils.getSDCardPath()+"/mdm_data/Spo2h/"+boModel.getDataFileName()), boModel);
        }
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

        ArrayList<AxisValue> waveAxisYValues = new ArrayList<>();
        waveAxisYValues.add(new AxisValue(MIN_WAVE_Y_VALUE));
        waveAxisYValues.add(new AxisValue(MAX_WAVE_Y_VALUE));
        waveAxisY = new Axis(waveAxisYValues);
        waveAxisY.setTextSize(0);
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

    private void initWaveLineView() {
        Line waveLine = new Line().setColor(Color.GREEN);  //折线的颜色（橙色）
        List<Line> waveLines = new ArrayList<>();
        waveLine.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        waveLine.setStrokeWidth(1);
        waveLine.setPointRadius(0);
        waveLine.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        waveLine.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        waveLines.add(waveLine);

        ArrayList<AxisValue> waveAxisXValues = new ArrayList<>();
        waveAxisXValues.add(new AxisValue(0));
        waveAxisXValues.add(new AxisValue(WAVE_X_MILLISECONDS));

        waveData = new LineChartData();
        waveData.setLines(waveLines);
        //坐标轴
        Axis waveAxisX = new Axis(); //X轴
        waveAxisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        waveAxisX.setTextColor(Color.RED);  //设置字体颜色
        waveAxisX.setTextSize(0);//设置字体大小
        waveAxisX.setValues(waveAxisXValues);  //填充X轴的坐标名称
        waveAxisX.setHasLines(false); //x 轴分割线
        waveData.setAxisXBottom(waveAxisX); //x 轴在底部

        waveData.setAxisYLeft(waveAxisY);  //Y轴设置在左边

        _waveLineView.setLineChartData(waveData);
    }

    /**
     * 清除掉心跳折线图的所有点，圆点毫秒数置为当前
     */
    private void resetWaveData() {
        minMilliseconds = System.currentTimeMillis();
        _waveLineView.clearPoints(-1);
    }

    int data_index = 0;
    private UsbDeviceHandle.USBDeviceInputDataListener usbDeviceInputDataListener = new UsbDeviceHandle.USBDeviceInputDataListener() {
        @Override
        public void onUSBDeviceInputData(byte[] data, String deviceKey) {
//            String ret_str = StringUtil.bytesToHexString(data);
//            Log.i("Write", "包数据：" + ret_str);

            if (data[2] == 0x53) {      //主动上传参数
                if (boModel == null) {
                    boModel = new BloodOxygenModel();
                    boModel.setDataFileName(IDGenerator.newIdWithTag("BO")+".dat");
                }
                boModel.appendData(data);
                data_index++;
                long cur_x = startValue + data_index;
                if (cur_x > MAX_X_VALUE) {
                    startValue = 0;
                    data_index = 0;
                    resetAxisXValues();
                    cur_x = 0;
                }
                _spo2 = (data[5] >= 0 ? data[5] : data[5] + 256);
                int pr_l = data[6] >= 0 ? data[6] : data[6] + 256;
                int pr_h = data[7] >= 0 ? data[7] : data[7] + 256;
                _pr = pr_l + pr_h;
                _pi = data[8] >= 0 ? data[8] : data[8] + 256;
                _spo2LineView.addLineToPoint(new PointValue(cur_x, _spo2 - MIN_SPO2_VALUE));
                _prLineView.addLineToPoint(new PointValue(cur_x, _pr));

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) linView.getLayoutParams();
                layoutParams.leftMargin = (int) _prLineView.getChartComputator().computeRawX(cur_x);
                linView.setLayoutParams(layoutParams);

                updateValueLabel();
            } else if (data[2] == 0x52) {
                curMilliseconds = System.currentTimeMillis();
                long x = curMilliseconds - minMilliseconds;
                if (minMilliseconds == 0 || x > WAVE_X_MILLISECONDS) {
                    resetWaveData();
                    x = curMilliseconds - minMilliseconds;
                }
                int y = data[5]<0?data[5]+256:data[5];
                y = y & 0x7F;
                _waveLineView.addLineToPoint(new PointValue(x, y));
            }
        }
    };

    private void updateValueLabel() {
        prView.setText("PR："+_pr);
        spo2View.setText("SpO₂："+_spo2);
        piView.setText("PI："+_pi/10.0f);
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

    private UsbDeviceHandle.USBDeviceDiscernFalseListener listener = new UsbDeviceHandle.USBDeviceDiscernFalseListener() {
        @Override
        public void onUsbDeviceDiscerning() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(BloodOxygenLineActivity.this, "识别失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        }
    };

    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int btn_id = v.getId();
            byte[] data = null;
            if (btn_id == R.id.send_data) {
                data = StringUtil.hexStringToBytes("AA5550030201");
            }
            char crc = CrcUtil.get_crc_code(data);
            byte[] data_n = new byte[data.length + 1];
            System.arraycopy(data, 0, data_n, 0, data.length);
            data_n[data_n.length - 1] = (byte) crc;
            reader.sendToUsb(data_n);
        }
    };

    @Override
    protected void onDeviceDiscernFinish(int type, String usbKey, int state) {
        super.onDeviceDiscernFinish(type, usbKey, state);

        progressDialog.dismiss();
        Toast.makeText(BloodOxygenLineActivity.this, "设备连接成功", Toast.LENGTH_SHORT).show();
    }
}
