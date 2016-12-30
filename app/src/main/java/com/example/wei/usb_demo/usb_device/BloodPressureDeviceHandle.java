package com.example.wei.usb_demo.usb_device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by Wei on 2016/12/30.
 */

public class BloodPressureDeviceHandle extends UsbDeviceHandle {

    private static final String TAG = "TAG_BloodPressureDeviceHandle";

    //接收帧前导码
    public static final int RECEIVE_PRE_1 = 0xAA;
    public static final int RECEIVE_PRE_2 = 0x80;

    //发送帧前导码
    public static final int SEND_PRE_1 = 0xCC;
    public static final int SEND_PRE_2 = 0x80;

    //版本号，索引
    public static final int BLUETOOTH_2_1 = 0x01;
    public static final int BLUETOOTH_4_0 = 0x02;
    public static final int UART = 0x03;
    public static final int BLOOD_PRESSURE_MODE = 0x04;

    //应答标示
    public static final int SUCCESS = 0x00;
    public static final int FAILUE = 0x01;

    //类型biaoshi
    public static final int TEST_BLOOD_PRESSURE = 0x01;

    //数据子码
    public static final int CONNECTION_MACHINE = 0X01;
    public static final int START_TEST = 0X02;
    public static final int STOP_TEST = 0X03;
    public static final int SHUT_DOWN = 0X04;
    public static final int SEND_PRESURE = 0X05;
    public static final int SEND_TEST_RESULT = 0X06;

    private ByteBuffer resultBuffer = ByteBuffer.allocate(1024);//数据包缓冲
    private int count = 0;//当前缓冲区实际数据个数
    private int datacount = 0;

    private byte[] leftbyteData = null;

    public BloodPressureDeviceHandle(Context context, String deviceKey) {
        super(context, deviceKey);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void receiveNewData(byte[] cur_data) {
        byte[] tempArray = null;
        if (leftbyteData != null) {
            Log.i(TAG, "readData: 上次有遗留数据，合并......." + leftbyteData.length);
            tempArray = new byte[cur_data.length + leftbyteData.length];
            System.arraycopy(leftbyteData, 0, tempArray, 0, leftbyteData.length);
            System.arraycopy(cur_data, 0, tempArray, leftbyteData.length, cur_data.length);
            leftbyteData = null;
        } else {
            tempArray = cur_data;
        }

        for (int i = 0; i < tempArray.length; i++) {
            byte b = tempArray[i];
            int intValue = b & 0xff;
            if (count < 2) {//存前导码
                resultBuffer.put(count, b);
                count++;
            } else if (count == 3) {//要存的数据长度
                resultBuffer.put(count, b);
                count++;
                datacount = 2 + 1 + 1 + 1 + intValue;
            } else if (count == datacount - 1) {//一个包接收完成
                resultBuffer.put(count, b);
                byte[] a = new byte[datacount];
                for (int j = 0; j < datacount; j++) {
                    a[j] = resultBuffer.get(j);
                }
                count = 0;
                datacount = 0;
                resultBuffer.clear();

                if (i < tempArray.length - 1) {//这次收到的数据取某部分后就组成完整数据包返回了，剩下一部分
                    int leftCount = tempArray.length - 1 - i;//剩余几个字节
                    leftbyteData = new byte[leftCount];
                    System.arraycopy(tempArray, i + 1, leftbyteData, 0, leftbyteData.length);
                } else {
                    leftbyteData = null;
                }
                _usbInputDataListener.onUSBDeviceInputData(a, deviceKey);
            } else {
                if ((resultBuffer.get(0) & 0xff) == RECEIVE_PRE_1
                        && (resultBuffer.get(1) & 0xff) == RECEIVE_PRE_2) {
                    resultBuffer.put(count, b);
                    count++;
                } else {
                    count = 0;
                    resultBuffer.clear();
                    datacount = 0;
                }
            }
        }
    }
}
