package com.truthower.suhang.mangareader.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Acorn on 2016/9/13.
 */
public class RecyclerItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

    private Drawable mDivider;

    private int mOrientation;

    private int firstItemTopDecoration = 0;
    private int firstItemLeftDecoration = 0;

    public RecyclerItemDecoration(Context context, int orientation) {
        this(context, orientation, null);
    }

    public RecyclerItemDecoration(Context context, int orientation, Drawable mDivider) {
        //获取Application的style中的<item name="android:listDivider">@drawable/xxx</item>属性
        //可参考http://blog.csdn.net/lmj623565791/article/details/38173061
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        if (null == mDivider)
            this.mDivider = a.getDrawable(0);
        else {
            this.mDivider = mDivider;
        }
        a.recycle();
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView v = new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isLastRawOrCol(int pos, int childCount) {
        return pos + 1 == childCount;
    }

//    @Override
//    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        if (mOrientation == VERTICAL_LIST) {
//            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
//        } else {
//            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
//        }
//    }


    public void setFirstItemLeftDecoration(int firstItemLeftDecoration) {
        this.firstItemLeftDecoration = firstItemLeftDecoration;
    }

    public void setFirstItemTopDecoration(int firstItemTopDecoration) {
        this.firstItemTopDecoration = firstItemTopDecoration;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
//        super.getItemOffsets(outRect, itemPosition, parent);
        int childCount = parent.getAdapter().getItemCount();
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(
                    itemPosition == 0 ? firstItemLeftDecoration : 0,
                    itemPosition == 0 ? firstItemTopDecoration : 0,
                    0, isLastRawOrCol(itemPosition, childCount) ? 0 :
                            mDivider.getIntrinsicHeight());
        } else {
            outRect.set(
                    itemPosition == 0 ? firstItemLeftDecoration : 0,
                    itemPosition == 0 ? firstItemTopDecoration : 0,
                    isLastRawOrCol(itemPosition, childCount) ? 0 :
                            isLastRawOrCol(itemPosition, childCount) ? 0 : mDivider.getIntrinsicWidth(),
                    0);
        }
    }
}
