package com.truthower.suhang.mangareader.widget.tab;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.truthower.suhang.mangareader.R;

/**
 * Created by Administrator on 2018/10/10.
 */

public class ViewPagerIndicator extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private View indicatorV;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs, defStyleAttr);
    }


    protected void initViews(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_indicator2, this);
        indicatorV = findViewById(R.id.indicator_v);
    }

    public void setupWithTabLayout(final TabLayout tableLayout) {
        mTabLayout = tableLayout;

        tableLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);
    }

    public void setupWithViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
    }

    private View getTabViewByPosition(int position) {
        if (mTabLayout != null && mTabLayout.getTabCount() > 0) {
            ViewGroup tabStrip = (ViewGroup) mTabLayout.getChildAt(0);
            return tabStrip != null ? tabStrip.getChildAt(position) : null;
        }
        return null;
    }

    private void generatePath(int position, float positionOffset) {
        View tabView = getTabViewByPosition(position);

        if (tabView == null)
            return;

        int left, right;
        left = right = 0;

        if (positionOffset > 0.f && position < mTabLayout.getTabCount() - 1) {
            View nextTabView = getTabViewByPosition(position + 1);
            left += (int) (nextTabView.getLeft() * positionOffset + tabView.getLeft() * (1.f - positionOffset));
            right += (int) (nextTabView.getRight() * positionOffset + tabView.getRight() * (1.f - positionOffset));
        } else {
            left = tabView.getLeft();
            right = tabView.getRight();
        }
        indicatorV.setTranslationX((left + right) / 2 - indicatorV.getWidth() / 2);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        generatePath(position, positionOffset);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
