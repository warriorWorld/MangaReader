package com.truthower.suhang.mangareader.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

public class VolleyTool {
    private static VolleyTool mInstance = null;
    private RequestQueue mRequestQueue;
    private Gson gson;

    private VolleyTool(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        gson = new Gson();
    }

    public static VolleyTool getInstance(Context context) {
        if (mInstance == null) {
            synchronized (VolleyTool.class) {
                if (mInstance == null)
                    mInstance = new VolleyTool(context);
            }
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void release() {
        this.mRequestQueue = null;
        mInstance = null;
    }

    /**
     * 请求数据
     *
     * @param url      请求路径
     * @param params   请求参数 Map集合
     * @param objClass 对象字节码
     * @param callBack 加载完成回调
     */
    public <ResultObj> void requestData(int method, Context context,
                                        String url, HashMap<String, String> params,
                                        Class<ResultObj> objClass, final VolleyCallBack<ResultObj> callBack) {
        MyGsonRequest<ResultObj> request = new MyGsonRequest<ResultObj>(method,
                url, params, objClass, new Listener<ResultObj>() {
            @Override
            public void onResponse(ResultObj response) {
                callBack.loadSucceed(response);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callBack.loadFailed(error);
            }
        });
        mRequestQueue.add(request);
    }

    public <T> void requestData(int method, Context context, String url,
                                JSONObject jsonObject, final Class<T> objClass,
                                final VolleyCallBack<T> callBack) {
        JsonObjectRequest request = new JsonObjectRequest(method, url,
                jsonObject, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject arg0) {
                callBack.loadSucceed(gson.fromJson(arg0.toString(),
                        objClass));
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                callBack.loadFailed(arg0);
            }
        });
        mRequestQueue.add(request);
    }
}
