package com.example.wei.usb_demo.main.router;

import android.content.Context;

import com.example.wei.usb_demo.activity.BloodOxygenLineActivity;
import com.example.wei.usb_demo.activity.BloodPressureActivity;
import com.example.wei.usb_demo.activity.BloodSugarActivity;
import com.example.wei.usb_demo.activity.HeartRateActivity;
import com.example.wei.usb_demo.activity.MainActivity;
import com.example.wei.usb_demo.activity.PrinterActivity;
import com.example.wei.usb_demo.activity.ReadCardActivity;
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
