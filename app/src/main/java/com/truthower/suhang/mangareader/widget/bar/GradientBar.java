package com.truthower.suhang.mangareader.widget.bar;/**
 * Created by Administrator on 2016/10/27.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 作者：苏航 on 2016/10/27 16:58
 * 邮箱：772192594@qq.com
 */
public class GradientBar extends RelativeLayout {


    public GradientBar(Context context) {
        this(context, null);
    }

    public GradientBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 修改TopBar背景的透明度
     *
     * @alpha 透明度 取值范围在[0, 255]
     */
    public void setBackgroundAlpha(int alpha) {
        //setbackgroud能避免标题也同时变透明的情况
        //.mutate方法能够避免 直接修改资源值的问题
        this.getBackground().mutate().setAlpha(alpha);
    }

    /***
     * 计算TopBar当前的透明度
     *
     * @param currentOffset
     * @param alphaTargetOffset currentOffset == alphaTargetOffset时，透明度为1
     */
    public void computeAndsetBackgroundAlpha(float currentOffset, int alphaTargetOffset) {
        float alpha = currentOffset * 1.0f / alphaTargetOffset;
        alpha = Math.max(0, Math.min(alpha, 1));
        int alphaInt = (int) (alpha * 255);
        this.setBackgroundAlpha(alphaInt);
    }

}
