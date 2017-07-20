package com.truthower.suhang.mangareader.widget.scrollview;/**
 * Created by Administrator on 2016/11/2.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 作者：苏航 on 2016/11/2 11:05
 * 邮箱：772192594@qq.com
 */
public class ListenableScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener;

    public ListenableScrollView(Context context) {
        super(context);
    }

    public ListenableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public interface ScrollViewListener {

        void onScrollChanged(ListenableScrollView scrollView, int x, int y, int oldx, int oldy);

    }
}
