package com.example.wei.usb_demo.common.net.utils;

import org.json.JSONObject;

/**
 * Created by GuangyongLiu on 15/10/12.
 */
public class RequestResult {


    public interface RequestCompleteListener {

        /**
         * 请求成功后，返回结果正确的回调
         *
         * @param data 返回的数据
         */
        void success(String data);

        /**
         * 请求成功后，返回结果错误的回调
         *
         * @param errCode 错误码
         * @param errInfo 错误信息
         */
        void error(int errCode, String errInfo);
    }

    public static void requestComplete(JSONObject jsonObject, RequestCompleteListener listener) {
        if (jsonObject == null) {
            return;
        }
        if (jsonObject.optInt("s") == -200) {
            if (listener != null) {
                listener.success(jsonObject.optString("d"));
            }
        } else {
            //返回结果错误的时候回调错误码，信息
            if (listener != null) {
                listener.error(jsonObject.optInt("s"), jsonObject.optString("m"));
            }
        }
    }
}
