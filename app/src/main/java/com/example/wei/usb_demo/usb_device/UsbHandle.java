package com.example.wei.usb_demo.usb_device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Wei on 2016/12/19.
 */

public class UsbHandle extends BroadcastReceiver {

    protected UsbManager usbManager;
    protected UsbDevice usbDevice;
    protected UsbInterface usbInterface = null;
    protected UsbEndpoint usbEndpoint = null;
    protected UsbDeviceConnection usbDeviceConnection = null;

    private USBDeviceChangeListener usbDeviceChangeListener;
    private USBDetachedListener usbDetachedListener;
    private Map<String, UsbDevice>_deviceList = new HashMap<>();
    private Context _context;

    private static UsbHandle self;

    /**
     * @brief 单例方法
     * @param context
     * @return
     */
    public static UsbHandle ShareHandle(Context context) {
        if (self == null) {
            synchronized (UsbHandle.class) {
                if (self == null) {
                    self = new UsbHandle(context);
                }
            }
        }
        return self;
    }

    /**
     * @brief 构造方法
     * @param context
     */
    public UsbHandle(Context context) {
        super();
        _context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (action.equalsIgnoreCase("android.hardware.usb.action.USB_DEVICE_ATTACHED")) {
            Log.i("Handel", "onReceive: "+"插入设备");
        } else if (action.equalsIgnoreCase("android.hardware.usb.action.USB_DEVICE_DETACHED")) {
            Log.i("Handel", "onReceive: "+"拔出设备");
            usbDetachedListener.onUSBDetached(device);
        }
        _deviceList = usbManager.getDeviceList();
        usbDeviceChangeListener.onUSBDeviceChanged(_deviceList);
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * Usb发生变化
     */
    public interface USBDeviceChangeListener {
        void onUSBDeviceChanged(Map<String, UsbDevice>deviceList);
    }

    public void setUsbDeviceChangeListener(USBDeviceChangeListener listener) {
        usbDeviceChangeListener = listener;

        _deviceList = usbManager.getDeviceList();
        usbDeviceChangeListener.onUSBDeviceChanged(_deviceList);
    }

    public interface USBDetachedListener {
        void onUSBDetached(UsbDevice device);
    }

    public void setUSBDetachedListener(USBDetachedListener listener) {
        usbDetachedListener = listener;
    }
}
