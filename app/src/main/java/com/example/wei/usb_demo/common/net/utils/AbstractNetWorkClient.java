package com.example.wei.usb_demo.common.net.utils;

import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by hoyouly on 15/8/6.
 * 抽象的网络接口层
 */
public interface AbstractNetWorkClient {

    /**
     * 请求json数据
     *
     * @param url      网络的url
     * @param param    参数，如果为null，表示get方式请求，否则为post方法请求
     * @param listener 请求结果监听
     */
    void requestJsonData(String url, Map<String, String> param, ResponseListener<JSONObject> listener);

    void requestJsonDataNew(String url, Map<String, String> param, boolean isToken, ResponseListener<JSONObject> listener);

    void requestJsonDataWithoutCache(String url, Map<String, String> param, boolean isToken, ResponseListener<JSONObject> listener);

    /**
     * 请求json数组数据
     *
     * @param url      网络的url
     * @param param    参数，如果为null，表示get方式请求，否则为post方法请求
     * @param listener 请求结果监听
     */
    void requestJsonArrayData(String url, Map<String, String> param, ResponseListener<JSONArray> listener);

    void requestJsonArrayDataNew(String url, Map<String, String> param, boolean isToken, ResponseListener<JSONArray> listener);

    /**
     * 加载网络图片
     *
     * @param imageView         图片所在的控件
     * @param url               图片的网络路径
     * @param defaultImageResId 默认图片资源ID
     * @param errorImageResId   图片加载错误后显示的资源ID
     */
    void loadImage(ImageView imageView, String url, int defaultImageResId, int errorImageResId);


    /**
     * 加载网络图片，默认图片和错误图片已经有默认值了
     *
     * @param imageView 图片所在的控件
     * @param url       图片的网络路径
     */
    void loadImage(ImageView imageView, String url);

    /**
     * 取消某个url的网络请求
     *
     * @param url
     */
    void cancelRequest(String url);

    /**
     * 清楚缓存
     *
     * @param url
     */
    void clearCache(String url);
}
