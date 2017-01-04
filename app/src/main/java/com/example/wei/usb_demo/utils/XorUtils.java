package com.example.wei.usb_demo.utils;

/**
 * Created by zhenqiang on 2017/1/4.
 */

public class XorUtils {
    /**
     * 异或运算和校验
     *
     * @param datas
     * @return
     */
    public static byte getXor(byte[] datas) {
        byte temp = datas[0];

        for (int i = 1; i < datas.length; i++) {
            temp ^= datas[i];
        }
        return temp;
    }
}
