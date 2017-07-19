package com.truthower.suhang.mangareader.widget.toast;/**
 * Created by Administrator on 2016/11/8.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.truthower.suhang.mangareader.R;


/**
 * 作者：苏航 on 2016/11/8 14:21
 * 邮箱：772192594@qq.com
 */
public class EasyToast extends Toast {
    private Context context;
    private TextView titleTv, messageTv;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public EasyToast(Context context) {
        super(context);
        this.context = context;
        init();

    }

    private void init() {
        View layout = LayoutInflater.from(context).inflate(R.layout.view_easy_toast, null);
        titleTv = (TextView) layout.findViewById(R.id.toast_title);
        messageTv = (TextView) layout.findViewById(R.id.toast_message);
        setGravity(Gravity.CENTER, 0, 0);
        setDuration(Toast.LENGTH_SHORT);
        setView(layout);
    }

    public void showToast(String title, String message) {
        if (!TextUtils.isEmpty(title) && TextUtils.isEmpty(message)) {
            //只有标题时,标题是白色的
            titleTv.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            titleTv.setTextColor(context.getResources().getColor(R.color.manga_reader));
        }
        if (!TextUtils.isEmpty(title)) {
            titleTv.setVisibility(View.VISIBLE);
            titleTv.setText(title);
        } else {
            titleTv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(message)) {
            messageTv.setText(message);
        } else {
            messageTv.setVisibility(View.GONE);
        }
        show();
    }

    public void showToast(String message, boolean longShow) {
        if (longShow) {
            setDuration(Toast.LENGTH_LONG);
        } else {
            setDuration(Toast.LENGTH_SHORT);
        }
        showToast("", message);
    }

    public void showToast(String message) {
        showToast("", message);
    }
}
