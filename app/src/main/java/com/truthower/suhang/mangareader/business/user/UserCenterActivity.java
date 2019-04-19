package com.truthower.suhang.mangareader.business.user;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaRecyclerListAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.base.BaseFragmentActivity;
import com.truthower.suhang.mangareader.base.BaseMultiTabActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.statistics.BookStatisticsFragment;
import com.truthower.suhang.mangareader.business.statistics.CalendarStatisticsFragment;
import com.truthower.suhang.mangareader.business.statistics.StatisticsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

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
