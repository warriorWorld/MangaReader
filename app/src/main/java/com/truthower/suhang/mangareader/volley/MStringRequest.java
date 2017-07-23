package com.truthower.suhang.mangareader.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MStringRequest extends StringRequest {
    private HashMap<String, String> params;
    private int timeoutMs = 70000;

    public MStringRequest(String url, HashMap<String, String> params,
                          Listener<String> listener, ErrorListener errorListener) {
        super(Method.POST, url, listener, errorListener);
        this.params = params;
        setRetryPolicy(new DefaultRetryPolicy(timeoutMs, 1, 1.0f));
    }

    @Override
    public RetryPolicy getRetryPolicy() {
        // 默认超时:super.getRetryPolicy().getCurrentTimeout()
        return super.getRetryPolicy();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }
}
