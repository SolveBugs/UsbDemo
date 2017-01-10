package com.example.wei.usb_demo.utils;

/**
 * Created by zhenqiang on 2017/1/3.
 */

import android.content.Context;
import android.util.SparseArray;

import com.example.wei.usb_demo.usb_device.BloodSugarDeviceHandle;

/**
 * 血糖:
 * 由一个完整的数据包获取具体的内容
 */
public class BSDataDispatchUtils {
    private static final String TAG = "BSDataDispatchUtils";

    private Context context;
    private static BSDataDispatchUtils bsDataDispatchUtils;
    private static IBloodSugarDataResultCallback mDataResultCallback;
    private static int currentState = -1;

    public static BSDataDispatchUtils getInstance(Context context) {
        if (bsDataDispatchUtils == null) {
            synchronized (BSDataDispatchUtils.class) {
                bsDataDispatchUtils = new BSDataDispatchUtils(context);
            }
        }
        return bsDataDispatchUtils;
    }

    public BSDataDispatchUtils(Context context) {
        this.context = context;
    }

    /**
     * @param datapackage
     * @return
     */
    public static byte[] getData(byte[] datapackage) {
        if (datapackage == null) {
            return null;
        }
        int count = (datapackage[2] & 0xff) + 1;
        byte[] bs = new byte[count];
        System.arraycopy(datapackage, 2, bs, 0, bs.length);
        return bs;
    }

    public static void dispatch(byte[] datapackage, IBloodSugarDataResultCallback iBloodSugarDataResultCallback) {
        if (datapackage == null) {
            return;
        }

        mDataResultCallback = iBloodSugarDataResultCallback;

        byte[] data = getData(datapackage);
        if (data == null) {
            return;
        }

        int type = data[1] & 0xff;
        switch (type) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                iBloodSugarDataResultCallback.onMeasuring(type);
                break;
            case 18:
                float dataResult = (float) ((data[2] << 8 | data[3]) & 0xff);
                iBloodSugarDataResultCallback.onSucess(dataResult);
                break;
            default:
                break;
        }
    }

    public interface IBloodSugarDataResultCallback {
        void onMeasuring(int arg0);

        void onSucess(float result);
    }
}
