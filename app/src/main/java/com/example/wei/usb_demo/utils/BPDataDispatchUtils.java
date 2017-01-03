package com.example.wei.usb_demo.utils;

/**
 * Created by zhenqiang on 2017/1/3.
 */

import android.content.Context;
import android.util.Log;

import com.example.wei.usb_demo.usb_device.BloodPressureDeviceHandle;

/**
 * 血压:
 * 由一个完整的血压数据包来获取其中具体的内容
 */
public class BPDataDispatchUtils {

    private static final String TAG = "BPDataDispatchUtils";
    private Context context;
    private static BPDataDispatchUtils bpDataDispatchUtils;
    private static IMeasureDataResultCallback mIMeasureDataResultCallback;
    private static String resultStr = "";

    public static BPDataDispatchUtils getInstance(Context context) {
        if (bpDataDispatchUtils == null) {
            synchronized (BPDataDispatchUtils.class) {
                bpDataDispatchUtils = new BPDataDispatchUtils(context);
            }
        }
        return bpDataDispatchUtils;
    }

    public BPDataDispatchUtils(Context context) {
        this.context = context;
    }


    /**
     * 由完整数据包截取数据长度+标识编号+数据子码+数据／参数部分
     *
     * @param datapackage
     * @return
     */
    public static byte[] getData(byte[] datapackage) {
        if (datapackage == null) {
            return null;
        }
        int count = (datapackage[3] & 0xff) + 1;
        byte[] bs = new byte[count];
        System.arraycopy(datapackage, 3, bs, 0, bs.length);
        return bs;
    }


    /**
     * 具体数据信息
     *
     * @param datapackage
     */
    public static void dispatch(byte[] datapackage, IMeasureDataResultCallback iMeasureDataResultCallback) {

        if (datapackage == null) {
            return;
        }

        mIMeasureDataResultCallback = iMeasureDataResultCallback;
        byte[] data = getData(datapackage);

        if (data == null) {
            return;
        }
        int type = data[2] & 0xff;
        int result = data[3] & 0xff;

        boolean success = false;
        if (result == BloodPressureDeviceHandle.SUCCESS) {
            success = true;
        }
        switch (type) {
            case BloodPressureDeviceHandle.CONNECTION_MACHINE:
                resultStr = "连接血压计应答------" + success;
                Log.i(TAG, "dispatch: 连接血压计应答------" + success);
                break;
            case BloodPressureDeviceHandle.START_TEST:
                resultStr = "启动测量应答------ " + success;
                Log.i(TAG, "dispatch: 启动测量应答------" + success);
                break;
            case BloodPressureDeviceHandle.STOP_TEST:
                resultStr = "停止测量应答-------" + success;
                Log.i(TAG, "dispatch: 停止测量应答-------" + success);
                break;
            case BloodPressureDeviceHandle.SHUT_DOWN:
                resultStr = "关机应答-------" + success;
                Log.i(TAG, "dispatch: 关机应答-------" + success);
                break;
            case BloodPressureDeviceHandle.SEND_PRESURE:
                getRealTimePressure(data);
                Log.i(TAG, "dispatch: 发送实时压力");
                break;
            case BloodPressureDeviceHandle.SEND_TEST_RESULT:
                getTestResult(data);
                Log.i(TAG, "dispatch: 发送测量结果");
                break;
            default:
                break;
        }
        iMeasureDataResultCallback.onResult(resultStr);
    }


    /**
     * 实时压力
     *
     * @param data
     */
    public static void getRealTimePressure(byte[] data) {
        byte[] bs = new byte[2];
        System.arraycopy(data, 3, bs, 0, bs.length);
        byte high = bs[0];
        byte low = bs[1];
        Log.i(TAG, "getRealTimePressure: 实时压力为==" + ((high << 8 | low) & 0xff));
        resultStr = "实时压力为==" + ((high << 8 | low) & 0xff);
    }

    /**
     * 测量结果
     *
     * @param data
     */
    public static void getTestResult(byte[] data) {
        byte[] bs = new byte[13];
        System.arraycopy(data, 3, bs, 0, bs.length);


        int year = bs[1] & 0xff + 2000;
        int month = bs[2] & 0xff;
        int day = bs[3] & 0xff;
        int hour = bs[4] & 0xff;
        int minute = bs[5] & 0xff;
        int second = bs[6] & 0xff;
        Log.i(TAG, "getTestResult: 测量时间为:" + year + "-" + month + "-" + day + "-" + " " + hour + ":" + minute + ":" + second);
        resultStr += "测量时间为:" + year + "-" + month + "-" + day + "-" + " " + hour + ":" + minute + ":" + second;

        int sys = (bs[7] << 8 | bs[8]) & 0xff;
        int dia = (bs[9] << 8 | bs[10]) & 0xff;
        int pul = (bs[11] << 8 | bs[12]) & 0xff;
        Log.i(TAG, "getTestResult: 测量结果为:" + "SYS=" + sys + ",DIA=" + dia + ",PUL=" + pul);
        resultStr += "===";
        resultStr += "测量结果为:" + "SYS=" + sys + ",DIA=" + dia + ",PUL=" + pul;

    }


    public interface IMeasureDataResultCallback {
        void onResult(String result);
    }

}
