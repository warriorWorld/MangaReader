package com.truthower.suhang.mangareader.widget.button;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.VibratorUtil;
import com.truthower.suhang.mangareader.widget.popupwindow.KeyboardPopupWindow;

public class GestureButton extends RelativeLayout {
    private Context context;
    private TextView gestureTv;
    private int threshold = 10;
    private OnResultListener mOnResultListener;
    private KeyboardPopupWindow mKeyboardPopupWindow;
    private char[] keys;

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
        gestureTv = (TextView) findViewById(R.id.gesture_tv);
    }

    public void setKeys(String key) {
        keys = key.toCharArray();
        gestureTv.setText(key);
        mKeyboardPopupWindow.setKeys(key);
    }

    private int dx, dy;
    private VelocityTracker vTracker = null;
    private String finalRes = "", lastFinalRes = "";

    @Override
    public boolean onTouchEvent(MotionEvent e) {
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
                mKeyboardPopupWindow.showAsDropDown(this, DisplayUtil.dip2px(context, 0), DisplayUtil.dip2px(context, -68));
                break;
            case MotionEvent.ACTION_MOVE:
                int cx = (int) e.getX();
                int cy = (int) e.getY();
                int moveX = cx - dx;
                int moveY = cy - dy;
                if (Math.abs(moveX) > Math.abs(moveY) && Math.abs(moveX) > threshold) {
                    if (moveX > 0) {
                        mKeyboardPopupWindow.toogleState(2);
                        finalRes = keys[2]+"";
                    } else {
                        mKeyboardPopupWindow.toogleState(0);
                        finalRes = keys[0]+"";
                    }
                }
                if (Math.abs(moveY) > Math.abs(moveX) && Math.abs(moveY) > threshold) {
                    if (moveY > 0) {
                        if (keys.length==4) {
                            mKeyboardPopupWindow.toogleState(3);
                            finalRes = keys[3] + "";
                        }
                    } else {
                        mKeyboardPopupWindow.toogleState(1);
                        finalRes = keys[1]+"";
                    }
                }
                if (null != mOnResultListener && !lastFinalRes.equals(finalRes)) {
                    lastFinalRes = finalRes;
                    mOnResultListener.onChange(finalRes.toLowerCase());
                    if (!SharedPreferencesUtils.getBooleanSharedPreferencesData
                            (context, ShareKeys.CLOSE_SH_KEYBOARD_VIBRATION, false)) {
                        VibratorUtil.Vibrate(context, 30);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastFinalRes = "";
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
