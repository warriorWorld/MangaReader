package com.truthower.suhang.mangareader.business.tag;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.flexbox.FlexboxLayout;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.listener.OnEditResultListener;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaEditDialog;
import com.truthower.suhang.mangareader.widget.tag.ToggleTag;

/**
 * Created by Administrator on 2017/8/1.
 */

public class TagManagerActivity extends BaseActivity implements View.OnClickListener {
    private FlexboxLayout tagsFlexBox;
    private Button finishSelectBtn;
    private MangaEditDialog addTagDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        doGetTags();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_tag_manager;
    }

    private void initUI() {
        tagsFlexBox = (FlexboxLayout) findViewById(R.id.tag_flex_box);
        finishSelectBtn = (Button) findViewById(R.id.finish_select_btn);

        finishSelectBtn.setOnClickListener(this);
        baseTopBar.setTitle("标签管理");
        baseTopBar.setRightBackground(R.drawable.add);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                TagManagerActivity.this.finish();
            }

            @Override
            public void onRightClick() {
                showAddTagDialog();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void doGetTags() {
        refreshTags();
    }

    private void refreshTags() {

    }

    private void addNewTag(String text) {
        ToggleTag tagBtn = new ToggleTag(this);
        tagsFlexBox.addView(tagBtn);
        tagBtn.setTagTvText(text);
        tagBtn.setChecked(true);
    }


    private void showAddTagDialog() {
        addTagDialog = new MangaEditDialog(TagManagerActivity.this);
        addTagDialog.setOnEditResultListener(new OnEditResultListener() {
            @Override
            public void onResult(String text) {
                addNewTag(text);
            }

            @Override
            public void onCancelClick() {

            }
        });
        addTagDialog.show();
        addTagDialog.setTitle("添加标签");
        addTagDialog.setOkText("确定");
        addTagDialog.setCancelText("取消");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.finish_select_btn:
                break;
        }
    }
}
