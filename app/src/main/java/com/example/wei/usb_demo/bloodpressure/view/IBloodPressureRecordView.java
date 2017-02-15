package com.example.wei.usb_demo.bloodpressure.view;

import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;

import java.util.ArrayList;

/**
 * Created by zhenqiang on 2017/2/15.
 */

public interface IBloodPressureRecordView {
    void showNodata();

    void showLoading();

    void stopLoading();

    void notifiData(ArrayList<ModelBloodPressure> data);
}
