package com.truthower.suhang.mangareader.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.OnResultListener;
import com.truthower.suhang.mangareader.utils.ThreeDESUtil;
import com.truthower.suhang.mangareader.widget.gesture.GestureLockViewGroup;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class GestureDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView titleTv;
    private ImageView crossIv;
    private GestureLockViewGroup mGestureLockViewGroup;
    private OnResultListener mOnResultListener;
    private Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (Integer.valueOf(msg.obj.toString())) {
                case 0:
                    mGestureLockViewGroup.reset();
                    break;
            }
        }
    };
    private String answer;

    public GestureDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_gesture);
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
        mGestureLockViewGroup = (GestureLockViewGroup) findViewById(R.id.id_gestureLockViewGroup);
        mGestureLockViewGroup
                .setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {

                    @Override
                    public void onUnmatchedExceedBoundary() {
                    }

                    @Override
                    public void onGestureEvent(boolean matched) {
                    }

                    @Override
                    public void onGestureEvent(String choose) {
                        if (choose.length() < 4) {
                            setGestureError();
                            return;
                        }
                        doVerifyGesture(choose);
                    }

                    @Override
                    public void onBlockSelected(int cId) {
                    }
                });

        crossIv.setOnClickListener(this);
    }

    private void doVerifyGesture(String choose) {
        if (null == mOnResultListener) {
            return;
        }
        if (ThreeDESUtil.encode(Configure.key, choose).equals(answer)) {
            dismiss();
            mOnResultListener.onFinish();
        } else {
            setGestureError();
            mOnResultListener.onFailed();
        }
    }

    private void setGestureError() {
        mGestureLockViewGroup.setErrorMode();
        mGestureLockViewGroup.invalidate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    Message msg = handler2.obtainMessage();
                    msg.obj = 0;
                    msg.sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setTitle(String title) {
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText(title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross_iv:
                dismiss();
                break;
        }
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setOnGestureLockViewListener(GestureLockViewGroup.OnGestureLockViewListener listener) {
        mGestureLockViewGroup.setOnGestureLockViewListener(listener);
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        mOnResultListener = onResultListener;
    }
}
