package com.truthower.suhang.mangareader.business.comment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.CommentAdapter;
import com.truthower.suhang.mangareader.adapter.OnlineMangaRecyclerListAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.CommentBean;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.OnCommenttemClickListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;
import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/29.
 */

public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private ArrayList<CommentBean> commentList = new ArrayList<>();
    private CommentAdapter adapter;
    private RecyclerView commentRcv;
    private String mangaName;
    private EditText commentEt;
    private TextView sentCommentTv;
    private String replyUser = "";
    private String mangaUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mangaName = intent.getStringExtra("mangaName");
        mangaUrl = intent.getStringExtra("mangaUrl");
        initUI();
        doGetData();
    }

    private void initUI() {
        commentRcv = (RecyclerView) findViewById(R.id.only_rcv);
        commentRcv.setLayoutManager
                (new LinearLayoutMangerWithoutBug
                        (this, LinearLayoutManager.VERTICAL, false));
        commentRcv.setFocusableInTouchMode(false);
        commentRcv.setFocusable(false);
        commentRcv.setHasFixedSize(true);
        commentEt = (EditText) findViewById(R.id.comment_et);
        sentCommentTv = (TextView) findViewById(R.id.sent_comment_tv);

        sentCommentTv.setOnClickListener(this);
        baseTopBar.setTitle("评论");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment;
    }

    private void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            this.finish();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(CommentActivity.this);
        AVQuery<AVObject> query = new AVQuery<>("Comment");
        query.whereEqualTo("mangaName", mangaName);
        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(CommentActivity.this, e)) {
                    commentList = new ArrayList<>();
                    if (null != list && list.size() > 0) {
                        CommentBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new CommentBean();
                            item.setCreate_at(list.get(i).getCreatedAt());
                            item.setMangaName(list.get(i).getString("mangaName"));
                            item.setMangaUrl(list.get(i).getString("mangaUrl"));
                            item.setOo_number(list.get(i).getInt("oo_number"));
                            item.setXx_number(list.get(i).getInt("xx_number"));
                            item.setReply_user(list.get(i).getString("reply_user"));
                            item.setOwner(list.get(i).getString("owner"));
                            item.setComment_content(list.get(i).getString("comment_content"));
                            item.setObjectId(list.get(i).getObjectId());
                            commentList.add(item);
                        }
                    }
                    initListView();
                }
            }
        });
    }

    private void initListView() {
        try {
            if (null == adapter) {
                adapter = new CommentAdapter(this, commentList);
                adapter.setOnCommenttemClickListener(new OnCommenttemClickListener() {
                    @Override
                    public void onOOClick(int position) {
                        doOO(commentList.get(position).getObjectId());
                    }

                    @Override
                    public void onXXClick(int position) {
                        doXX(commentList.get(position).getObjectId());
                    }

                    @Override
                    public void onUserNameClick(int position) {

                    }

                    @Override
                    public void onReplyClick(int position) {

                    }
                });
                commentRcv.setAdapter(adapter);
            } else {
                adapter.setDatas(commentList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }

    private void doComment() {
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(CommentActivity.this);

        AVObject object = new AVObject("Comment");
        object.put("owner", userName);
        object.put("reply_user", replyUser);
        object.put("mangaUrl", mangaUrl);
        object.put("mangaName", mangaName);
        object.put("comment_content", commentEt.getText().toString().trim());
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(CommentActivity.this, e)) {
                    commentEt.setText("");
                    closeKeyBroad();
                    doGetData();
                }
            }
        });
    }

    private void doOO(String objId) {
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(this,
                ShareKeys.COMMENT_OOXX_KEY + objId, false)) {
            baseToast.showToast("你已经点过一次了...");
            return;
        }
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        SharedPreferencesUtils.setSharedPreferencesData
                (this, ShareKeys.COMMENT_OOXX_KEY + objId, true);
        SingleLoadBarUtil.getInstance().showLoadBar(CommentActivity.this);

        AVQuery query = new AVQuery("Comment");
        query.whereEqualTo("objectId", objId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(CommentActivity.this, e)) {
                    commentList = new ArrayList<>();
                    if (null != list && list.size() > 0) {
                        list.get(0).increment("oo_number");
                        list.get(0).setFetchWhenSave(true);
                        list.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                doGetData();
                            }
                        });
                    }
                }
            }
        });
    }

    private void doXX(String objId) {
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(this,
                ShareKeys.COMMENT_OOXX_KEY + objId, false)) {
            baseToast.showToast("你已经点过一次了...");
            return;
        }
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        SharedPreferencesUtils.setSharedPreferencesData
                (this, ShareKeys.COMMENT_OOXX_KEY + objId, true);
        SingleLoadBarUtil.getInstance().showLoadBar(CommentActivity.this);

        AVQuery query = new AVQuery("Comment");
        query.whereEqualTo("objectId", objId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(CommentActivity.this, e)) {
                    commentList = new ArrayList<>();
                    if (null != list && list.size() > 0) {
                        list.get(0).increment("xx_number");
                        list.get(0).setFetchWhenSave(true);
                        list.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                doGetData();
                            }
                        });
                    }
                }
            }
        });
    }

    public void showKeyBroad() {
        // 自动弹出键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(translateET, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void closeKeyBroad() {
        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(commentEt, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(commentEt.getWindowToken(), 0);
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(commentEt.getText().toString().trim())) {
            baseToast.showToast("不能为空!");
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sent_comment_tv:
                if (checkData()) {
                    doComment();
                }
                break;
        }
    }
}
