package com.example.wei.usb_demo.common.net.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.wei.pl2303_test.R;
import com.example.wei.usb_demo.app.AppContext;
import com.example.wei.usb_demo.common.net.volley.DNUJSONArrayRequest;
import com.example.wei.usb_demo.common.net.volley.DNUJSONObjectRequest;
import com.example.wei.usb_demo.common.net.volley.HandleVolleyError;
import com.example.wei.usb_demo.common.net.volley.VolleyUtil;
import com.example.wei.usb_demo.common.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by hoyouly on 15/8/7.
 */
public class VolleyClient implements AbstractNetWorkClient {

    private Context mContext;
    private static VolleyClient sClient;
    private ImageLoader.ImageListener imageListener;
    private AppContext appContext;

    private VolleyClient(Context mContext) {
        this.mContext = mContext;
        appContext = (AppContext) mContext.getApplicationContext();
    }

    public static VolleyClient getInstance(Context context) {
        synchronized (VolleyClient.class) {
            if (sClient == null) {
                sClient = new VolleyClient(context.getApplicationContext());
            }
        }
        return sClient;
    }


    @Override
    public void requestJsonData(final String url, final Map<String, String> param, final ResponseListener<JSONObject> listener) {
        DNUJSONObjectRequest request = new DNUJSONObjectRequest(url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                if (listener != null) {
                    listener.onSuccess(object);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("hoyouly", "onErrorResponse " + url + "  param :" + Utils.printMap(param) + "   错误信息：" + error.toString());
                if (listener != null) {
                    listener.onError(HandleVolleyError.getMessage(mContext, error));
                }
            }
        });
        request.setTag(url);
        request.setMyDefaultPolicy();
        request.setShouldCache(false);
        VolleyUtil.getInstance(mContext).startRequestQueue(request);
    }

    @Override
    public void requestJsonDataNew(String url, Map<String, String> param, boolean isToken, ResponseListener<JSONObject> listener) {
        requestJsonData(url, Utils.rebuildMap(param, appContext.getActiveUser()), listener);
    }

    private Map<String, String> rebuildMap(Map<String, String> param, boolean isToken) {
        if (isToken) {
            param = Utils.rebuildMap(param, appContext.getActiveUser());
        } else {
            param = Utils.rebuildMap(param, null);
        }
        return param;
    }

    @Override
    public void requestJsonArrayData(final String url, final Map<String, String> param,
                                     final ResponseListener<JSONArray> listener) {
        DNUJSONArrayRequest request = new DNUJSONArrayRequest(url, param, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (listener != null) {
                    listener.onSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("hoyouly", "onErrorResponse " + url + "  param :" + Utils.printMap(param) + "   错误信息：" + error.toString());
                if (listener != null) {
                    listener.onError(HandleVolleyError.getMessage(mContext, error));
                }
            }
        });
        request.setTag(url);
        VolleyUtil.getInstance(mContext).startRequestQueue(request);
    }

    @Override
    public void requestJsonArrayDataNew(String url, Map<String, String> param,
                                        boolean isToken, ResponseListener<JSONArray> listener) {
        requestJsonArrayData(url, rebuildMap(param, isToken), listener);
    }

    @Override
    public void requestJsonDataWithoutCache(final String url, Map<String, String> param,
                                            boolean isToken, final ResponseListener<JSONObject> listener) {
        DNUJSONObjectRequest request = new DNUJSONObjectRequest(url, rebuildMap(param, isToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                if (listener != null) {
                    listener.onSuccess(object);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("hoyouly", "onErrorResponse " + url);
                if (listener != null) {
                    listener.onError(HandleVolleyError.getMessage(mContext, error));
                }
            }
        });
        request.setTag(url);
        request.setMyDefaultPolicy();
        request.setShouldCache(false);
        VolleyUtil.getInstance(mContext).startRequestQueue(request);
    }

    @Override
    public void loadImage(ImageView imageView, String url, int defaultImageResId,
                          int errorImageResId) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Log.e("Volley", url + "___startTime :" + System.currentTimeMillis());
        if (imageView instanceof NetworkImageView) {
            ((NetworkImageView) imageView).setImageUrl(url, VolleyUtil.getInstance(mContext).getImageLoader());
        } else {
            imageListener = ImageLoader.getImageListener(imageView, defaultImageResId, errorImageResId, url);
            VolleyUtil.getInstance(mContext).getImageLoader().get(url, imageListener);
        }
    }

    @Override
    public void loadImage(ImageView imageView, String url) {
        loadImage(imageView, url, R.drawable.more_default_avatar, R.drawable.more_default_avatar);
    }

    @Override
    public void cancelRequest(String url) {
        if (!TextUtils.isEmpty(url)) {
            VolleyUtil.getInstance(mContext).getRequestQueue().cancelAll(url);
        }
    }

    @Override
    public void clearCache(String url) {
        VolleyUtil.getInstance(mContext).getImageLoader().clearCache(url);
    }

}
