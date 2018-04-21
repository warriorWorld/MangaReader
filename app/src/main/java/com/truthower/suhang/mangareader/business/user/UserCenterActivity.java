package com.truthower.suhang.mangareader.business.user;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
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

public class UserCenterActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    private View emptyView;
    private ArrayList<MangaBean> collectedMangaList = new ArrayList<>();
    private int collectType;
    private OnlineMangaRecyclerListAdapter adapter;
    private RecyclerView mangaRcv;
    private SwipeToLoadLayout swipeToLoadLayout;
    private TextView totalCollectTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        collectType = intent.getIntExtra("collectType", Configure.COLLECT_TYPE_COLLECT);
        initUI();
        doGetData();
    }

    private void initUI() {
        totalCollectTv = (TextView) findViewById(R.id.total_collect_tv);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        mangaRcv = (RecyclerView) findViewById(R.id.swipe_target);
        mangaRcv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mangaRcv.setFocusableInTouchMode(false);
        mangaRcv.setFocusable(false);
        mangaRcv.setHasFixedSize(true);
        TopBar topBar = (TopBar) findViewById(R.id.gradient_bar);
        topBar.setVisibility(View.GONE);

        emptyView = findViewById(R.id.empty_view);

        switch (collectType) {
            case Configure.COLLECT_TYPE_COLLECT:
                baseTopBar.setTitle("我的收藏");
                break;
            case Configure.COLLECT_TYPE_WAIT_FOR_UPDATE:
                baseTopBar.setTitle("正在追更");
                break;
            case Configure.COLLECT_TYPE_FINISHED:
                baseTopBar.setTitle("我看完的");
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.collect_manga_list;
    }

    private void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            this.finish();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(UserCenterActivity.this);
        AVQuery<AVObject> query = new AVQuery<>("Collected");
        query.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(UserCenterActivity.this, e)) {
                    collectedMangaList = new ArrayList<MangaBean>();
                    if (null != list && list.size() > 0) {
                        MangaBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new MangaBean();
                            item.setName(list.get(i).getString("mangaName"));
                            item.setWebThumbnailUrl(list.get(i).getString("webThumbnailUrl"));
                            item.setUrl(list.get(i).getString("mangaUrl"));

                            switch (collectType) {
                                case Configure.COLLECT_TYPE_COLLECT:
                                    if (!list.get(i).getBoolean("finished")) {
                                        collectedMangaList.add(item);
                                    }
                                    break;
                                case Configure.COLLECT_TYPE_WAIT_FOR_UPDATE:
                                    if (list.get(i).getBoolean("top")) {
                                        collectedMangaList.add(item);
                                    }
                                    break;
                                case Configure.COLLECT_TYPE_FINISHED:
                                    if (list.get(i).getBoolean("finished")) {
                                        collectedMangaList.add(item);
                                    }
                                    break;
                            }
                        }
                    }
                    initListView();
                }
            }
        });
    }

    private void initListView() {
        try {
            if (null == collectedMangaList || collectedMangaList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new OnlineMangaRecyclerListAdapter(this, collectedMangaList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(UserCenterActivity.this, WebMangaDetailsActivity.class);
                        intent.putExtra("mangaUrl", collectedMangaList.get(position).getUrl());
                        startActivity(intent);
                    }
                });
                mangaRcv.setAdapter(adapter);
                ColorDrawable dividerDrawable = new ColorDrawable(0x00000000) {
                    @Override
                    public int getIntrinsicHeight() {
                        return DisplayUtil.dip2px(UserCenterActivity.this, 8);
                    }

                    @Override
                    public int getIntrinsicWidth() {
                        return DisplayUtil.dip2px(UserCenterActivity.this, 8);
                    }
                };
                RecyclerGridDecoration itemDecoration = new RecyclerGridDecoration(this,
                        dividerDrawable, true);
                mangaRcv.addItemDecoration(itemDecoration);
            } else {
                adapter.setList(collectedMangaList);
                adapter.notifyDataSetChanged();
            }
            totalCollectTv.setText(collectedMangaList.size() + "");
        } catch (Exception e) {
        }
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }


    @Override
    public void onLoadMore() {
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onRefresh() {
        doGetData();
    }
}
