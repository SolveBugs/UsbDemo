package com.example.wei.usb_demo;

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

import java.nio.ByteBuffer;

/**
 * Created by Wei on 2016/12/20.
 */

public class UsbDeviceHandle extends Object {

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
    private USBDeviceInputDataListener _usbInputDataListener = null;

    final static String TAG = "USBReader";
    final static int DEFAULT_TIMEOUT = 500;

    public UsbDeviceHandle(Context context, String deviceKey) {
        super();
        _context = context;
        usbManager = (UsbManager) _context.getSystemService(Context.USB_SERVICE);
        readUsbDevice = usbManager.getDeviceList().get(deviceKey);
        if (usbManager.hasPermission(readUsbDevice)) {
            this.openUSB();
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
        Log.i(TAG, "openUSB getInterfaceCount: "+readUsbDevice.getInterfaceCount());
        usbInterface = readUsbDevice.getInterface(0);
        Log.i(TAG, "openUSB getInterfaceClass: "+usbInterface.getInterfaceClass());
        Log.i(TAG, "openUSB getInterfaceProtocol: "+usbInterface.getInterfaceProtocol());
        Log.i(TAG, "openUSB getInterfaceSubclass: "+usbInterface.getInterfaceSubclass());
        for (int index = 0; index < usbInterface.getEndpointCount(); index ++) {
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
        arrayOfByte[0] = (byte) (38400 & 255);
        arrayOfByte[1] = (byte) (38400 >> 8 & 255);
        arrayOfByte[2] = (byte) (38400 >> 16 & 255);
        arrayOfByte[3] = (byte) (38400 >> 24 & 255);
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
    private class MyThread implements Runnable  {
        private UsbRequest usbRequest;
        private ByteBuffer byteBuffer;
        private byte[] data_package;    //包数据
        private boolean get_new_p;      //接收一个新包
        private char package_len;
        public MyThread() {
            usbRequest = new UsbRequest();
            int inMax = usbEpIn.getMaxPacketSize();
            byteBuffer = ByteBuffer.allocate(inMax);

            get_new_p = true;
            package_len = 0;
        }
        public void run() {
            while (read) {
                usbRequest.initialize(mDeviceConnection, usbEpIn);
                usbRequest.queue(byteBuffer, byteBuffer.capacity());
                if(mDeviceConnection.requestWait() == usbRequest){
                    byte[] cur_data = new byte[byteBuffer.position()];
                    System.arraycopy(byteBuffer.array(), 0, cur_data, 0, cur_data.length);
                    Log.i(TAG, "run 收到数据: "+StringUtil.bytesToHexString(cur_data));
                    if (get_new_p) {
                        data_package = cur_data;
                        get_new_p = false;
                    } else {
                        byte[] data = data_package;
                        data_package = new byte[data.length+cur_data.length];
                        System.arraycopy(data, 0, data_package, 0, data.length);
                        System.arraycopy(cur_data, 0, data_package, data.length, cur_data.length);
                    }

                    int cur_len = data_package.length;
                    if (cur_len > 0 && (data_package[0] == (byte) 0xAA)) {
                        do {
                            if (cur_len > 1) {
                                if (data_package[1] == 0x55) {
                                    if (cur_len >= 6) {     //正常包至少是6字节
                                        int data_len = data_package[3];
                                        byte[] next_package = null;
                                        if (cur_len == data_len+4) {
                                            get_new_p = true;
                                        } else if (cur_len > data_len+4) {
                                            next_package = new byte[cur_len - data_len-4];
                                            System.arraycopy(data_package, data_len+4, next_package, 0, next_package.length);
                                            byte[] cur_package = new byte[data_len+4];
                                            System.arraycopy(data_package, 0, cur_package, 0, cur_package.length);
                                            data_package = cur_package;
                                        } else {
                                            break;
                                        }
                                        Log.i(TAG, "run 数据长度符合要求: "+StringUtil.bytesToHexString(data_package));
                                        cur_len = data_package.length;
                                        byte[] content = new byte[cur_len-1];
                                        System.arraycopy(data_package, 0, content, 0, content.length);
                                        char crc = CrcUtil.cal_crc_table(content);
                                        if (crc == (data_package[cur_len-1]<0?(data_package[cur_len-1]+256):data_package[cur_len-1])) {      //校验成功
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    _usbInputDataListener.onUSBDeviceInputData(data_package, readUsbDevice.getDeviceName());
                                                }
                                            });
                                        } else {
                                            Log.i(TAG, "run 校验失败: "+StringUtil.bytesToHexString(data_package)+"-->"+(int)crc);
                                        }
                                        if (next_package != null) {
                                            data_package = next_package;
                                            cur_len = data_package.length;
                                            if (data_package[0] != (byte) 0xAA) {
                                                get_new_p = true;
                                                break;
                                            }
                                        } else {
                                            get_new_p = true;
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                } else {
                                    Log.i(TAG, "run 包头错误: "+StringUtil.bytesToHexString(data_package));
                                    get_new_p = true;
                                    break;
                                }
                            } else {
                                break;
                            }
                        } while (cur_len > 0 && (data_package[0] == (byte) 0xAA));
                    } else {
                        Log.i(TAG, "run 包头错误: "+StringUtil.bytesToHexString(data_package));
                        get_new_p = true;
                    }
                }

            }
            mDeviceConnection.releaseInterface(usbInterface);
            mDeviceConnection.close();
        }
    }

    public int sendToUsb(byte[] data) {
        if (!usbManager.hasPermission(readUsbDevice)) {
            return -2;
        }
        // 发送准备命令
        char crc = CrcUtil.cal_crc_table(data);
        byte[] data_n = new byte[data.length+1];
        System.arraycopy(data, 0, data_n, 0, data.length);
        data_n[data_n.length-1] = (byte) crc;
        int ret = mDeviceConnection.bulkTransfer(usbEpOut, data_n, data_n.length, DEFAULT_TIMEOUT);
        Log.i(TAG, "sendToUsb 发送: "+ret);
        // 接收发送成功信息(相当于读取设备数据)
//        receiveytes = new byte[128];   //根据设备实际情况写数据大小
//        ret = mDeviceConnection.bulkTransfer(usbEpIn, receiveytes, receiveytes.length, 10000);
        return ret;
    }

    /**
     * usb权限相关广播
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
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

    public void setUSBDeviceInputDataListener(USBDeviceInputDataListener listener) {
        _usbInputDataListener = listener;
    }

    public void release() {
        if (mUsbReceiver != null) {
            _context.unregisterReceiver(mUsbReceiver);
        }
        
    }

    public boolean SetConfig(int baudRate, byte dataBit, byte stopBit,
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

        switch (baudRate) {
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

    /**
     * Performs a control transaction on endpoint zero for this device. The
     * direction of the transfer is determined by the request type. If
     * requestType & {@link UsbConstants#USB_ENDPOINT_DIR_MASK} is
     * {@link UsbConstants#USB_DIR_OUT}, then the transfer is a write, and if it
     * is {@link UsbConstants#USB_DIR_IN}, then the transfer is a read.
     *
     * @param1 requestType
     *            request type for this transaction
     * @param request
     *            request ID for this transaction
     * @param value
     *            value field for this transaction
     * @param index
     *            index field for this transaction
     * @param1 buffer
     *            buffer for data portion of transaction, or null if no data
     *            needs to be sent or received
     * @param1 length
     *            the length of the data to send or receive
     * @param1 timeout
     *            in milliseconds
     * @return length of data transferred (or zero) for success, or negative
     *         value for failure
     *
     *         public int controlTransfer(int requestType, int request, int
     *         value, int index, byte[] buffer, int length, int timeout)
     */

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

    private int Uart_Set_Handshake(int control) {
        return Uart_Control_Out(UartCmd.VENDOR_MODEM_OUT, ~control, 0);
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

    public final class UsbType {
        public static final int USB_TYPE_VENDOR = (0x02 << 5);
        public static final int USB_RECIP_DEVICE = 0x00;
        public static final int USB_DIR_OUT = 0x00; /* to device */
        public static final int USB_DIR_IN = 0x80; /* to host */
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

    public final class UartState {
        public static final int UART_STATE = 0x00;
        public static final int UART_OVERRUN_ERROR = 0x01;
        public static final int UART_PARITY_ERROR = 0x02;
        public static final int UART_FRAME_ERROR = 0x06;
        public static final int UART_RECV_ERROR = 0x02;
        public static final int UART_STATE_TRANSIENT_MASK = 0x07;
    }

    public final class UartIoBits {
        public static final int UART_BIT_RTS = (1 << 6);
        public static final int UART_BIT_DTR = (1 << 5);
    }
}
