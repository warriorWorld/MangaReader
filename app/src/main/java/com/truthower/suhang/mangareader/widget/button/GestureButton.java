package com.truthower.suhang.mangareader.widget.button;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.widget.popupwindow.KeyboardPopupWindow;

public class GestureButton extends RelativeLayout {
    private Context context;
    private TextView gestureTv1;
    private TextView gestureTv2;
    private TextView gestureTv3;
    private TextView gestureTv4;
    private int threshold = 10;
    private OnResultListener mOnResultListener;
    private KeyboardPopupWindow mKeyboardPopupWindow;

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
        mKeyboardPopupWindow = new KeyboardPopupWindow(context);
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
        mKeyboardPopupWindow.setKeys(key);
    }

    private int dx, dy;
    private VelocityTracker vTracker = null;
    private String finalRes = "", lastFinalRes = "";

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
                mKeyboardPopupWindow.showAsDropDown(this,DisplayUtil.dip2px(context,10), DisplayUtil.dip2px(context,-80));
                break;
            case MotionEvent.ACTION_MOVE:
                int cx = (int) e.getX();
                int cy = (int) e.getY();
                int moveX = cx - dx;
                int moveY = cy - dy;
                if (Math.abs(moveX) > Math.abs(moveY) && Math.abs(moveX) > threshold) {
                    if (moveX > 0) {
                        mKeyboardPopupWindow.toogleState(2);
                        gestureTv3.setTextColor(context.getResources().getColor(R.color.manga_reader));
                        finalRes = gestureTv3.getText().toString();
                    } else {
                        mKeyboardPopupWindow.toogleState(0);
                        gestureTv1.setTextColor(context.getResources().getColor(R.color.manga_reader));
                        finalRes = gestureTv1.getText().toString();
                    }
                }
                if (Math.abs(moveY) > Math.abs(moveX) && Math.abs(moveY) > threshold) {
                    if (moveY > 0) {
                        mKeyboardPopupWindow.toogleState(3);
                        gestureTv4.setTextColor(context.getResources().getColor(R.color.manga_reader));
                        finalRes = gestureTv4.getText().toString();
                    } else {
                        mKeyboardPopupWindow.toogleState(1);
                        gestureTv2.setTextColor(context.getResources().getColor(R.color.manga_reader));
                        finalRes = gestureTv2.getText().toString();
                    }
                }
                if (null != mOnResultListener && !lastFinalRes.equals(finalRes)) {
                    lastFinalRes = finalRes;
                    mOnResultListener.onChange(finalRes.toLowerCase());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setBackground(getResources().getDrawable(R.drawable.item_click_white));
                if (null != mOnResultListener) {
                    mOnResultListener.onResult(finalRes.toLowerCase());
                }
                mKeyboardPopupWindow.dismiss();
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
