package com.example.wei.usb_demo.usb_device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.util.Log;

import com.example.wei.usb_demo.utils.CrcUtil;
import com.example.wei.usb_demo.utils.StringUtil;

/**
 * Created by Wei on 2016/12/30.
 */

public class BloodOxygenDeviceHandle extends UsbDeviceHandle {

    private static final String TAG = "TAG_BloodOxygenDeviceHandle";

    private byte[] data_package;    //包数据
    private boolean get_new_p;      //接收一个新包

    public BloodOxygenDeviceHandle(Context context, String deviceKey) {
        super(context, deviceKey);

        get_new_p = true;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void receiveNewData(byte[] cur_data) {
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
                            Log.i(TAG, "run 数据长度符合要求: "+ StringUtil.bytesToHexString(data_package));
                            cur_len = data_package.length;
                            byte[] content = new byte[cur_len-1];
                            System.arraycopy(data_package, 0, content, 0, content.length);
                            char crc = CrcUtil.get_crc_code(content);
                            if (crc == (data_package[cur_len-1]<0?(data_package[cur_len-1]+256):data_package[cur_len-1])) {      //校验成功
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                         _usbInputDataListener.onUSBDeviceInputData(data_package, deviceKey);
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

    @Override
    public byte[] getHandshakePacketData() {
        final String str = "AA55FF0201";
        byte[] data = StringUtil.hexStringToBytes(str);
        char crc = CrcUtil.get_crc_code(data);
        byte[] data_n = new byte[data.length + 1];
        System.arraycopy(data, 0, data_n, 0, data.length);
        data_n[data_n.length - 1] = (byte) crc;

        return data_n;
    }

    @Override
    public boolean discernDevice(UsbDevice device) {
        return false;
    }
}
