package com.truthower.suhang.mangareader.widget.dragview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.VibratorUtil;


public class DragView extends android.support.v7.widget.AppCompatImageView {
    private String TAG = "DragImageView";
    private Context mContext;
    private float downX;
    private float downY;
    private long downTime;
    private int lastMotion, lastLeft, lastRight, lastTop, lastBottom, screenWidth, screenHeight;
    private boolean edged = false;
    private int marginBottom;
    private int clickThreshold = 0;
    private float xDistance, yDistance;
    private OnClickListener mOnClickListener;

    public DragView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        screenWidth = DisplayUtil.getScreenWidth(mContext);
        screenHeight = DisplayUtil.getScreenHeight(mContext);
        marginBottom = DisplayUtil.dip2px(mContext, 10);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            if ((lastMotion == MotionEvent.ACTION_UP || lastMotion == MotionEvent.ACTION_CANCEL) && edged) {
                this.layout(lastLeft, lastTop, lastRight, lastBottom);
                return;
            }
            lastLeft = left;
            lastTop = top;
            lastRight = right;
            lastBottom = bottom;
        }
//        Logger.d(TAG+":"+changed+","+left+","+top+","+right+","+bottom);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
//        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    private void toEdge() {
        if (lastLeft != 0 || lastRight != screenWidth) {
            int width = lastRight - lastLeft;
            float center = (lastLeft + lastRight) / 2f;
            if (center > screenWidth / 2f) {
                //向右
                float curTranslationX = getTranslationX();
                ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationX", curTranslationX, screenWidth - lastLeft - width);
                animator.setDuration(250);
                animator.start();
//                this.layout(screenWidth - width, lastTop, screenWidth, lastBottom);
//                String save = (int)(screenWidth - width) + ";" + (int)getY();
                String save = (int) (screenWidth - width) + ";" + (int) lastTop + ";" + (int) screenWidth + ";" + (int) lastBottom;
                SharedPreferencesUtils.setSharedPreferencesData(mContext, ShareKeys.LAST_DRAGVIEW_POSITION, save);
            } else {
                //向左
                float curTranslationX = getTranslationX();
                ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationX", curTranslationX, -lastLeft);
                animator.setDuration(250);
                animator.start();
//                this.layout(0, lastTop, width, lastBottom);
//                String save = 0 + ";" + (int)getY();
                String save = 0 + ";" + (int) lastTop + ";" + (int) width + ";" + (int) lastBottom;
                SharedPreferencesUtils.setSharedPreferencesData(mContext, ShareKeys.LAST_DRAGVIEW_POSITION, save);
            }
        }
        edged = true;
    }

    public void toLastPosition() {
        String s = SharedPreferencesUtils.getSharedPreferencesData(mContext, ShareKeys.LAST_DRAGVIEW_POSITION);
        if (TextUtils.isEmpty(s)) {
            return;
        }
        String[] ss = s.split(";");
        if (ss.length <= 0) {
            return;
        }

        try {
            this.layout(Integer.valueOf(ss[0]), Integer.valueOf(ss[1]), Integer.valueOf(ss[2]), Integer.valueOf(ss[3]));
            lastMotion=MotionEvent.ACTION_UP;
            edged=true;

//            setX(Float.valueOf(ss[0]));
//            setY(Float.valueOf(ss[1]));

//            ObjectAnimator animator = ObjectAnimator.ofFloat(this, "translationX", getTranslationX(), Integer.valueOf(ss[0]));
//            animator.setDuration(250);
//            animator.start();
//            ObjectAnimator animator1 = ObjectAnimator.ofFloat(this, "translationY", getTranslationY(), Integer.valueOf(ss[1]) - getY());
//            animator1.setDuration(250);
//            animator1.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Logger.d(TAG+":onTouchEvent");
        super.onTouchEvent(event);
        if (this.isEnabled()) {
            edged = false;
            lastMotion = event.getAction();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    VibratorUtil.Vibrate(mContext, 30);
                    downX = event.getX();
                    downY = event.getY();
                    xDistance = 0;
                    yDistance = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    xDistance = event.getX() - downX;
                    yDistance = event.getY() - downY;
                    if (xDistance != 0 && yDistance != 0) {
                        int l = (int) (getLeft() + xDistance);
                        int r = (int) (getRight() + xDistance);
                        int t = (int) (getTop() + yDistance);
                        int b = (int) (getBottom() + yDistance);
                        if (l < 0) {
                            l = 0;
                            r = (int) (getRight() - getLeft());
                        }
                        if (r > screenWidth) {
                            r = screenWidth;
                            l = (int) (getLeft() - getRight() + screenWidth);
                        }
                        if (t < 0) {
                            t = 0;
                            b = (int) (getBottom() - getTop());
                        }
                        if (b > screenHeight - marginBottom) {
                            b = screenHeight - marginBottom;
                            t = (int) (getTop() - getBottom() + screenHeight - marginBottom);
                        }
                        this.layout(l, t, r, b);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(xDistance) <= clickThreshold && Math.abs(yDistance) <= clickThreshold && null != mOnClickListener) {
                        mOnClickListener.onClick(this);
                    }
                    setPressed(false);
                    toEdge();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    setPressed(false);
                    toEdge();
                    break;
            }
            return true;
        }
        return false;
    }
}
