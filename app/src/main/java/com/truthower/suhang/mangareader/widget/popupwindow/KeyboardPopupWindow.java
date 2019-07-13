package com.truthower.suhang.mangareader.widget.popupwindow;/**
 * Created by Administrator on 2017/2/6.
 */

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
public class KeyboardPopupWindow extends PopupWindow {
    private Context context;
    private Button leftBtn;
    private Button rightBtn;
    private Button upBtn;
    private Button downBtn;

    public KeyboardPopupWindow(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
        View layout = LayoutInflater.from(context).inflate(R.layout.keyboard_popupwindow, null);
        leftBtn = (Button) layout.findViewById(R.id.left_btn);
        rightBtn = (Button) layout.findViewById(R.id.right_btn);
        upBtn = (Button) layout.findViewById(R.id.up_btn);
        downBtn = (Button) layout.findViewById(R.id.down_btn);

        setContentView(layout);
        setWidth(DisplayUtil.dip2px(context, 90));
        setHeight(DisplayUtil.dip2px(context, 90));

        // TODO: 2016/5/17 设置背景颜色
        setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparency)));


        // 2016/5/17 设置可以获取焦点
        setFocusable(false);
        // 2016/5/17 设置可以触摸弹出框以外的区域
        setOutsideTouchable(false);
        // 更新popupwindow的状态
        update();
    }

    public void setKeys(String key) {
        char[] keys = key.toCharArray();
        leftBtn.setText(keys[0] + "");
        upBtn.setText(keys[1] + "");
        rightBtn.setText(keys[2] + "");
        if (keys.length == 4) {
            downBtn.setVisibility(View.VISIBLE);
            downBtn.setText(keys[3] + "");
        } else {
            downBtn.setVisibility(View.INVISIBLE);
        }
    }
    public void toogleState(int pos){
        leftBtn.setBackgroundColor(context.getResources().getColor(R.color.white));
        rightBtn.setBackgroundColor(context.getResources().getColor(R.color.white));
        upBtn.setBackgroundColor(context.getResources().getColor(R.color.white));
        downBtn.setBackgroundColor(context.getResources().getColor(R.color.white));
        leftBtn.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        rightBtn.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        upBtn.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        downBtn.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        switch (pos){
            case 0:
                leftBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                leftBtn.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 1:
                upBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                upBtn.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 2:
                rightBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                rightBtn.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case 3:
                downBtn.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                downBtn.setTextColor(context.getResources().getColor(R.color.white));
                break;
        }
    }
}
