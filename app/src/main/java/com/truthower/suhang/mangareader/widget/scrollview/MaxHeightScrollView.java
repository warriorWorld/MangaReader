package com.truthower.suhang.mangareader.widget.scrollview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ScrollView;

import com.truthower.suhang.mangareader.R;

/**
 * Created by Administrator on 2018/8/30.
 */

public class MaxHeightScrollView extends ScrollView {
    private Context mContext;
    private int maxHeight = 50;

    public MaxHeightScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaxHeightScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        if (null != attrs) {
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.max_height_scrollview);
            maxHeight = (int)ta.getDimension(R.styleable.max_height_scrollview_max_height,50);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //重新计算控件高、宽
        super.onMeasure(widthMeasureSpec, maxHeight);
    }

}
