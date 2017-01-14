package com.example.wei.usb_demo.common.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;


public final class UIHelper {

    /**
     * @param msg
     */
    static Toast toast1 = null;
    static Toast toast = null;

    private UIHelper() {
    }

    public static void ToastMessage(Context cont, String msg) {
        if (cont == null) return;
        if (TextUtils.isEmpty(msg)) return;
        if (toast1 == null) {
            toast1 = Toast.makeText(cont, msg, Toast.LENGTH_LONG);
        } else {
            toast1.setText(msg);
        }
        toast1.show();
    }

    public static void ToastMessage(Context cont, int msg) {
        if (cont == null) return;
        if (msg == 0) return;
        if (toast == null) {
            toast = Toast.makeText(cont, cont.getResources().getString(msg), Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    public static void ToastMessage(Context cont, int msg, int time) {
        Toast.makeText(cont, msg, time).show();
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        Toast.makeText(cont, msg, time).show();
    }

}
