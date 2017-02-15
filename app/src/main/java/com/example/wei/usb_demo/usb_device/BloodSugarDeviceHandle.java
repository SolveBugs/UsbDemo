package com.example.wei.usb_demo.usb_device;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;

import com.example.wei.usb_demo.utils.CrcUtil;
import com.example.wei.usb_demo.utils.StringUtil;

import java.nio.ByteBuffer;

/**
 * Created by zhenqiang on 2017/1/3.
 */

public class BloodSugarDeviceHandle extends UsbDeviceHandle {


    private static final String TAG = "BloodSugarDeviceHandle";

    //包头 0xAA,0X60
    public static final int RECEIVE_PRE_1 = 0xAA;
    public static final int RECEIVE_PRE_2 = 0x60;

    //应答标示
    public static final int SUCCESS = 0x01;
    public static final int FAILUE = 0x00;


    public static final int DATA_VALUE = 1;
    public static final int DATA_DATETIME = 2;
    public static final int DEVICE_SN = 3;


    private ByteBuffer resultBuffer = ByteBuffer.allocate(1024);//数据包缓冲
    private int count = 0;//当前缓冲区实际数据个数
    private int datacount = 0;

    private byte[] leftbyteData = null;

    public BloodSugarDeviceHandle(Context context, String deviceKey) {
        super(context, deviceKey);
    }

    public BloodSugarDeviceHandle(Context context) {
        super();
        this._context = context;
    }

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
            } else if (count == 2) {//要存的数据长度
                resultBuffer.put(count, b);
                count++;
                datacount = 2 + 1 + intValue;
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
                int cur_len = a.length;
                byte[] content = new byte[cur_len - 1];
                System.arraycopy(a, 0, content, 0, content.length);
                char crc = CrcUtil.get_crc_code(content);
                if (crc == (a[cur_len - 1] < 0 ? (a[cur_len - 1] + 256) : a[cur_len - 1])) {//校验成功
                    _usbInputDataListener.onUSBDeviceInputData(a, deviceKey);
                } else {
                    Log.i(TAG, "run 校验失败: " + StringUtil.bytesToHexString(a) + "-->" + (int) crc);
                }
                break;
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

    @Override
    public byte[] getHandshakePacketData() {
        String dataHead = "aa600201";
        String dataStr = dataHead;
        byte[] data = StringUtil.hexStringToBytes(dataStr);
        char crc = CrcUtil.get_crc_code(data);
        byte[] data_n = new byte[data.length + 1];
        System.arraycopy(data, 0, data_n, 0, data.length);
        data_n[data_n.length - 1] = (byte) crc;
        return data_n;
    }


    @Override
    public boolean discernDevice(UsbDevice device) {

        int vendorId = device.getVendorId();
        int productId = device.getProductId();

        if (vendorId == 10473 && productId == 394) {
            Intent intent = new Intent(ACTION_DEVICE_DISCERN_FINISH_NOTIFY);
            Bundle bundle = new Bundle();
            bundle.putInt(K_DEVICE_DISCERN_FINISH_TYPE, 0);
            bundle.putString(K_DEVICE_DISCERN_FINISH_KEY, deviceKey);
            bundle.putInt(K_DEVICE_DISCERN_FINISH_STATE, 1);
            intent.putExtras(bundle);
            _context.sendBroadcast(intent);
            return true;
        }
        return false;
    }
}
