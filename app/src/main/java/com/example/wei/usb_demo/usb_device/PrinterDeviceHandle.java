package com.example.wei.usb_demo.usb_device;

import android.content.Context;

/**
 * Created by Wei on 2017/1/3.
 */

public class PrinterDeviceHandle extends UsbDeviceHandle {

    private static final String TAG = "TAG_PrinterDeviceHandle";

    public PrinterDeviceHandle(Context context, String deviceKey) {
        super(context, deviceKey);
    }

    @Override
    public void receiveNewData(byte[] cur_data) {

    }
}
