/*
 * Copyright (c) 2014. Beijing Dnurse Technology Ltd. All rights reserved.
 */

package com.example.wei.usb_demo.data.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.wei.pl2303_test.R;

import java.util.Locale;

public class CircleLoadingProgressView extends FrameLayout {
    private Animation loadingAnimation;
    private View animView;
    private TextView countDownView;

    public CircleLoadingProgressView(Context context) {
        super(context);
        init(context);
    }

    public CircleLoadingProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLoadingProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.data_circle_loading_progress_view, this);

        animView = findViewById(R.id.progress_background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            animView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        loadingAnimation = AnimationUtils.loadAnimation(context, R.anim.test_progress);
        countDownView = (TextView) findViewById(R.id.data_test_result_count_down);
    }

    public void start() {
        animView.setAnimation(loadingAnimation);
        animView.setVisibility(View.VISIBLE);
    }

    public void stop() {
        animView.setAnimation(null);
        animView.setVisibility(View.GONE);
    }

    public void setProgress(int progress) {
        String text = String.format(Locale.US, "%d", progress);
        countDownView.setText(text);
    }
}
