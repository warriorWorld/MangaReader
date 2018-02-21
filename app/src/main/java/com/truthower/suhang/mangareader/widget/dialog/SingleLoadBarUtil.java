package com.truthower.suhang.mangareader.widget.dialog;

import android.app.Activity;
import android.app.ProgressDialog;


/**
 * Created by Administrator on 2017/11/23.
 */

public class SingleLoadBarUtil {
    private ProgressDialog loaderBar;
    private Activity context;

    private SingleLoadBarUtil() {
    }


    private static volatile SingleLoadBarUtil instance = null;

    public static SingleLoadBarUtil getInstance() {
        if (instance == null) {
            synchronized (SingleLoadBarUtil.class) {
                if (instance == null) {
                    instance = new SingleLoadBarUtil();
                }
            }
        }
        return instance;
    }

    public void showLoadBar(Activity context) {
        try {
            if (null == loaderBar || this.context == null || this.context.isFinishing() || this.context != context) {
                this.context = context;
                loaderBar = new ProgressDialog(context);
                loaderBar.setCancelable(true);
                loaderBar.setMessage("加载中...");
            }

            if (loaderBar.isShowing()) {
                return;
            }
            loaderBar.show();
        } catch (Exception e) {

        }
    }

    public void dismissLoadBar() {
        try {
            loaderBar.dismiss();
        } catch (Exception e) {

        }
    }
}
