package com.truthower.suhang.mangareader.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.truthower.suhang.mangareader.utils.Logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MyGsonRequest<T> extends Request<T> {

    private final Listener<T> mListener;

    private Gson mGson;

    private Class<T> mClass;
    private HashMap<String, String> params;

    public MyGsonRequest(int method, String url, Class<T> clazz,
                         Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mGson = new Gson();
        mClass = clazz;
        mListener = listener;
    }

    public MyGsonRequest(String url, Class<T> clazz, Listener<T> listener,
                         ErrorListener errorListener) {
        this(Method.GET, url, clazz, listener, errorListener);
    }

    public MyGsonRequest(String url, HashMap<String, String> params,
                         Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
        this(Method.POST, url, clazz, listener, errorListener);
        this.params = params;

        // Set<String> keys = params.keySet();
        // Iterator<String> iterator = keys.iterator();
        // while (iterator.hasNext()) {
        // try {
        // Logger.e(iterator.next() + ":" + "\n");
        // Logger.e(params.get(iterator.next().toString()) + "\n");
        // } catch (Exception e) {
        // Logger.e(e.getLocalizedMessage());
        // Logger.e(e.getMessage());
        // }
        // }
    }

    /**
     * @param method        这里可以传值POST或者GET从而决定是什么方式获取网络
     * @param url
     * @param params
     * @param clazz
     * @param listener
     * @param errorListener
     */
    public MyGsonRequest(int method, String url,
                         HashMap<String, String> params, Class<T> clazz,
                         Listener<T> listener, ErrorListener errorListener) {

        this(method, url, clazz, listener, errorListener);
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        // TODO Auto-generated method stub
        return params;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Logger.d("mClass" + mClass + "json串" + jsonString);
//            jsonString = StringUtil.cutString(jsonString);
            return Response.success(mGson.fromJson(jsonString, mClass),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

}
