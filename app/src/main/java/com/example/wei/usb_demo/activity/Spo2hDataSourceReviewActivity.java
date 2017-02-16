package com.example.wei.usb_demo.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.data.db.bean.BloodOxygenModel;

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
 * Created by Wei on 2017/2/14.
 */

public class Spo2hDataSourceReviewActivity extends BaseActivity {

    private static final String TAG = "TAG_Spo2hDataSourceReviewActivity";

    private BloodOxygenModel dataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spo2h_data_source_review);

        final Bundle intentData = getIntent().getExtras();
        dataModel = intentData.getParcelable("data_model");
        LineChartView spo2LineView, prLineView;
        ArrayList<AxisValue> mAxisXValues = new ArrayList<>();
        ArrayList<AxisValue> spo2AxisYValues = new ArrayList<>();
        ArrayList<AxisValue> prAxisYValues = new ArrayList<>();
        LineChartData spo2Data, prData;
        Axis spo2AxisY, prAxisY;

        final int MIN_SPO2_VALUE = 84;      //spo2最小值
//        final int SAMPLING_FREQUENCY = 1;      //采样频率
//        final int VALUE_SHOW_TIME = 10;     //显示时长
//        final float MAX_X_VALUE = 60.0f * VALUE_SHOW_TIME * SAMPLING_FREQUENCY;

        spo2LineView = (LineChartView) findViewById(R.id.spo2_line);
        spo2LineView.setViewportCalculationEnabled(false);
        spo2LineView.setZoomEnabled(false);

        prLineView = (LineChartView) findViewById(R.id.pr_line);
        prLineView.setViewportCalculationEnabled(false);
        prLineView.setZoomEnabled(false);

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

        List<PointValue> spo2Points = new ArrayList<>();
        List<PointValue> prPoints = new ArrayList<>();
        String[] strArr = dataModel.getStrDataArray();
        long dataStartTime = dataModel.getDataTime();
        long axisXstart = dataStartTime - (dataStartTime % 60);
        long max = dataStartTime - axisXstart;
        dataStartTime *= 1000;
        Date cur_date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        if (max > 0) {
            cur_date.setTime(axisXstart*1000);
            mAxisXValues.add(new AxisValue(0).setLabel(format.format(cur_date)));
        }
        for (String strLine: strArr) {
            String[] points = strLine.split(",");
            spo2Points.add(new PointValue(max, Integer.parseInt(points[0])-MIN_SPO2_VALUE));
            prPoints.add(new PointValue(max, Integer.parseInt(points[1])));
            if (max % 60 == 0) {
                dataStartTime += 60000;
                cur_date.setTime(dataStartTime);
                mAxisXValues.add(new AxisValue(max).setLabel(format.format(cur_date)));
            }
            max ++;
        }

        for (int index = mAxisXValues.size(); index < 10; index++) {
            cur_date.setTime(axisXstart*1000 + (60000*index));
            mAxisXValues.add(new AxisValue(index*60).setLabel(format.format(cur_date)));
        }

        Line spo2Line = new Line(spo2Points).setColor(Color.GREEN);  //折线的颜色（橙色）
        List<Line> spo2Lines = new ArrayList<>();
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


        Line prLine = new Line(prPoints).setColor(Color.GREEN);  //折线的颜色（橙色）
        List<Line> prLines = new ArrayList<>();
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

        spo2LineView.setMaximumViewport(new Viewport(0.0f, 16.0f, max>600?max:600, 0.0f));
        spo2LineView.setCurrentViewport(new Viewport(0.0f, 16.0f, 600, 0.0f));
        prLineView.setMaximumViewport(new Viewport(0.0f, 200.0f, max>600?max:600, 0.0f));
        prLineView.setCurrentViewport(new Viewport(0.0f, 200.0f, 600, 0.0f));
        spo2LineView.setLineChartData(spo2Data);
        prLineView.setLineChartData(prData);
    }
}
