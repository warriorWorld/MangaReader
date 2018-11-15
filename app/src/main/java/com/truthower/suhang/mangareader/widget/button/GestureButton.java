package com.truthower.suhang.mangareader.widget.button;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.StarAdapter;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;

import java.util.ArrayList;

public class GestureButton extends RelativeLayout {
    private Context context;
    private TextView gestureTv1;
    private TextView gestureTv2;
    private TextView gestureTv3;
    private TextView gestureTv4;
    private int threshold = 10;
    private OnResultListener mOnResultListener;

    public GestureButton(Context context) {
        this(context, null);
    }

    public GestureButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.btn_gesture, this);
        setBackground(getResources().getDrawable(R.drawable.item_click_white));
        gestureTv1 = (TextView) findViewById(R.id.gesture_tv1);
        gestureTv2 = (TextView) findViewById(R.id.gesture_tv2);
        gestureTv3 = (TextView) findViewById(R.id.gesture_tv3);
        gestureTv4 = (TextView) findViewById(R.id.gesture_tv4);
    }

    public void setKeys(String key) {
        char[] keys = key.toCharArray();
        gestureTv1.setText(keys[0] + "");
        gestureTv2.setText(keys[1] + "");
        gestureTv3.setText(keys[2] + "");
        if (keys.length == 4) {
            gestureTv4.setText(keys[3] + "");
        }
    }

    private int dx, dy;
    private VelocityTracker vTracker = null;
    private String finalRes = "";

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        gestureTv1.setTextColor(context.getResources().getColor(R.color.main_text_color));
        gestureTv2.setTextColor(context.getResources().getColor(R.color.main_text_color));
        gestureTv3.setTextColor(context.getResources().getColor(R.color.main_text_color));
        gestureTv4.setTextColor(context.getResources().getColor(R.color.main_text_color));
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackground(getResources().getDrawable(R.drawable.item_click_gray));
                if (vTracker == null) {
                    vTracker = VelocityTracker.obtain();
                } else {
                    vTracker.clear();
                }
                vTracker.addMovement(e);
                dx = (int) e.getX();
                dy = (int) e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int cx = (int) e.getX();
                int cy = (int) e.getY();
                int moveX = cx - dx;
                int moveY = cy - dy;
                if (Math.abs(moveX) > Math.abs(moveY) && Math.abs(moveX) > threshold) {
                    if (moveX > 0) {
                        gestureTv3.setTextColor(context.getResources().getColor(R.color.manga_reader));
                        finalRes = gestureTv3.getText().toString();
                    } else {
                        gestureTv1.setTextColor(context.getResources().getColor(R.color.manga_reader));
                        finalRes = gestureTv1.getText().toString();
                    }
                }
                if (Math.abs(moveY) > Math.abs(moveX) && Math.abs(moveY) > threshold) {
                    if (moveY > 0) {
                        gestureTv4.setTextColor(context.getResources().getColor(R.color.manga_reader));
                        finalRes = gestureTv4.getText().toString();
                    } else {
                        gestureTv2.setTextColor(context.getResources().getColor(R.color.manga_reader));
                        finalRes = gestureTv2.getText().toString();
                    }
                }
                if (null != mOnResultListener) {
                    mOnResultListener.onChange(finalRes.toLowerCase());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setBackground(getResources().getDrawable(R.drawable.item_click_white));
                if (null != mOnResultListener) {
                    mOnResultListener.onResult(finalRes.toLowerCase());
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setOnResultListener(OnResultListener onResultListener) {
        mOnResultListener = onResultListener;
    }

    public interface OnResultListener {
        //输入完一个字母
        void onResult(String result);

        //用户停留在一个字母
        void onChange(String result);
    }
}
