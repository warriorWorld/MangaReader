package com.truthower.suhang.mangareader.widget.tag;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;


public class ToggleTag extends LinearLayout {
    private Context context;
    private LinearLayout toggleTagLl;
    private TextView tagTv;
    private String tag;
    private boolean isChecked = false;

    public ToggleTag(Context context) {
        this(context, null);
    }

    public ToggleTag(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleTag(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.view_tag_with_line, this);
        toggleTagLl = (LinearLayout) findViewById(R.id.toggle_tag_ll);
        tagTv = (TextView) findViewById(R.id.tag_tv);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = !isChecked;
                toggleSelect();
            }
        });
        if (!TextUtils.isEmpty(tag)) {
            tagTv.setText(tag);
        }
    }

    private void toggleSelect() {
        if (isChecked) {
            tagTv.setTextColor(context.getResources().getColor(R.color.divide));
            toggleTagLl.setBackgroundResource(R.drawable.tag_toggle_btn_style_checked);
        } else {
            tagTv.setTextColor(context.getResources().getColor(R.color.manga_reader));
            toggleTagLl.setBackgroundResource(R.drawable.tag_toggle_btn_style);
        }
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        toggleSelect();
    }

    public void setTagTvText(String tag) {
        this.tag = tag;
        if (null != tagTv) {
            tagTv.setText(tag);
        }
    }

    public String getTagTvText() {
        return tag;
    }

    public boolean isChecked() {
        return isChecked;
    }
}
