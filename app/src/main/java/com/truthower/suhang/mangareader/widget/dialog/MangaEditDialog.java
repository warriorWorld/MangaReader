package com.truthower.suhang.mangareader.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.truthower.suhang.mangareader.R;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class MangaEditDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private EditText editTextV;
    private TextView titleTv;
    private ImageView crossIv;
    private TextView messageTv;
    private TextView cancelTv;
    private TextView okTv;

    private OnPeanutEditDialogClickListener onPeanutEditDialogClickListener;

    public MangaEditDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_manga);
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
        titleTv = (TextView) findViewById(R.id.title_tv);
        crossIv = (ImageView) findViewById(R.id.cross_iv);
        messageTv = (TextView) findViewById(R.id.message_tv);
        editTextV = (EditText) findViewById(R.id.edit_et);
        okTv = (TextView) findViewById(R.id.ok_tv);
        cancelTv = (TextView) findViewById(R.id.cancel_tv);

        crossIv.setOnClickListener(this);
        okTv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);

    }

    public void setTitle(String title) {
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(title);
    }

    public void setMessage(String message) {
        messageTv.setVisibility(View.VISIBLE);
        messageTv.setText(message);
    }

    public void setHint(String message) {
        editTextV.setHint(message);
    }

    public void setOnlyNumInput(boolean onlyNumInput) {
        if (onlyNumInput) {
            editTextV.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            editTextV.setInputType(InputType.TYPE_CLASS_TEXT);
        }
    }

    public void setOkText(String text) {
        okTv.setText(text);
    }

    public void setCancelText(String text) {
        cancelTv.setVisibility(View.VISIBLE);
        cancelTv.setText(text);
    }

    public void clearEdit() {
        editTextV.setText("");
    }

    public void setOnPeanutEditDialogClickListener(OnPeanutEditDialogClickListener onPeanutEditDialogClickListener) {
        this.onPeanutEditDialogClickListener = onPeanutEditDialogClickListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross_iv:
                dismiss();
                break;
            case R.id.cancel_tv:
                dismiss();
                if (null != onPeanutEditDialogClickListener) {
                    onPeanutEditDialogClickListener.onCancelClick();
                }
                break;
            case R.id.ok_tv:
                if (TextUtils.isEmpty(editTextV.getText().toString())) {
                    Toast.makeText(context, "空的啊", Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                if (null != onPeanutEditDialogClickListener) {
                    onPeanutEditDialogClickListener.onOkClick(editTextV.getText().toString());
                }
                break;
        }
    }


    public interface OnPeanutEditDialogClickListener {
        void onOkClick(String text);

        void onCancelClick();
    }
}
