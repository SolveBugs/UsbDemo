package com.example.wei.usb_demo.data.common;

import android.content.Context;

import com.example.wei.usb_demo.common.config.AppConfig;
import com.example.wei.usb_demo.data.TimePoint;

import java.util.Locale;

public final class DataCommon {
    public final static float MIN_VAlUE = 1.1f;
    public final static float MAX_VALUE = 33.34f;

    public final static float VALUES_1 = 30.0f;
    public final static float VALUES_2 = 15.0f;
    public final static float VALUES_3 = 11.0f;
    public final static float VALUES_4 = 9.0f;
    public final static float VALUES_5 = 8.0f;
    public final static float VALUES_6 = 7.0f;
    public final static float VALUES_7 = 6.0f;
    public final static float VALUES_8 = 5.0f;
    public final static float VALUES_9 = 4.0f;
    public final static float VALUES_10 = 2.5f;
    public final static float VALUES_0 = 0;

    public final static float MG_RATIO = 18f;

    private DataCommon() {

    }

    public static String formatDataValue(Context context, float value) {
        if (Float.compare(value, 0) == 0) {
            return "";
        } else if (Float.compare(value, DataCommon.MIN_VAlUE) < 0) {
            return "LOW";
        } else if (Float.compare(value, DataCommon.MAX_VALUE) > 0) {
            return "HIGH";
        }

        return formatDataValueNoHL(context.getApplicationContext(), value);
    }

    public static String formatDataValueZero(Context context, float value) {
        if (Float.compare(value, 0) == 0) {
            return "0";
        } else if (Float.compare(value, DataCommon.MIN_VAlUE) < 0) {
            return "LOW";
        } else if (Float.compare(value, DataCommon.MAX_VALUE) > 0) {
            return "HIGH";
        }

        return formatDataValueNoHL(context.getApplicationContext(), value);
    }

    public static String formatDataValueNoHL(Context context, float value) {
        GlucoseUnit unit = getDataUnit(context);
        switch (unit) {
            case GLUCOSE_UNIT_MG:
                return String.format(Locale.US, "%d", Math.round(value * MG_RATIO));
            default:
                return String.format(Locale.US, "%.1f", value);
        }
    }

    public static String getTypeString(Context context, TimePoint timePoint) {
        return timePoint.getResString(context);
    }

    public static GlucoseUnit getDataUnit(Context context) {
        AppConfig appConfig = AppConfig.getInstance(context.getApplicationContext());
        GlucoseUnit unit = appConfig.getGlucoseUnit();
        return unit;
    }

    public static float convertToMMOLValue(Context context, float value) {
        GlucoseUnit unit = getDataUnit(context.getApplicationContext());
        switch (unit) {
            case GLUCOSE_UNIT_MG: {
                float saveValue = value / MG_RATIO;
                if (Float.compare(saveValue, MAX_VALUE) > 0) {
                    saveValue = MAX_VALUE + 1;
                } else if (Float.compare(saveValue, MIN_VAlUE) < 0) {
                    saveValue = MIN_VAlUE - 1;
                }
                return saveValue;
            }
            default:
                return value;
        }
    }

}
