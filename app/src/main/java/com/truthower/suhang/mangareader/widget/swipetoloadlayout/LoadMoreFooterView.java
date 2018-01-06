package com.truthower.suhang.mangareader.widget.swipetoloadlayout;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;
import com.truthower.suhang.mangareader.R;

/**
 * Created by Administrator on 2018/1/6.
 */

public class LoadMoreFooterView extends LinearLayout implements SwipeLoadMoreTrigger, SwipeTrigger {
    private Context context;
    private ImageView spinnerIv;
    private AnimationDrawable spinner;

    public LoadMoreFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.pull_to_load_footer_silupay, this);
        spinnerIv = (ImageView) findViewById(R.id.up_spinner_iv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            spinnerIv.setBackgroundResource(R.drawable.down_spinner);
        }
        spinner = (AnimationDrawable) spinnerIv.getBackground();
    }

    @Override
    public void onPrepare() {
        if (null != spinner) {
            spinner.start();
        }
    }

    @Override
    public void onMove(int i, boolean b, boolean b1) {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        if (null != spinner) {
            spinner.stop();
        }
    }

    @Override
    public void onReset() {

    }

    @Override
    public void onLoadMore() {

    }
}
