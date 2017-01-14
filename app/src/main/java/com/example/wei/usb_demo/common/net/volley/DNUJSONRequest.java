package com.example.wei.usb_demo.common.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.example.wei.usb_demo.app.AppContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ygc on 14-10-24.
 */
public abstract class DNUJSONRequest<T> extends Request<T> {

    private final Response.Listener<T> mListener;
    private Map<String, String> mParam;

    public DNUJSONRequest(int method, String url, Map<String, String> param, Response.Listener<T> mListener, Response.ErrorListener listener) {
        super(method, url, listener);
        this.mListener = mListener;
        mParam = param;
    }

    @Override
    abstract protected Response<T> parseNetworkResponse(NetworkResponse networkResponse);

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParam;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("User-Agent", AppContext.getUSER_AGENT());
        return headerMap;
    }
}
