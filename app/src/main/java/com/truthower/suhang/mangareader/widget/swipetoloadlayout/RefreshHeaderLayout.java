package com.truthower.suhang.mangareader.widget.swipetoloadlayout;/**
 * Created by Administrator on 2017/2/20.
 */

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;
import com.truthower.suhang.mangareader.R;


/**
 * 作者：苏航 on 2017/2/20 10:53
 * 邮箱：772192594@qq.com
 */
public class RefreshHeaderLayout extends LinearLayout implements SwipeRefreshTrigger, SwipeTrigger {
    private Context context;
    private ImageView spinnerIv;

    public RefreshHeaderLayout(Context context) {
        this(context, null);
    }

    public RefreshHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh_header_silupay, this);
        spinnerIv = (ImageView) findViewById(R.id.down_spinner_iv);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onPrepare() {
    }

    @Override
    public void onMove(int i, boolean b, boolean b1) {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onReset() {

    }
}
