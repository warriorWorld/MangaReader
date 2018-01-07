package com.truthower.suhang.mangareader.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


/**
 * http://www.jianshu.com/p/0a57583a3333
 * 解决官方RecyclerView底层Bug：IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter
 * Created by Acorn on 2017/11/30.
 */
public class LinearLayoutMangerWithoutBug extends LinearLayoutManager {
    public LinearLayoutMangerWithoutBug(Context context) {
        super(context);
    }

    public LinearLayoutMangerWithoutBug(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public LinearLayoutMangerWithoutBug(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {

        }
    }
}
