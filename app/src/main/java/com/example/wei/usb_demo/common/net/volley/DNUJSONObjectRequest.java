package com.example.wei.usb_demo.common.net.volley;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by ygc on 14-10-24.
 */
public class DNUJSONObjectRequest extends DNUJSONRequest<JSONObject> {
    private final static String TAG = "DNUJSONObjectRequest";

    public DNUJSONObjectRequest(int method, String url, Map<String, String> param, Response.Listener<JSONObject> mListener, Response.ErrorListener listener) {
        super(method, url, param, mListener, listener);
        setMyDefaultPolicy();
    }


    public DNUJSONObjectRequest(String url, Map<String, String> param, Response.Listener<JSONObject> mListener, Response.ErrorListener listener) {
        super(param == null ? Method.GET : Method.POST, url, param, mListener, listener);
        setMyDefaultPolicy();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        if (response.data.length > 10000) setShouldCache(false);
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    public void setMyDefaultPolicy() {
        setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0, 0));
    }


    public Request<?> setMyDefaultPolicy2() {
        return setRetryPolicy(new DefaultRetryPolicy(15 * 1000, 0, 0));
    }

}
