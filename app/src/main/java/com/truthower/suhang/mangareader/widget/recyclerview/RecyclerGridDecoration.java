package com.truthower.suhang.mangareader.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.truthower.suhang.mangareader.R;


/**
 * Created by Acorn on 2016/9/18.
 */
public class RecyclerGridDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;
    private boolean isDrawLastRawAndCol;
    private boolean hasHeader;

    private int firstItemTopDecoration = 0;

    public RecyclerGridDecoration(Context context) {
        this(context, null);
    }

    public RecyclerGridDecoration(Context context, Drawable mDivider) {
        //获取Application的style中的<item name="android:listDivider">@drawable/xxx</item>属性
        //可参考http://blog.csdn.net/lmj623565791/article/details/38173061
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        if (null == mDivider)
            this.mDivider = a.getDrawable(0);
        else {
            this.mDivider = mDivider;
        }
        a.recycle();
    }

    public RecyclerGridDecoration(Context context, Drawable mDivider, boolean header) {
        //获取Application的style中的<item name="android:listDivider">@drawable/xxx</item>属性
        //可参考http://blog.csdn.net/lmj623565791/article/details/38173061
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        if (null == mDivider)
            this.mDivider = a.getDrawable(0);
        else {
            this.mDivider = mDivider;
        }
        a.recycle();
        hasHeader = header;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        drawHorizontal(c, parent);
        drawVertical(c, parent);

    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager)
                    .getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin;
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin + mDivider.getIntrinsicHeight();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isFirstColumn(RecyclerView parent, int pos, int spanCount) {
        //TODO 暂时没用到流式布局的,所以没写StaggeredGridLayoutManager
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (pos % spanCount == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount,
                                 int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
            {
                return true;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
                {
                    return true;
                }
            } else {
                childCount = childCount - childCount % spanCount;
                if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
                    return true;
            }
        }
        return false;
    }

    private boolean isFirstRaw(RecyclerView parent, int pos, int spanCount) {
        //TODO 暂时没用到流式布局的,所以没写StaggeredGridLayoutManager
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (pos < spanCount)
                return true;
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
                              int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            if (hasHeader) {
                pos = pos - 1;
            }
            childCount = childCount - childCount % spanCount;
            if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
                return true;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager)
                    .getOrientation();
            // StaggeredGridLayoutManager 且纵向滚动
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                // 如果是最后一行，则不需要绘制底部
                if (pos >= childCount)
                    return true;
            } else
            // StaggeredGridLayoutManager 且横向滚动
            {
                // 如果是最后一行，则不需要绘制底部
                if ((pos + 1) % spanCount == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setFirstItemTopDecoration(int firstItemTopDecoration) {
        this.firstItemTopDecoration = firstItemTopDecoration;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int pos = position;
        if (hasHeader) {
            if (position == 0) {
                outRect.set(0, 0, 0, 0);
                return;
            } else {
                pos = position - 1;
            }
        }
        //多少列
        int spanCount = getSpanCount(parent);
        //总共多少item
        int childCount = parent.getAdapter().getItemCount();
        int hPadding = mDivider.getIntrinsicWidth() / 2;
        int vPadding = mDivider.getIntrinsicHeight() / 2;
        outRect.left = hPadding;
        outRect.right = hPadding;
        outRect.top = vPadding;
        outRect.bottom = vPadding;
        if (isFirstRaw(parent, pos, spanCount))
            outRect.top = firstItemTopDecoration;
        if (isLastRaw(parent, pos, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
            outRect.bottom = 0;
        if (isFirstColumn(parent, pos, spanCount))
            outRect.left = 0;
        if (isLastColumn(parent, pos, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
            outRect.right = 0;
    }
}
