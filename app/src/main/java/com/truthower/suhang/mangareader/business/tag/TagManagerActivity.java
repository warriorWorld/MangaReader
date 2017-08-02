package com.truthower.suhang.mangareader.business.tag;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.google.android.flexbox.FlexboxLayout;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.OnEditResultListener;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.ThreeDESUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaEditDialog;
import com.truthower.suhang.mangareader.widget.tag.ToggleTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/8/1.
 */

public class TagManagerActivity extends BaseActivity implements View.OnClickListener {
    private FlexboxLayout tagsFlexBox;
    private Button finishSelectBtn;
    private MangaEditDialog addTagDialog;
    private String imgUrl;
    private ArrayList<String> tags = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        imgUrl = intent.getStringExtra("imgUrl");

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

    /**
     * 获取所有已有标签
     */
    private void doGetTags() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            this.finish();
            return;
        }
        AVQuery<AVObject> query = new AVQuery<>("TagList");
        query.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(TagManagerActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        tags = new ArrayList<String>();
                        for (int i = 0; i < list.size(); i++) {
                            String tag = ThreeDESUtil.decode(Configure.key, list.get(i).getString("tag"));
                            addOneNewTag(tag);
                        }
                    } else {
                    }
                }
            }
        });
    }


    /**
     * 把标签加进后台表
     */
    private void addNewTag(final String text) {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            return;
        }
        AVQuery<AVObject> query1 = new AVQuery<>("TagList");
        query1.whereEqualTo("tag", text);

        AVQuery<AVObject> query2 = new AVQuery<>("TagList");
        query2.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(TagManagerActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        //已存在的不加
                    } else {
                        //新建的
                        AVObject object = new AVObject("TagList");
                        object.put("owner", LoginBean.getInstance().getUserName());
                        object.put("tag", ThreeDESUtil.encode(Configure.key, text));
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (LeanCloundUtil.handleLeanResult(TagManagerActivity.this, e)) {
                                    addOneNewTag(text);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 在页面中添加一个标签
     *
     * @param text
     */
    private void addOneNewTag(String text) {
        tags.add(text);
        ToggleTag tagBtn = new ToggleTag(TagManagerActivity.this);
        tagsFlexBox.addView(tagBtn);
        tagBtn.setTagTvText(text);
    }

    /**
     * 获取到选中的标签
     *
     * @return
     */
    private ArrayList<String> getSelectedTags() {
        ArrayList<String> selectedTags = new ArrayList<String>();
        for (int i = 0; i < tagsFlexBox.getChildCount(); i++) {
            ToggleTag toggleTag = (ToggleTag) (tagsFlexBox.getChildAt(i));
            if (toggleTag.isChecked()) {
                selectedTags.add(ThreeDESUtil.encode(Configure.key, toggleTag.getTagTvText()));
            }
        }
        return selectedTags;
    }

    /**
     * 给图片加标签
     */
    private void doImgAddTags() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            return;
        }
        AVQuery<AVObject> query1 = new AVQuery<>("Tags");
        query1.whereEqualTo("imgUrl", imgUrl);

        AVQuery<AVObject> query2 = new AVQuery<>("Tags");
        query2.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(TagManagerActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        //已存在的保存
                        AVObject object = AVObject.createWithoutData("Tags", list.get(0).getObjectId());
                        object.put("owner", LoginBean.getInstance().getUserName());
                        object.put("imgUrl", imgUrl);
                        object.addAllUnique("tags", getSelectedTags());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (LeanCloundUtil.handleLeanResult(TagManagerActivity.this, e)) {
                                    TagManagerActivity.this.finish();
                                }
                            }
                        });
                    } else {
                        //新建的
                        AVObject object = new AVObject("Tags");
                        object.put("owner", LoginBean.getInstance().getUserName());
                        object.put("imgUrl", imgUrl);
                        object.put("tags", getSelectedTags());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (LeanCloundUtil.handleLeanResult(TagManagerActivity.this, e)) {
                                    TagManagerActivity.this.finish();
                                }
                            }
                        });
                    }
                }
            }
        });
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
                doImgAddTags();
                break;
        }
    }
}
