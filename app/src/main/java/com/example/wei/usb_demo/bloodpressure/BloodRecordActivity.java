package com.example.wei.usb_demo.bloodpressure;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.activity.base.BaseActivity;
import com.example.wei.usb_demo.data.db.DataDBM;
import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class BloodRecordActivity extends BaseActivity implements View.OnClickListener {

    /*=========== 控件相关 ==========*/
    private LineChartView mLineChartView;               //线性图表控件

    /*=========== 数据相关 ==========*/
    private LineChartData mLineData;                    //图表数据
    private int numberOfLines = 3;                      //图上折线/曲线的显示条数
    private int maxNumberOfLines = 4;                   //图上折线/曲线的最多条数
    private int numberOfPoints = 5;                    //图上的节点数

    /*=========== 状态相关 ==========*/
    private boolean isHasAxes = true;                   //是否显示坐标轴
    private boolean isHasAxesNames = false;              //是否显示坐标轴名称
    private boolean isHasLines = true;                  //是否显示折线/曲线
    private boolean isHasPoints = true;                 //是否显示线上的节点
    private boolean isFilled = false;                   //是否填充线下方区域
    private boolean isHasPointsLabels = false;          //是否显示节点上的标签信息
    private boolean isCubic = false;                    //是否是立体的
    private boolean isPointsHasSelected = false;        //设置节点点击后效果(消失/显示标签)
    private boolean isPointsHaveDifferentColor;         //节点是否有不同的颜色

    /*=========== 其他相关 ==========*/
    private ValueShape pointsShape = ValueShape.CIRCLE; //点的形状(圆/方/菱形)
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints]; //将线上的点放在一个数组中

    public static final int COLOR_0e6841 = Color.parseColor("#0e6841");
    public static final int COLOR_efe03f = Color.parseColor("#efe03f");
    public static final int COLOR_7924e2 = Color.parseColor("#7924e2");
    public static final int[] COLORS = {COLOR_0e6841, COLOR_efe03f, COLOR_7924e2};


    private TextView tvOneDaysData, tvSevenDaysData, tvMonthDaysData, tvCustomDaysData;
    private TextView tvDate;

    private static int currentType = 1;
    private PopupWindow popupWindow;

    private ArrayList<ModelBloodPressure> bloodPressureArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_record);
        initView();
        initData();
        initListener();
//        mToolBarHelper.setTvRight("历史记录", new ToolBarHelper.onTextViewClickListener() {
//            @Override
//            public void onClick() {
//                showHistoryPopwindow();
//            }
//        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle("血压报告");
    }

    private void initListener() {
        //节点点击事件监听
        mLineChartView.setOnValueTouchListener(new ValueTouchListener());
    }


    private void initView() {
        mLineChartView = (LineChartView) findViewById(R.id.lvc_main);
        /**
         * 禁用视图重新计算 主要用于图表在变化时动态更改，不是重新计算
         * 类似于ListView中数据变化时，只需notifyDataSetChanged()，而不用重新setAdapter()
         */
        mLineChartView.setViewportCalculationEnabled(false);
        tvDate = (TextView) findViewById(R.id.tv_date);
    }

    private void initData() {
        setPointsValues();          //设置每条线的节点值
        setLinesDatas();            //设置每条线的一些属性
        resetViewport();            //计算并绘图
    }


    /**
     * 利用随机数设置每条线对应节点的值
     */
    private void setPointsValues() {
        randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 300f;
            }
        }
        bloodPressureArrayList = DataDBM.getInstance(this).getAllModelBloodPressure();
    }

    /**
     * 设置线的相关数据
     */
    private void setLinesDatas() {
        List<Line> lines = new ArrayList<>();
        //循环将每条线都设置成对应的属性
        for (int i = 0; i < numberOfLines; ++i) {
            //节点的值
            List<PointValue> values = new ArrayList<>();
            for (int j = 0; j < bloodPressureArrayList.size(); ++j) {
                int y = 0;
                if (i == 0) {
                    y = bloodPressureArrayList.get(j).getDiastolic();
                } else if (i == 1) {
                    y = bloodPressureArrayList.get(j).getPulse();
                } else {
                    y = bloodPressureArrayList.get(j).getSystolic();
                }
                values.add(new PointValue(j % bloodPressureArrayList.size(), y));
            }

            /*========== 设置线的一些属性 ==========*/
            Line line = new Line(values);               //根据值来创建一条线
            line.setColor(COLORS[i]);        //设置线的颜色
            line.setShape(pointsShape);                 //设置点的形状
            line.setPointRadius(3);                     //设置点的大小
            line.setHasLines(isHasLines);               //设置是否显示线
            line.setHasPoints(isHasPoints);             //设置是否显示节点
            line.setCubic(isCubic);                     //设置线是否立体或其他效果
            line.setFilled(isFilled);                   //设置是否填充线下方区域
            line.setHasLabels(isHasPointsLabels);       //设置是否显示节点标签
            line.setStrokeWidth(1);                     //设置线的粗细
            //设置节点点击的效果
            line.setHasLabelsOnlyForSelected(isPointsHasSelected);
            //如果节点与线有不同颜色 则设置不同颜色
            if (isPointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        mLineData = new LineChartData(lines);                      //将所有的线加入线数据类中
        mLineData.setBaseValue(Float.NEGATIVE_INFINITY);           //设置基准数(大概是数据范围)

        //如果显示坐标轴
        if (isHasAxes) {
            Axis axisX = new Axis();                    //X轴
            Axis axisY = new Axis().setHasLines(true);  //Y轴
            axisX.setTextColor(Color.GRAY);             //X轴灰色
            axisX.setValues(getAxisValuesX(currentType));
            axisY.setTextColor(Color.GRAY);             //Y轴灰色
            //setLineColor()：此方法是设置图表的网格线颜色 并不是轴本身颜色
            //如果显示名称
            if (isHasAxesNames) {
                axisX.setName("Axis X");                //设置名称
                axisY.setName("Axis Y");
            }

            mLineData.setAxisXBottom(axisX);            //设置X轴位置 下方
            mLineData.setAxisYLeft(axisY);              //设置Y轴位置 左边
        } else {
            mLineData.setAxisXBottom(null);
            mLineData.setAxisYLeft(null);
        }

        mLineChartView.setLineChartData(mLineData);    //设置图表控件
    }

    /**
     * 重点方法，计算绘制图表
     */
    private void resetViewport() {
        //创建一个图标视图 大小为控件的最大大小
        final Viewport v = new Viewport(mLineChartView.getMaximumViewport());
        v.left = 0;                             //坐标原点在左下
        v.bottom = 0;
        v.top = 305;                            //最高点为300
        v.right = numberOfPoints - 0.5f;           //右边为点 坐标从0开始 点号从1 需要 -1
        mLineChartView.setMaximumViewport(v);   //给最大的视图设置 相当于原图

        final Viewport v2 = new Viewport(mLineChartView.getMaximumViewport());
        v2.left = 0;
        v2.bottom = 0;
        v2.top = 305;
        v2.right = 5;
        mLineChartView.setCurrentViewport(v2);   //给当前的视图设置 相当于当前展示的图
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_one_days_data:
                tvDate.setText("最近一天");
                currentType = 1;
                numberOfPoints = 5;

                break;
            case R.id.tv_seven_days_data:
                tvDate.setText("最近七天");
                currentType = 2;
                numberOfPoints = 7;
                break;
            case R.id.tv_month_days_data:
                tvDate.setText("最近一月");
                currentType = 3;
                numberOfPoints = 30;
                break;
            case R.id.tv_custom_days_data:
                break;
            default:
                break;
        }
        popupWindow.dismiss();
        initData();
    }


    /**
     * 节点触摸监听
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(BloodRecordActivity.this, "选中第 " + (pointIndex + 1) + " 个节点", Toast.LENGTH_SHORT).show();

            ModelBloodPressure modelBloodPressure = bloodPressureArrayList.get(pointIndex);
            Intent intent = new Intent(BloodRecordActivity.this, DialogActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("data", modelBloodPressure);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onValueDeselected() {

        }
    }

    public ArrayList<AxisValue> getAxisValuesX(int type) {
        ArrayList<AxisValue> axisValues = new ArrayList<>();
        if (type == 1) {
            for (int i = 0; i < 5; i++) {
                AxisValue axisValue = new AxisValue(i).setLabel((6 * i) + "时");
                axisValues.add(axisValue);
            }

        } else if (type == 2) {
            for (int i = 0; i < 7; i++) {
                AxisValue axisValue = new AxisValue(i).setLabel(i + "天");
                axisValues.add(axisValue);
            }
        } else if (type == 3) {
            for (int i = 0; i < 30; i++) {
                AxisValue axisValue = new AxisValue(i).setLabel((i + 1) + "天");
                axisValues.add(axisValue);
            }
        }
        return axisValues;
    }

    private void showHistoryPopwindow() {
        popupWindow = new PopupWindow(BloodRecordActivity.this);
        View popwindowView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.history_popwindow_layout, null);
        tvOneDaysData = (TextView) popwindowView.findViewById(R.id.tv_one_days_data);
        tvSevenDaysData = (TextView) popwindowView.findViewById(R.id.tv_seven_days_data);
        tvMonthDaysData = (TextView) popwindowView.findViewById(R.id.tv_month_days_data);
        tvCustomDaysData = (TextView) popwindowView.findViewById(R.id.tv_custom_days_data);

        tvOneDaysData.setOnClickListener(this);
        tvSevenDaysData.setOnClickListener(this);
        tvMonthDaysData.setOnClickListener(this);
        tvCustomDaysData.setOnClickListener(this);

        popupWindow.setContentView(popwindowView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setWidth(480);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int xpos = manager.getDefaultDisplay().getWidth() - popupWindow.getWidth() / 2;
        popupWindow.showAsDropDown(toolbar, xpos, 0);
    }
}
