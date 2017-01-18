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
import android.widget.Toast;

import com.example.wei.usb_demo.utils.StringUtil;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by Wei on 2016/12/20.
 */

public abstract class UsbDeviceHandle {

    protected UsbDevice readUsbDevice = null;
    protected UsbManager usbManager = null;
    private UsbInterface usbInterface = null;
    private static final String ACTION_DEVICE_PERMISSION = "com.linc.USB_PERMISSION";
    protected Context _context;
    //代表一个接口的某个节点的类:写数据节点
    private UsbEndpoint usbEpIn = null;
    private UsbEndpoint usbEpOut = null;
    private UsbDeviceConnection mDeviceConnection = null;

    private boolean read = true;
    protected USBDeviceInputDataListener _usbInputDataListener = null;
    public USBDeviceDiscernFalseListener usbDeviceDiscernFalseListener = null;
    public String deviceKey;
    public boolean isConnected = false;
    protected boolean connectTimeOut = false;

    final static String TAG = "USBReader";
    final static int DEFAULT_TIMEOUT = 500;

    public final static String ACTION_DEVICE_DISCERN_FINISH_NOTIFY = "ACTION_DEVICE_DISCERN_FINISH_NOTIFY";
    public final static String K_DEVICE_DISCERN_FINISH_KEY = "K_DEVICE_DISCERN_FINISH_KEY";
    public final static String K_DEVICE_DISCERN_FINISH_STATE = "K_DEVICE_DISCERN_FINISH_STATE";
    public final static String K_DEVICE_DISCERN_FINISH_TYPE = "K_DEVICE_DISCERN_FINISH_TYPE";

    private long baudRate = 38400;

    private ChipType chipType = ChipType.PL2303;

    public UsbDeviceHandle(Context context, String deviceKey) {
        super();
        _context = context;
        this.deviceKey = deviceKey;
        usbManager = (UsbManager) _context.getSystemService(Context.USB_SERVICE);
        readUsbDevice = usbManager.getDeviceList().get(deviceKey);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (readUsbDevice == null) {
                            usbDeviceDiscernFalseListener.onUsbDeviceDiscerning();
                            mUsbReceiver = null;
                        }
                    }
                });
            }
        }).start();
    }

    public UsbDeviceHandle(Context context) {
        super();
        _context = context;
        usbManager = (UsbManager) _context.getSystemService(Context.USB_SERVICE);
        Map<String, UsbDevice> usbList = usbManager.getDeviceList();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (readUsbDevice == null) {
                            usbDeviceDiscernFalseListener.onUsbDeviceDiscerning();
                            mUsbReceiver = null;
                        }
                    }
                });
            }
        }).start();
    }

    public UsbDeviceHandle() {
        super();
    }

    public void setBaudRate(long baudRate) {
        this.baudRate = baudRate;
    }

    public void start() {
        connectTimeOut = false;
        read = true;
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


        if (chipType == ChipType.PL2303) {//pl2303芯片
            byte[] arrayOfByte = new byte[7];
            int i = mDeviceConnection.controlTransfer(161, 33, 0, 0, arrayOfByte, 7, 100);
            if (i < 0) {
                isConnected = false;
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
                isConnected = false;
                return;
            }

            i = mDeviceConnection.controlTransfer(33, 35, 0, 0, (byte[]) null, 0, 100);
            if (i < 0) {
                isConnected = false;
                return;
            }

            i = mDeviceConnection.controlTransfer(64, 1, 0, 0, (byte[]) null, 0, 100);
            if (i < 0) {
                isConnected = false;
                return;
            }

            i = mDeviceConnection.controlTransfer(64, 1, 1, 0, (byte[]) null, 0, 100);
            if (i < 0) {
                isConnected = false;
                return;
            }

            i = mDeviceConnection.controlTransfer(64, 1, 2, 68, (byte[]) null, 0, 100);
            if (i < 0) {
                isConnected = false;
                return;
            }
        } else if (chipType == ChipType.CH340) {//ch340芯片

            if (!UartInit()) {//初始化串口
                Toast.makeText(_context, "Init Uart Error",
                        Toast.LENGTH_SHORT).show();
            } else {//配置串口
                if (SetConfig(baudRate, (byte) 8, (byte) 1,
                        (byte) 0, (byte) 0)) {
                    Log.e(TAG, "Uart Configed");
                }
            }
        }


        new Thread(new MyThread()).start();

        byte[] handshakePacket = getHandshakePacketData();
        sendToUsb(handshakePacket);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: 线程启动");
                try {
                    Thread.sleep(3000);//3s后没有识别则为超时
                    connectTimeOut = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
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

    public void startDiscernDevice() {
        return;
    }

    public abstract byte[] getHandshakePacketData();

    public int sendToUsb(byte[] data) {
        if (!usbManager.hasPermission(readUsbDevice)) {
            return -2;      //无权限
        }
        // 发送准备命令
        int ret = mDeviceConnection.bulkTransfer(usbEpOut, data, data.length, DEFAULT_TIMEOUT);
        Log.i(TAG, "sendToUsb 发送: " + ret);
        return ret;
    }

    public void setChipType(ChipType chipType) {
        this.chipType = chipType;
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
     * 发送握手包后开始计时是否超时
     */
    public interface USBDeviceDiscernFalseListener {
        void onUsbDeviceDiscerning();
    }

    public void setUSBDeviceInputDataListener(USBDeviceInputDataListener listener) {
        _usbInputDataListener = listener;
    }

    public void setUsbDeviceDiscernFalseListener(USBDeviceDiscernFalseListener usbDeviceDiscernFalseListener) {
        this.usbDeviceDiscernFalseListener = usbDeviceDiscernFalseListener;
    }

    public void release() {
        if (mDeviceConnection != null) {
            mDeviceConnection.releaseInterface(usbInterface);
            mDeviceConnection.close();
        }
        if (mUsbReceiver != null) {
            try {
                _context.unregisterReceiver(mUsbReceiver);
            } catch (Exception e) {
                Log.i(TAG, "release: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * 设备类型枚举
     */
    public enum DeviceType {
        BloodOxygenDevice,      //血氧设备
        BloodPressureDevice,    //血压设备
        BloodSugarDevice;       //血糖设备
    }

    /**
     * 芯片类型枚举
     */
    public enum ChipType {
        PL2303,     //pl2303芯片
        CH340;      //ch340芯片
    }


    /*----ch340芯片相关配置----------------------------------------------*/
    public boolean UartInit() {
        int ret;
        int size = 8;
        byte[] buffer = new byte[size];
        Uart_Control_Out(UartCmd.VENDOR_SERIAL_INIT, 0x0000, 0x0000);
        ret = Uart_Control_In(UartCmd.VENDOR_VERSION, 0x0000, 0x0000, buffer, 2);
        if (ret < 0)
            return false;
        Uart_Control_Out(UartCmd.VENDOR_WRITE, 0x1312, 0xD982);
        Uart_Control_Out(UartCmd.VENDOR_WRITE, 0x0f2c, 0x0004);
        ret = Uart_Control_In(UartCmd.VENDOR_READ, 0x2518, 0x0000, buffer, 2);
        if (ret < 0)
            return false;
        Uart_Control_Out(UartCmd.VENDOR_WRITE, 0x2727, 0x0000);
        Uart_Control_Out(UartCmd.VENDOR_MODEM_OUT, 0x00ff, 0x0000);
        return true;
    }

    public int Uart_Control_Out(int request, int value, int index) {
        int retval = 0;
        retval = mDeviceConnection.controlTransfer(UsbType.USB_TYPE_VENDOR
                        | UsbType.USB_RECIP_DEVICE | UsbType.USB_DIR_OUT, request,
                value, index, null, 0, DEFAULT_TIMEOUT);

        return retval;
    }

    public int Uart_Control_In(int request, int value, int index,
                               byte[] buffer, int length) {
        int retval = 0;
        retval = mDeviceConnection.controlTransfer(UsbType.USB_TYPE_VENDOR
                        | UsbType.USB_RECIP_DEVICE | UsbType.USB_DIR_IN, request,
                value, index, buffer, length, DEFAULT_TIMEOUT);
        return retval;
    }

    public final class UartCmd {
        public static final int VENDOR_WRITE_TYPE = 0x40;
        public static final int VENDOR_READ_TYPE = 0xC0;
        public static final int VENDOR_READ = 0x95;
        public static final int VENDOR_WRITE = 0x9A;
        public static final int VENDOR_SERIAL_INIT = 0xA1;
        public static final int VENDOR_MODEM_OUT = 0xA4;
        public static final int VENDOR_VERSION = 0x5F;
    }

    public final class UsbType {
        public static final int USB_TYPE_VENDOR = (0x02 << 5);
        public static final int USB_RECIP_DEVICE = 0x00;
        public static final int USB_DIR_OUT = 0x00; /* to device */
        public static final int USB_DIR_IN = 0x80; /* to host */
    }

    public final class UartModem {
        public static final int TIOCM_LE = 0x001;
        public static final int TIOCM_DTR = 0x002;
        public static final int TIOCM_RTS = 0x004;
        public static final int TIOCM_ST = 0x008;
        public static final int TIOCM_SR = 0x010;
        public static final int TIOCM_CTS = 0x020;
        public static final int TIOCM_CAR = 0x040;
        public static final int TIOCM_RNG = 0x080;
        public static final int TIOCM_DSR = 0x100;
        public static final int TIOCM_CD = TIOCM_CAR;
        public static final int TIOCM_RI = TIOCM_RNG;
        public static final int TIOCM_OUT1 = 0x2000;
        public static final int TIOCM_OUT2 = 0x4000;
        public static final int TIOCM_LOOP = 0x8000;
    }

    public int Uart_Tiocmset(int set, int clear) {
        int control = 0;
        if ((set & UartModem.TIOCM_RTS) == UartModem.TIOCM_RTS)
            control |= UartIoBits.UART_BIT_RTS;
        if ((set & UartModem.TIOCM_DTR) == UartModem.TIOCM_DTR)
            control |= UartIoBits.UART_BIT_DTR;
        if ((clear & UartModem.TIOCM_RTS) == UartModem.TIOCM_RTS)
            control &= ~UartIoBits.UART_BIT_RTS;
        if ((clear & UartModem.TIOCM_DTR) == UartModem.TIOCM_DTR)
            control &= ~UartIoBits.UART_BIT_DTR;

        return Uart_Set_Handshake(control);
    }

    private int Uart_Set_Handshake(int control) {
        return Uart_Control_Out(UartCmd.VENDOR_MODEM_OUT, ~control, 0);
    }

    public final class UartIoBits {
        public static final int UART_BIT_RTS = (1 << 6);
        public static final int UART_BIT_DTR = (1 << 5);
    }

    public boolean SetConfig(long baudRate, byte dataBit, byte stopBit,
                             byte parity, byte flowControl) {
        int value = 0;
        int index = 0;
        char valueHigh = 0, valueLow = 0, indexHigh = 0, indexLow = 0;
        switch (parity) {
            case 0: /* NONE */
                valueHigh = 0x00;
                break;
            case 1: /* ODD */
                valueHigh |= 0x08;
                break;
            case 2: /* Even */
                valueHigh |= 0x18;
                break;
            case 3: /* Mark */
                valueHigh |= 0x28;
                break;
            case 4: /* Space */
                valueHigh |= 0x38;
                break;
            default: /* None */
                valueHigh = 0x00;
                break;
        }

        if (stopBit == 2) {
            valueHigh |= 0x04;
        }

        switch (dataBit) {
            case 5:
                valueHigh |= 0x00;
                break;
            case 6:
                valueHigh |= 0x01;
                break;
            case 7:
                valueHigh |= 0x02;
                break;
            case 8:
                valueHigh |= 0x03;
                break;
            default:
                valueHigh |= 0x03;
                break;
        }

        valueHigh |= 0xc0;
        valueLow = 0x9c;

        value |= valueLow;
        value |= (int) (valueHigh << 8);

        switch ((int) baudRate) {
            case 50:
                indexLow = 0;
                indexHigh = 0x16;
                break;
            case 75:
                indexLow = 0;
                indexHigh = 0x64;
                break;
            case 110:
                indexLow = 0;
                indexHigh = 0x96;
                break;
            case 135:
                indexLow = 0;
                indexHigh = 0xa9;
                break;
            case 150:
                indexLow = 0;
                indexHigh = 0xb2;
                break;
            case 300:
                indexLow = 0;
                indexHigh = 0xd9;
                break;
            case 600:
                indexLow = 1;
                indexHigh = 0x64;
                break;
            case 1200:
                indexLow = 1;
                indexHigh = 0xb2;
                break;
            case 1800:
                indexLow = 1;
                indexHigh = 0xcc;
                break;
            case 2400:
                indexLow = 1;
                indexHigh = 0xd9;
                break;
            case 4800:
                indexLow = 2;
                indexHigh = 0x64;
                break;
            case 9600:
                indexLow = 2;
                indexHigh = 0xb2;
                break;
            case 19200:
                indexLow = 2;
                indexHigh = 0xd9;
                break;
            case 38400:
                indexLow = 3;
                indexHigh = 0x64;
                break;
            case 57600:
                indexLow = 3;
                indexHigh = 0x98;
                break;
            case 115200:
                indexLow = 3;
                indexHigh = 0xcc;
                break;
            case 230400:
                indexLow = 3;
                indexHigh = 0xe6;
                break;
            case 460800:
                indexLow = 3;
                indexHigh = 0xf3;
                break;
            case 500000:
                indexLow = 3;
                indexHigh = 0xf4;
                break;
            case 921600:
                indexLow = 7;
                indexHigh = 0xf3;
                break;
            case 1000000:
                indexLow = 3;
                indexHigh = 0xfa;
                break;
            case 2000000:
                indexLow = 3;
                indexHigh = 0xfd;
                break;
            case 3000000:
                indexLow = 3;
                indexHigh = 0xfe;
                break;
            default: // default baudRate "9600"
                indexLow = 2;
                indexHigh = 0xb2;
                break;
        }

        index |= 0x88 | indexLow;
        index |= (int) (indexHigh << 8);

        Uart_Control_Out(UartCmd.VENDOR_SERIAL_INIT, value, index);
        if (flowControl == 1) {
            Uart_Tiocmset(UartModem.TIOCM_DTR | UartModem.TIOCM_RTS, 0x00);
        }
        return true;
    }
    /*-----------------------------------------------------*/
}
