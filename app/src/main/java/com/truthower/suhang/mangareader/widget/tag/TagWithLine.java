package com.truthower.suhang.mangareader.widget.tag;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;


public class TagWithLine extends LinearLayout {
    private Context context;
    private TextView tagTv;
    private View lineV;
    private String tag;

    public TagWithLine(Context context) {
        this(context, null);
    }

    public TagWithLine(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagWithLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.view_tag_with_line, this);
        tagTv = (TextView) findViewById(R.id.tag_tv);
        lineV = findViewById(R.id.tag_line);

        if (!TextUtils.isEmpty(tag)) {
            tagTv.setText(tag);
        }
    }

    public void toggleSelect(boolean isSelected) {
        if (isSelected) {
            tagTv.setTextColor(context.getResources().getColor(R.color.manga_reader));
            lineV.setVisibility(VISIBLE);
        } else {
            tagTv.setTextColor(context.getResources().getColor(R.color.main_text_color_gray));
            lineV.setVisibility(INVISIBLE);
        }
    }


    @Override
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        if (null != tagTv) {
            tagTv.setText(tag);
        }
    }
}
