package com.example.wei.usb_demo.common.net.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by dnurse3 on 14/10/31.
 */
public class DNUJSONArrayRequest extends DNUJSONRequest<JSONArray> {
    private final static String TAG = "DNUJSONObjectRequest";

    public DNUJSONArrayRequest(int method, String url, Map<String, String> param, Response.Listener<JSONArray> mListener, Response.ErrorListener listener) {
        super(method, url, param, mListener, listener);
    }


    public DNUJSONArrayRequest(String url, Map<String, String> param, Response.Listener<JSONArray> mListener, Response.ErrorListener listener) {
        super(param == null ? Request.Method.GET : Request.Method.POST, url, param, mListener, listener);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        if (response.data.length > 10000) setShouldCache(false);
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
