package com.example.wei.usb_demo.common.net.utils;

/**
 * Created by hoyouly on 15/8/6.
 */
public interface ResponseListener<T> {
    void onSuccess(T t);

    void onError(String errMsg);
}
