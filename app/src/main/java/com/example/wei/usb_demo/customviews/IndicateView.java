package com.example.wei.usb_demo.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;

/**
 * Created by zhenqiang on 2017/1/11.
 */

public class IndicateView extends LinearLayout {

    private static final String TAG = "IndicateView";
    private Context context;

    private int bgRes;
    private int iconRes;
    private String text;
    private View view;

    public IndicateView(Context context) {
        super(context);
    }

    public IndicateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        view = LayoutInflater.from(context).inflate(R.layout.indicate_view_layout, this);
        initAttrs(context, attrs);
        init(context);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.IndicateView, 0, 0);

        bgRes = typeArray.getResourceId(R.styleable.IndicateView_indicateBg, 0);
        iconRes = typeArray.getResourceId(R.styleable.IndicateView_indicateIcon, 0);
        text = typeArray.getString(R.styleable.IndicateView_indicateText);
        typeArray.recycle();
    }

    private void init(Context context) {
        Button indicate_bg = (Button) view.findViewById(R.id.indicate_bg);
        Button indicate_icon = (Button) view.findViewById(R.id.indicate_icon);
        TextView indicate_text = (TextView) view.findViewById(R.id.indicate_text);

        indicate_bg.setBackgroundResource(bgRes);
        indicate_icon.setBackgroundResource(iconRes);
        indicate_text.setText(text);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
