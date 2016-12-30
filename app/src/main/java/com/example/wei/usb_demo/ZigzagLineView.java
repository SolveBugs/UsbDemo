package com.example.wei.usb_demo;

import android.content.Context;
import android.view.View;

/**
 * Created by Wei on 2016/12/26.
 */

public class ZigzagLineView extends View {

    private float minX = 0, maxX = 100;     //X坐标区间
    private float minY = 0, maxY = 100;     //Y坐标区间

    public ZigzagLineView(Context context) {
        super(context);
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }
}
