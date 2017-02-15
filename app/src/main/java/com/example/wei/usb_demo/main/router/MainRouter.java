package com.example.wei.usb_demo.main.router;

import android.content.Context;

import com.example.wei.usb_demo.activity.BloodOxygenHistoryActivity;
import com.example.wei.usb_demo.activity.BloodOxygenLineActivity;
import com.example.wei.usb_demo.bloodpressure.BloodPressureActivity;
import com.example.wei.usb_demo.activity.BloodSugarActivity;
import com.example.wei.usb_demo.activity.EcgHistoryActivity;
import com.example.wei.usb_demo.activity.HeartRateActivity;
import com.example.wei.usb_demo.activity.MainActivity;
import com.example.wei.usb_demo.activity.PrinterActivity;
import com.example.wei.usb_demo.activity.ReadCardActivity;
import com.example.wei.usb_demo.activity.Spo2hDataSourceReviewActivity;
import com.example.wei.usb_demo.common.module.RouterBase;

/**
 * Created by ygc on 14-10-23.
 */
public class MainRouter extends RouterBase {
    private static MainRouter singleton = null;

    public MainRouter(Context context) {
        super(context);
        // TODO init map
        maps.put(MainUI.MAIN, MainActivity.class);
        maps.put(MainUI.BLOOD_OXYGEN, BloodOxygenLineActivity.class);
        maps.put(MainUI.BLOOD_PRESS, BloodPressureActivity.class);
        maps.put(MainUI.BLOOD_SUGAR, BloodSugarActivity.class);
        maps.put(MainUI.HEART_RATE, HeartRateActivity.class);
        maps.put(MainUI.READ_CARD, ReadCardActivity.class);
        maps.put(MainUI.PRINTER, PrinterActivity.class);
        maps.put(MainUI.BLOOD_OXYGEN_HISTORY, BloodOxygenHistoryActivity.class);
        maps.put(MainUI.HEART_RATE_HISTORY, EcgHistoryActivity.class);
        maps.put(MainUI.BLOOD_OXYGEN_REVIEW, Spo2hDataSourceReviewActivity.class);
    }

    public static MainRouter getInstance(Context context) {
        synchronized (MainRouter.class) {
            if (singleton == null) {
                singleton = new MainRouter(context);
            }
        }
        return singleton;
    }
}
