package com.truthower.suhang.mangareader.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.listener.OnSpeakClickListener;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class TranslateDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private LinearLayout titleLl;
    private TextView titleTv;
    private TextView messageTv;
    private TextView cancelTv;
    private View verticalLine;
    private TextView okTv;
    private OnSpeakClickListener onSpeakClickListener;
    private OnPeanutDialogClickListener onPeanutDialogClickListener;

    public TranslateDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_translate);
        init();

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay();
        // lp.height = (int) (d.getHeight() * 0.4);
        lp.width = (int) (d.getWidth() * 1);
        // window.setGravity(Gravity.LEFT | Gravity.TOP);
        window.setGravity(Gravity.CENTER);
//        window.getDecorView().setPadding(0, 0, 0, 0);
        // lp.x = 100;
        // lp.y = 100;
        // lp.height = 30;
        // lp.width = 20;
        window.setAttributes(lp);
    }


    private void init() {
        titleLl = (LinearLayout) findViewById(R.id.title_ll);
        titleTv = (TextView) findViewById(R.id.title_tv);
        messageTv = (TextView) findViewById(R.id.message_tv);
        cancelTv = (TextView) findViewById(R.id.cancel_tv);
        verticalLine = (View) findViewById(R.id.vertical_line);
        okTv = (TextView) findViewById(R.id.ok_tv);
        okTv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);
        messageTv.setOnClickListener(this);
        titleLl.setOnClickListener(this);
    }

    public void setTitle(String title) {
        setTitle(new SpannableString(title));
    }

    public void setTitle(SpannableString title) {
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(title);
    }

    public void setMessage(String message) {
        messageTv.setVisibility(View.VISIBLE);
        message = message.replaceAll("\\\\n", "\n");
        setMessageLeft(message.contains("\n"));
        messageTv.setText(message);
    }

    public void setMessage(SpannableString message) {
        messageTv.setVisibility(View.VISIBLE);
//        message = message.replaceAll("\\\\n", "\n");
//        setMessageLeft(message.contains("\n"));
        messageTv.setText(message);
    }

    public void setMessageColor(int color) {
        messageTv.setTextColor(color);
    }

    public void setTitleColor(int color) {
        titleTv.setTextColor(color);
    }

    public void setMessageLeft(boolean left) {
        if (left) {
            messageTv.setGravity(Gravity.LEFT);
        } else {
            messageTv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }

    public void setOkText(String text) {
        okTv.setText(text);
    }

    public void setCancelText(String text) {
        cancelTv.setVisibility(View.VISIBLE);
        cancelTv.setText(text);
    }


    public void setOnPeanutDialogClickListener(OnPeanutDialogClickListener onPeanutDialogClickListener) {
        this.onPeanutDialogClickListener = onPeanutDialogClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_tv:
                dismiss();
                if (null != onPeanutDialogClickListener) {
                    onPeanutDialogClickListener.onOkClick();
                }
                break;
            case R.id.cancel_tv:
                dismiss();
                if (null != onPeanutDialogClickListener) {
                    onPeanutDialogClickListener.onCancelClick();
                }
                break;
            case R.id.title_ll:
            case R.id.message_tv:
                if (null != onSpeakClickListener) {
                    onSpeakClickListener.onSpeakClick(titleTv.getText().toString());
                }
                break;
        }
    }

    public void setOnSpeakClickListener(OnSpeakClickListener onSpeakClickListener) {
        this.onSpeakClickListener = onSpeakClickListener;
    }


    public interface OnPeanutDialogClickListener {
        void onOkClick();

        void onCancelClick();
    }
}
