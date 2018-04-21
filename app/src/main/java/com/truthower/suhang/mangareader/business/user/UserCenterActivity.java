package com.truthower.suhang.mangareader.business.user;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/29.
 */

public class UserCenterActivity extends BaseFragmentActivity {
    private ReplyMsgFragment replyMsgFragment;
    private UserCommentFragment userCommentFragment;
    private TabLayout tabLayout;
    private ViewPager vp;
    private MyFragmentPagerAdapter adapter;
    private String owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        owner = intent.getStringExtra("owner");
        if (TextUtils.isEmpty(owner)) {
            finish();
        }
        initUI();
        initFragment();
    }

    private void initFragment() {
        replyMsgFragment = new ReplyMsgFragment();
        userCommentFragment=new UserCommentFragment();
    }

    private void initUI() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        vp = (ViewPager) findViewById(R.id.view_pager);

        vp.setAdapter(adapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager()));
        vp.setOffscreenPageLimit(5);
        tabLayout.setupWithViewPager(vp);

        baseTopBar.setTitle(owner);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_only_vp;
    }


    /*
  setOffscreenPageLimit对此无用,全都在内存里
  FragmentPagerAdapter 继承自 PagerAdapter。相比通用的 PagerAdapter，该类更专注于每一页均为 Fragment
   的情况。如文档所述，<b>该类内的每一个生成的 Fragment 都将保存在内存之中，因此适用于那些相对静态的页</b>，数量也比
   较少的那种；如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，应该使用
   FragmentStatePagerAdapter。FragmentPagerAdapter 重载实现了几个必须的函数，因此来自 PagerAdapter
   的函数，我们只需要实现 getCount()，即可。且，由于 FragmentPagerAdapter.instantiateItem() 的实现中，
   调用了一个新增的虚函数 getItem()，因此，我们还至少需要实现一个 getItem()。因此，总体上来说，相对于继承自
   PagerAdapter，更方便一些。*/
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 2;
        private String[] pageTitle = new String[]{"回复我的","我的评论"};

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return replyMsgFragment;
                case 1:
                    return userCommentFragment;
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitle[position];
        }
    }
}
