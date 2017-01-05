package com.example.wei.usb_demo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class HeartRateActivity extends BaseActivity {

    private static final String TAG = "HeartRateActivity";

    private LineChartView _heartRateLineView;
    private ArrayList<AxisValue> axisYValues = new ArrayList<AxisValue>();
    private ArrayList<AxisValue> axisXValues = new ArrayList<AxisValue>();
    private LineChartData lineChartData;
    private Axis axisY, axisX;

    final static int MAX_X_VALUE = 1000;
    final static int MAX_Y_VALUE = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        _heartRateLineView = (LineChartView) findViewById(R.id.heart_rate);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) _heartRateLineView.getLayoutParams();
        int height = layoutParams.height;
        int width = layoutParams.width;

        int right = MAX_Y_VALUE * width / height;

        _heartRateLineView.setViewportCalculationEnabled(false);
        _heartRateLineView.setMaximumViewport(new Viewport(0.0f, MAX_Y_VALUE, MAX_X_VALUE, 0.0f));
        _heartRateLineView.setCurrentViewport(new Viewport(0.0f, MAX_Y_VALUE, right, 0.0f));
        _heartRateLineView.setZoomEnabled(false);

        for (int index = 0; index < MAX_Y_VALUE; index += 2) {
            axisYValues.add(new AxisValue(index).setLabel(""+index));
        }
        for (int index = 0; index < MAX_X_VALUE; index += 2) {
            axisXValues.add(new AxisValue(index).setLabel(""));
        }

//        Line spo2Line = new Line().setColor(Color.GREEN);  //折线的颜色（橙色）
//        List<Line> spo2Lines = new ArrayList<Line>();
//        spo2Line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
//        spo2Line.setStrokeWidth(1);
//        spo2Line.setPointRadius(0);
//        spo2Line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
//        spo2Line.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
//        spo2Lines.add(spo2Line);

        lineChartData = new LineChartData();
//        lineChartData.setLines(spo2Lines);
        //坐标轴
        axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.RED);  //设置字体颜色
        axisX.setTextSize(7);//设置字体大小
        axisX.setValues(axisXValues);  //填充X轴的坐标名称
        axisX.setHasLines(true); //x 轴分割线
        axisX.setTextSize(0);
        lineChartData.setAxisXBottom(axisX); //x 轴在底部

        axisY = new Axis();  //Y轴
//        axisY.setName("SpO₂");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        axisY.setHasLines(true); //x 轴分割线
        axisY.setValues(axisYValues);
        lineChartData.setAxisYLeft(axisY);  //Y轴设置在左边

        _heartRateLineView.setLineChartData(lineChartData);
    }
}
