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
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class MangaDialog extends Dialog {
    private Context context;
    private TextView titleTv, messageTv, okTv, cancelTv;
    private View verticalLine;
    private OnPeanutDialogClickListener onPeanutDialogClickListener;

    public MangaDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_manga);
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
        verticalLine = findViewById(R.id.vertical_line);
        okTv = (TextView) findViewById(R.id.ok_tv);
        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != onPeanutDialogClickListener) {
                    onPeanutDialogClickListener.onOkClick();
                }
            }
        });
        cancelTv = (TextView) findViewById(R.id.cancel_tv);
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (null != onPeanutDialogClickListener) {
                    onPeanutDialogClickListener.onCancelClick();
                }
            }
        });
        titleTv = (TextView) findViewById(R.id.title);
        messageTv = (TextView) findViewById(R.id.message);
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


    public interface OnPeanutDialogClickListener {
        void onOkClick();

        void onCancelClick();
    }
}
