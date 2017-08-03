package com.truthower.suhang.mangareader.widget.pulltorefresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;


/**
 * @Title: SilupayLoadingLayout.java
 * @Package: com.silupay.ebusiness.refresh
 * @Description: TODO(下拉刷新header)
 * @author:acorn
 * @date: 2014年9月25日 上午11:17:55
 * @version: V1.0
 */
public class SilupayLoadingLayout extends LoadingLayout {
    /**
     * 旋转动画的时间
     */
    static final int ROTATION_ANIMATION_DURATION = 1200;
    /**
     * 动画插值
     */
    static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();
    /**
     * Header的容器
     */
    private RelativeLayout mHeaderContainer;
    /**
     * 状态提示TextView
     */
    private TextView mHintTextView;
    /** 最后更新时间的TextView */
    // private TextView mHeaderTimeView;
    /** 最后更新时间的标题 */
    // private TextView mHeaderTimeViewTitle;
    /**
     * 旋转的动画
     */
    private Animation mRotateAnimation;
    private ImageView spinnerIv;
    private AnimationDrawable spinner;

    /**
     * 构造方法
     *
     * @param context context
     */
    public SilupayLoadingLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public SilupayLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
        mHintTextView = (TextView) findViewById(R.id.tip_tv);
        spinnerIv = (ImageView) findViewById(R.id.down_spinner_iv);
        try {
            spinner = (AnimationDrawable) spinnerIv.getBackground();
        } catch (ClassCastException e) {
            //异常就异常唄
        }

        float pivotValue = 0.5f; // SUPPRESS CHECKSTYLE
        float toDegree = 720.0f; // SUPPRESS CHECKSTYLE
        mRotateAnimation = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue,
                Animation.RELATIVE_TO_SELF, pivotValue);
        mRotateAnimation.setFillAfter(true);
        mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header_silupay, null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
        // 如果最后更新的时间的文本是空的话，隐藏前面的标题
        // mHeaderTimeViewTitle.setVisibility(TextUtils.isEmpty(label) ?
        // View.INVISIBLE : View.VISIBLE);
        // mHeaderTimeView.setStandard_title(label);
    }

    @Override
    public int getContentSize() {
        if (null != mHeaderContainer) {
            Log.i("ts", "getContentSize header height:" + mHeaderContainer.getHeight());
            return mHeaderContainer.getHeight();
        }

        Log.i("ts", "getContentSize::" + getResources().getDisplayMetrics().density);
        return (int) (getResources().getDisplayMetrics().density * 60);
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        resetRotation();
        mHintTextView.setText(R.string.last_time_refresh);
        if (null != spinner) {
            spinner.stop();
        }
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintTextView.setText(R.string.release_to_refresh);
        if (null != spinner) {
            spinner.start();
        }
    }

    @Override
    protected void onPullToRefresh() {
        mHintTextView.setText(R.string.pull_to_refresh);
    }

    @Override
    protected void onRefreshing() {
        resetRotation();
        // mArrowImageView.startAnimation(mRotateAnimation);
        mHintTextView.setText(R.string.last_time_refresh);
        if (null != spinner) {
            spinner.start();
        }
    }

    @Override
    public void onPull(float scale) {
        // Log.i("ts", "scale:"+scale);
        float angle = scale * 180f; // SUPPRESS CHECKSTYLE
        // mArrowImageView.setRotation(angle);
    }

    /**
     * 重置动画
     */
    private void resetRotation() {
        // mArrowImageView.clearAnimation();
        // mArrowImageView.setRotation(0);
    }
}
