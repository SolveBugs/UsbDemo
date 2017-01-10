package com.example.wei.usb_demo.usb_device;

import android.content.Context;
import android.hardware.usb.UsbDevice;

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
        _usbInputDataListener.onUSBDeviceInputData(cur_data, deviceKey);
    }

    @Override
    public byte[] getHandshakePacketData() {
        return new byte[0];
    }

    @Override
    public boolean discernDevice(UsbDevice device) {
        return false;
    }

    @Override
    public byte[] getHandshakePacketData() {
        return new byte[0];
    }
}
