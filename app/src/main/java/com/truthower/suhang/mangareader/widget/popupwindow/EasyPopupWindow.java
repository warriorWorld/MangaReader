package com.truthower.suhang.mangareader.widget.popupwindow;/**
 * Created by Administrator on 2017/2/6.
 */

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.utils.DisplayUtil;


/**
 * 作者：苏航 on 2017/2/6 10:33
 * 邮箱：772192594@qq.com
 */
public class EasyPopupWindow extends PopupWindow {
    private Context context;
    private ImageView triangleIv;
    private TextView messageTv, iKnowTv;

    public EasyPopupWindow(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        View layout = LayoutInflater.from(context).inflate(R.layout.easy_popupwindow, null);
        triangleIv = (ImageView) layout.findViewById(R.id.triangle_iv);
        messageTv = (TextView) layout.findViewById(R.id.popupwindow_message);
        iKnowTv = (TextView) layout.findViewById(R.id.i_know_tv);
        iKnowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyPopupWindow.this.dismiss();
            }
        });

        setContentView(layout);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

        // TODO: 2016/5/17 设置背景颜色
        setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparency)));


        // TODO: 2016/5/17 设置可以获取焦点
        setFocusable(true);
        // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
        setOutsideTouchable(true);
        // TODO：更新popupwindow的状态
        update();
    }

    public void hideIKnowTv() {
        iKnowTv.setVisibility(View.GONE);
    }

    public void adaptiveShowAsDropDown(View anchor, int xoff, int yoff) {
        showAsDropDown(anchor, xoff, yoff);
        //13是我本身的margin 这里暂时写死吧
        triangleIv.setX(anchor.getX() + anchor.getWidth() / 2);
    }

    public void setMessage(String msg) {
        messageTv.setText(msg);
    }
}
