package com.truthower.suhang.mangareader.widget.pulltorefresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;


/**
 * @Title: SilupayFooterLayout.java
 * @Package: com.silupay.ebusiness.refresh
 * @Description: TODO(上拉刷新footer)
 * @author:acorn
 * @date: 2014年9月25日 上午11:17:19
 * @version: V1.0
 */
public class SilupayFooterLayout extends LoadingLayout {
    /**
     * 显示的文本
     */
    private TextView mHintView;
    private ImageView spinnerIv;
    private AnimationDrawable spinner;

    /**
     * 构造方法
     *
     * @param context context
     */
    public SilupayFooterLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 构造方法
     *
     * @param context context
     * @param attrs   attrs
     */
    public SilupayFooterLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        mHintView = (TextView) findViewById(R.id.pull_to_load_footer_hint_textview);
        spinnerIv = (ImageView) findViewById(R.id.up_spinner_iv);
        spinner = (AnimationDrawable) spinnerIv.getBackground();
        setState(State.RESET);
    }

    @Override
    protected View createLoadingView(Context context, AttributeSet attrs) {
        View container = LayoutInflater.from(context).inflate(R.layout.pull_to_load_footer_silupay, null);
        return container;
    }

    @Override
    public void setLastUpdatedLabel(CharSequence label) {
    }

    @Override
    public int getContentSize() {
        View view = findViewById(R.id.pull_to_load_footer_content);
        if (null != view) {
            return view.getHeight();
        }

        return (int) (getResources().getDisplayMetrics().density * 40);
    }

    @Override
    protected void onStateChanged(State curState, State oldState) {
        mHintView.setVisibility(INVISIBLE);
        spinner.stop();
        super.onStateChanged(curState, oldState);
    }

    @Override
    protected void onReset() {
        mHintView.setText(R.string.last_time_refresh);
    }

    @Override
    protected void onPullToRefresh() {
        mHintView.setVisibility(VISIBLE);
        mHintView.setText(R.string.up_pull_to_refresh);
    }

    @Override
    protected void onReleaseToRefresh() {
        mHintView.setVisibility(VISIBLE);
        mHintView.setText(R.string.release_to_refresh);
    }

    @Override
    protected void onRefreshing() {
        mHintView.setVisibility(VISIBLE);
        mHintView.setText(R.string.last_time_refresh);
        spinner.start();
    }

    @Override
    protected void onNoMoreData() {
        //没有更多数据了
        mHintView.setVisibility(VISIBLE);
//        mHintView.setStandard_title(R.string.pushmsg_center_no_more_msg);
    }
}
