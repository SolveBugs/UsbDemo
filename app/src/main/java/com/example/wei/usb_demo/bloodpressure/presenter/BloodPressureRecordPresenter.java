package com.example.wei.usb_demo.bloodpressure.presenter;

import android.content.Context;

import com.example.wei.usb_demo.bloodpressure.view.IBloodPressureRecordView;
import com.example.wei.usb_demo.data.db.DataDBM;
import com.example.wei.usb_demo.data.db.bean.ModelBloodPressure;

import java.util.ArrayList;

/**
 * Created by zhenqiang on 2017/2/15.
 */

public class BloodPressureRecordPresenter {
    private IBloodPressureRecordView view;

    public BloodPressureRecordPresenter(IBloodPressureRecordView view) {
        this.view = view;
    }

    public void load(Context context) {
        view.showLoading();
        ArrayList<ModelBloodPressure> allModelBloodPressure = DataDBM.getInstance(context).getAllModelBloodPressure();
        if (allModelBloodPressure == null || allModelBloodPressure.size() == 0) {
            view.stopLoading();
            view.showNodata();
        } else {
            view.notifiData(allModelBloodPressure);
            view.stopLoading();
        }
    }
}
