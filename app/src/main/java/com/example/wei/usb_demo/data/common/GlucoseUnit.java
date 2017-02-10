package com.example.wei.usb_demo.data.common;

import android.content.Context;

import com.example.wei.pl2303_test.R;


/**
 * Created by ZhouYuzhen on 14/10/31.
 */
public enum GlucoseUnit {
    GLUCOSE_UNIT_MOLE("mmol/L", 0, R.string.unit_glucose_mole),
    GLUCOSE_UNIT_MG("mg/dL", 1, R.string.unit_glucose_mg);

    private String name;
    private int id;
    private int resId;

    private GlucoseUnit(String name, int id, int resId) {
        this.name = name;
        this.id = id;
        this.resId = resId;
    }

    public static GlucoseUnit getGlucoseUnitById(int id) {
        if (GLUCOSE_UNIT_MG.getId() == id) {
            return GLUCOSE_UNIT_MG;
        }
        return GLUCOSE_UNIT_MOLE;
    }

    public static GlucoseUnit getGlucoseUnitByName(String name) {
        if (GLUCOSE_UNIT_MG.getName().equals(name)) {
            return GLUCOSE_UNIT_MG;
        }
        return GLUCOSE_UNIT_MOLE;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getResId() {
        return resId;
    }

    public String getResString(Context context) {
        return context.getResources().getString(resId);
    }
}
