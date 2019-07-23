package com.truthower.suhang.mangareader.business.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.base.BaseMultiTabActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.umeng.analytics.MobclickAgent;

import androidx.fragment.app.Fragment;

/**
 * Created by Administrator on 2017/7/29.
 */

public class UserCenterActivity extends BaseMultiTabActivity {
    private ReplyMsgFragment replyMsgFragment;
    private UserCommentFragment userCommentFragment;
    private GradeListFragment gradeListFragment;
    private String owner;
    private String[] pageTitle = new String[]{"回复我的", "我的评论", "我的评分"};
    private String[] pageTitle1 = new String[]{"回复Ta的", "Ta的评论", "Ta的评分"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        owner = intent.getStringExtra("owner");
        if (TextUtils.isEmpty(owner)) {
            finish();
        }
        MobclickAgent.onEvent(this, "user_center");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initFragment() {
        replyMsgFragment = new ReplyMsgFragment();
        replyMsgFragment.setOwner(owner);
        userCommentFragment = new UserCommentFragment();
        userCommentFragment.setOwner(owner);
        gradeListFragment = new GradeListFragment();
        gradeListFragment.setOwner(owner);
    }

    @Override
    protected String getActivityTitle() {
        return owner;
    }

    @Override
    protected int getPageCount() {
        return 3;
    }

    @Override
    protected String[] getTabTitleList() {
        if (owner.equals(LoginBean.getInstance().getUserName())) {
            return pageTitle;
        } else {
            return pageTitle1;
        }
    }

    @Override
    protected Fragment getFragmentByPosition(int position) {
        switch (position) {
            case 0:
                return replyMsgFragment;
            case 1:
                return userCommentFragment;
            case 2:
                return gradeListFragment;
            default:
                return null;
        }
    }
}
