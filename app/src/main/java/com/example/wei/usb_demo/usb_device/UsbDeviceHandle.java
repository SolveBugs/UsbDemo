package com.example.wei.usb_demo.usb_device;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import com.example.wei.usb_demo.utils.StringUtil;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by Wei on 2016/12/20.
 */

public abstract class UsbDeviceHandle {

    private UsbDevice readUsbDevice = null;
    private UsbManager usbManager = null;
    private UsbInterface usbInterface = null;
    private static final String ACTION_DEVICE_PERMISSION = "com.linc.USB_PERMISSION";
    private Context _context;
    //代表一个接口的某个节点的类:写数据节点
    private UsbEndpoint usbEpIn = null;
    private UsbEndpoint usbEpOut = null;
    private UsbDeviceConnection mDeviceConnection = null;

    private boolean read = true;
    protected USBDeviceInputDataListener _usbInputDataListener = null;
    public USBDeviceDiscernSucessListener usbDeviceDiscernSucessListener = null;
    protected String deviceKey;

    final static String TAG = "USBReader";
    final static int DEFAULT_TIMEOUT = 500;

    private long baudRate = 38400;

    public UsbDeviceHandle(Context context, String deviceKey) {
        super();
        _context = context;
        this.deviceKey = deviceKey;
        usbManager = (UsbManager) _context.getSystemService(Context.USB_SERVICE);
        readUsbDevice = usbManager.getDeviceList().get(deviceKey);
    }

    public UsbDeviceHandle(Context context) {
        super();
        _context = context;
        usbManager = (UsbManager) _context.getSystemService(Context.USB_SERVICE);
        Map<String,UsbDevice> usbList = usbManager.getDeviceList();
        for (Object o : usbList.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String) entry.getKey();
            UsbDevice val = (UsbDevice) entry.getValue();
            if (discernDevice(val)) {
                this.deviceKey = key;
                readUsbDevice = val;
                break;
            }
        }
    }

    public void setBaudRate(long baudRate) {
        this.baudRate = baudRate;
    }

    public void start() {
        if (usbManager.hasPermission(readUsbDevice)) {
            this.openUSB();
            mUsbReceiver = null;
        } else {
            //申请权限
            Intent intent = new Intent(ACTION_DEVICE_PERMISSION);
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(_context, 0, intent, 0);
            IntentFilter permissionFilter = new IntentFilter(ACTION_DEVICE_PERMISSION);
            _context.registerReceiver(mUsbReceiver, permissionFilter);
            usbManager.requestPermission(readUsbDevice, mPermissionIntent);
        }
    }

    private void openUSB() {
        Log.i(TAG, "openUSB getInterfaceCount: " + readUsbDevice.getInterfaceCount());
        usbInterface = readUsbDevice.getInterface(0);
        Log.i(TAG, "openUSB getInterfaceClass: " + usbInterface.getInterfaceClass());
        Log.i(TAG, "openUSB getInterfaceProtocol: " + usbInterface.getInterfaceProtocol());
        Log.i(TAG, "openUSB getInterfaceSubclass: " + usbInterface.getInterfaceSubclass());
        for (int index = 0; index < usbInterface.getEndpointCount(); index++) {
            UsbEndpoint point = usbInterface.getEndpoint(index);
            if (point.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (point.getDirection() == UsbConstants.USB_DIR_IN) {
                    usbEpIn = point;
                } else if (point.getDirection() == UsbConstants.USB_DIR_OUT) {
                    usbEpOut = point;
                }
            }
        }
        mDeviceConnection = usbManager.openDevice(readUsbDevice);
        byte[] arrayOfByte = new byte[7];
        int i = mDeviceConnection.controlTransfer(161, 33, 0, 0, arrayOfByte, 7, 100);
        if (i < 0) {
            return;
        }
        arrayOfByte[0] = (byte) (baudRate & 255);
        arrayOfByte[1] = (byte) (baudRate >> 8 & 255);
        arrayOfByte[2] = (byte) (baudRate >> 16 & 255);
        arrayOfByte[3] = (byte) (baudRate >> 24 & 255);
        arrayOfByte[4] = 0;
        arrayOfByte[5] = 0;
        arrayOfByte[6] = 8;
        i = mDeviceConnection.controlTransfer(33, 32, 0, 0, arrayOfByte, 7, 100);
        if (i < 0) {
            return;
        }

        i = mDeviceConnection.controlTransfer(33, 35, 0, 0, (byte[]) null, 0, 100);
        if (i < 0) {
            return;
        }

        i = mDeviceConnection.controlTransfer(64, 1, 0, 0, (byte[]) null, 0, 100);
        if (i < 0) {
            return;
        }

        i = mDeviceConnection.controlTransfer(64, 1, 1, 0, (byte[]) null, 0, 100);
        if (i < 0) {
            return;
        }

        i = mDeviceConnection.controlTransfer(64, 1, 2, 68, (byte[]) null, 0, 100);
        if (i < 0) {
            return;
        }

        new Thread(new MyThread()).start();
    }

    public void stop() {
        read = false;
    }

    android.os.Handler handler = new android.os.Handler();

    private class MyThread implements Runnable {
        private UsbRequest usbRequest;
        private ByteBuffer byteBuffer;

        public MyThread() {
            usbRequest = new UsbRequest();
            int inMax = usbEpIn.getMaxPacketSize();
            byteBuffer = ByteBuffer.allocate(inMax);
        }

        public void run() {
            while (read) {
                usbRequest.initialize(mDeviceConnection, usbEpIn);
                usbRequest.queue(byteBuffer, byteBuffer.capacity());
                if (mDeviceConnection.requestWait() == usbRequest) {
                    byte[] cur_data = new byte[byteBuffer.position()];
                    System.arraycopy(byteBuffer.array(), 0, cur_data, 0, cur_data.length);
                    Log.i(TAG, "run 收到数据: " + StringUtil.bytesToHexString(cur_data));
                    receiveNewData(cur_data);
                }

            }
        }
    }

    public abstract void receiveNewData(byte[] cur_data);

    public abstract boolean discernDevice(UsbDevice device);

    public int sendToUsb(byte[] data) {
        if (!usbManager.hasPermission(readUsbDevice)) {
            return -2;      //无权限
        }
        // 发送准备命令
        int ret = mDeviceConnection.bulkTransfer(usbEpOut, data, data.length, DEFAULT_TIMEOUT);
        Log.i(TAG, "sendToUsb 发送: " + ret);
        return ret;
    }

    /**
     * usb权限相关广播
     */
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_DEVICE_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            openUSB();
                            Log.i("Handel", "onReceive: usb EXTRA_PERMISSION_GRANTED");
                        }
                    } else {
                        Log.i("Handel", "onReceive:usb EXTRA_PERMISSION_GRANTED null!!! ");
                    }
                }
            }
        }
    };

    /**
     * Interface definition for a callback to be invoked when an item in this
     * Usb读到数据了
     */
    public interface USBDeviceInputDataListener {
        void onUSBDeviceInputData(byte[] data, String usbKey);
    }

    /**
     * 设备识别成功通知
     */
    public interface USBDeviceDiscernSucessListener {
        void onUSBDeviceInputData(DeviceType type, String usbKey);
    }

    public void setUSBDeviceInputDataListener(USBDeviceInputDataListener listener) {
        _usbInputDataListener = listener;
    }

    public void setUsbDeviceDiscernSucessListener(USBDeviceDiscernSucessListener usbDeviceDiscernSucessListener) {
        this.usbDeviceDiscernSucessListener = usbDeviceDiscernSucessListener;
    }

    public void release() {
        mDeviceConnection.releaseInterface(usbInterface);
        mDeviceConnection.close();
        if (mUsbReceiver != null) {
            _context.unregisterReceiver(mUsbReceiver);
        }
        usbDeviceDiscernSucessListener = null;
    }

    /**
     * 设备类型枚举
     */
    public enum DeviceType {
        BloodOxygenDevice,      //血氧设备
        BloodPressureDevice,    //血压设备
        BloodSugarDevice;       //血糖设备
    }
}
