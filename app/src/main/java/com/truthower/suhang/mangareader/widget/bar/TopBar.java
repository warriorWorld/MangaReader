package com.truthower.suhang.mangareader.widget.bar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;


public class TopBar extends GradientBar {
    private ImageView rightIV;
    private TextView rightTV;
    private ImageView leftIV;
    private TextView leftTV;
    private View rightLayout;
    private View leftLayout;
    private ImageView titleIv;
    private TextView tipTV;
    private View topBarLayout;
    private Drawable topBarBg;
    private OnTopBarClickListener l;
    private OnTopBarLongClickListener ll;

    public TopBar(Context context) {
        super(context);
        init();
    }

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.home_top_bar);
        topBarBg = ta.getDrawable(R.styleable.home_top_bar_topbar_background);

        Drawable leftBg = ta.getDrawable(R.styleable.home_top_bar_left_background);

        Drawable rightBg = ta.getDrawable(R.styleable.home_top_bar_right_background);

        Drawable titleSrc = ta.getDrawable(R.styleable.home_top_bar_title_src);

        Drawable leftLayoutBg = ta
                .getDrawable(R.styleable.home_top_bar_left_press_background);

        float rightTextSize = ta.getDimension(
                R.styleable.home_top_bar_right_textSize, 14);
        float leftTextSize = ta.getDimension(R.styleable.home_top_bar_left_textSize,
                18);
        String rightText = ta.getString(R.styleable.home_top_bar_right_text);
        String leftText = ta.getString(R.styleable.home_top_bar_left_text);
        int rightTextColor = ta.getColor(R.styleable.home_top_bar_right_textColor,
                Color.WHITE);
        int leftTextColor = ta.getColor(R.styleable.home_top_bar_left_textColor,
                Color.WHITE);
        float titleTextSize = ta.getDimension(
                R.styleable.home_top_bar_title_textSize, 22);
        String titleText = ta.getString(R.styleable.home_top_bar_title_text);
        int titleTextColor = ta.getColor(R.styleable.home_top_bar_title_textColor,
                Color.WHITE);
        ta.recycle();
        if (null != leftBg)
            leftIV.setImageDrawable(leftBg);
        if (null != rightBg) {
            rightIV.setImageDrawable(rightBg);
        }
        rightTV.setText(rightText);
        rightTV.setTextColor(rightTextColor);
        rightTV.setTextSize(rightTextSize);
        leftTV.setText(leftText);
        leftTV.setTextColor(leftTextColor);
        leftTV.setTextSize(leftTextSize);
        tipTV.setText(titleText);
        tipTV.setTextSize(titleTextSize);
        tipTV.setTextColor(titleTextColor);
        if (null == rightBg && TextUtils.isEmpty(rightText)) {
            rightLayout.setVisibility(View.GONE);
        } else {
            rightLayout.setVisibility(View.VISIBLE);
        }
        if (null == leftBg && TextUtils.isEmpty(leftText)) {
            leftLayout.setVisibility(View.GONE);
        } else {
            leftLayout.setVisibility(View.VISIBLE);
        }

        if (null == titleSrc) {
            titleIv.setVisibility(View.GONE);
        } else {
            titleIv.setImageDrawable(titleSrc);
        }

        if (null != topBarBg) {
            topBarLayout.setBackgroundDrawable(topBarBg);
        }
        if (null != leftLayoutBg) {
            leftLayout.setBackgroundDrawable(leftLayoutBg);
        }
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_widget_topbar, this);

        leftIV = (ImageView) findViewById(R.id.left_iv);
        tipTV = (TextView) findViewById(R.id.title_tv);
        rightIV = (ImageView) findViewById(R.id.right_iv);
        rightTV = (TextView) findViewById(R.id.right_tv);
        leftTV = (TextView) findViewById(R.id.left_tv);
        rightLayout = findViewById(R.id.right_layout);
        leftLayout = findViewById(R.id.left_layout);
        titleIv = (ImageView) findViewById(R.id.title_iv);
        topBarLayout = findViewById(R.id.topbar_layout);

        leftLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != l)
                    l.onLeftClick();
            }
        });
        rightLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != l)
                    l.onRightClick();
            }
        });
        tipTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != l)
                    l.onTitleClick();
            }
        });
        tipTV.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != ll)
                    ll.onTitleLongClick();
                return true;
            }
        });
        leftLayout.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != ll)
                    ll.onLeftLongClick();
                return true;
            }
        });
        rightLayout.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (null != ll)
                    ll.onRightLongClick();
                return true;
            }
        });
    }

    public void setOnTopBarClickListener(OnTopBarClickListener l) {
        this.l = l;
    }

    public void setTopBarBackground(int color) {
        // TODO
        topBarLayout.setBackgroundColor(color);
    }

    public void hideTitle(boolean hide) {
        if (hide) {
            titleIv.setVisibility(INVISIBLE);
        } else {
            titleIv.setVisibility(VISIBLE);
        }
    }

    public void setLeftBackground(int bg) {
        leftLayout.setVisibility(View.VISIBLE);
        leftIV.setImageResource(bg);
    }

    public void setLeftButton(View.OnClickListener l) {
        leftLayout.setVisibility(View.VISIBLE);
        leftLayout.setOnClickListener(l);
    }

    public void setRightButton(View.OnClickListener l) {
        rightLayout.setVisibility(View.VISIBLE);
        rightLayout.setOnClickListener(l);
    }

    public void showLeftButton() {
        leftLayout.setVisibility(View.VISIBLE);
    }

    public void showRightButton() {
        rightLayout.setVisibility(View.VISIBLE);
    }

    public void hideLeftButton() {
        leftLayout.setVisibility(View.INVISIBLE);
    }

    public void hideRightButton() {
        rightLayout.setVisibility(View.INVISIBLE);
    }

    public void setRightBackground(int bg) {
        rightLayout.setVisibility(VISIBLE);
        rightIV.setVisibility(View.VISIBLE);
        rightIV.setImageResource(bg);
        rightTV.setVisibility(View.INVISIBLE);
    }

    public void setTitle(String title) {
        tipTV.setText(title);
    }

    public String getTitle() {
        return tipTV.getText().toString();
    }


    public void setRightText(String text) {
        rightTV.setVisibility(View.VISIBLE);
        rightTV.setText(text);
        rightIV.setVisibility(View.INVISIBLE);
        showRightButton();
    }

    public void setLeftText(String text) {
        leftTV.setVisibility(View.VISIBLE);
        leftTV.setText(text);
        leftIV.setVisibility(View.INVISIBLE);
        showLeftButton();
    }

    public String getLeftText() {
        return leftTV.getText().toString();
    }

    public void setTopBarLongClickLister(OnTopBarLongClickListener ll) {
        this.ll = ll;
    }

    public interface OnTopBarClickListener {
        public void onLeftClick();

        public void onRightClick();

        public void onTitleClick();
    }

    public interface OnTopBarLongClickListener {
        public void onLeftLongClick();

        public void onRightLongClick();


        public void onTitleLongClick();
    }
}
