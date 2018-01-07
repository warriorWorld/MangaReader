//package com.truthower.suhang.mangareader.business.user;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//
//import com.avos.avoscloud.AVException;
//import com.avos.avoscloud.AVObject;
//import com.avos.avoscloud.AVQuery;
//import com.avos.avoscloud.FindCallback;
//import com.truthower.suhang.mangareader.R;
//import com.truthower.suhang.mangareader.adapter.OnlineMangaListAdapter;
//import com.truthower.suhang.mangareader.base.BaseActivity;
//import com.truthower.suhang.mangareader.bean.LoginBean;
//import com.truthower.suhang.mangareader.bean.MangaBean;
//import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
//import com.truthower.suhang.mangareader.config.Configure;
//import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
//import com.truthower.suhang.mangareader.widget.bar.TopBar;
//import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
//import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshListView;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Created by Administrator on 2017/7/29.
// */
//
//public class CollectedActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener<ListView> {
//    private PullToRefreshListView pullListView;
//    private ListView mangaListLv;
//    private View emptyView;
//    private OnlineMangaListAdapter onlineMangaListAdapter;
//    private ArrayList<MangaBean> collectedMangaList = new ArrayList<>();
//    private TopBar topBar;
//    private int collectType;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Intent intent = getIntent();
//        collectType = intent.getIntExtra("collectType", Configure.COLLECT_TYPE_COLLECT);
//        initUI();
//        initPullListView();
//        doGetData();
//    }
//
//    private void initUI() {
//        pullListView = (PullToRefreshListView) findViewById(R.id.home_ptf);
//        mangaListLv = pullListView.getRefreshableView();
//        emptyView = findViewById(R.id.empty_view);
//        topBar = (TopBar) findViewById(R.id.gradient_bar);
//        topBar.setVisibility(View.GONE);
//
//        switch (collectType) {
//            case Configure.COLLECT_TYPE_COLLECT:
//                baseTopBar.setTitle("我的收藏");
//                break;
//            case Configure.COLLECT_TYPE_WAIT_FOR_UPDATE:
//                baseTopBar.setTitle("正在追更");
//                break;
//            case Configure.COLLECT_TYPE_FINISHED:
//                baseTopBar.setTitle("我看完的");
//                break;
//        }
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_online_manga_list;
//    }
//
//    private void doGetData() {
//        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
//            this.finish();
//            return;
//        }
//        AVQuery<AVObject> query = new AVQuery<>("Collected");
//        query.whereEqualTo("owner", LoginBean.getInstance().getUserName());
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if (LeanCloundUtil.handleLeanResult(CollectedActivity.this, e)) {
//                    collectedMangaList = new ArrayList<MangaBean>();
//                    if (null != list && list.size() > 0) {
//                        MangaBean item;
//                        for (int i = 0; i < list.size(); i++) {
//                            item = new MangaBean();
//                            item.setName(list.get(i).getString("mangaName"));
//                            item.setWebThumbnailUrl(list.get(i).getString("webThumbnailUrl"));
//                            item.setUrl(list.get(i).getString("mangaUrl"));
//
//                            switch (collectType) {
//                                case Configure.COLLECT_TYPE_COLLECT:
//                                    if (!list.get(i).getBoolean("finished")) {
//                                        collectedMangaList.add(item);
//                                    }
//                                    break;
//                                case Configure.COLLECT_TYPE_WAIT_FOR_UPDATE:
//                                    if (list.get(i).getBoolean("top")) {
//                                        collectedMangaList.add(item);
//                                    }
//                                    break;
//                                case Configure.COLLECT_TYPE_FINISHED:
//                                    if (list.get(i).getBoolean("finished")) {
//                                        collectedMangaList.add(item);
//                                    }
//                                    break;
//                            }
//                        }
//                    }
//                    initListView();
//                }
//            }
//        });
//    }
//
//    private void initPullListView() {
//        // 上拉加载更多
//        pullListView.setPullLoadEnabled(true);
//        // 滚到底部自动加载
//        pullListView.setScrollLoadEnabled(false);
//        pullListView.setOnRefreshListener(this);
//
//        mangaListLv.setCacheColorHint(0xFFCCCCCC);// 点击后颜色
//        // // mListView.setScrollBarStyle(ScrollView.);
////        mangaListLv.setDivider(getResources().getDrawable(R.color.colorAccent));// 线的颜色
//        mangaListLv.setDividerHeight(0);// 线的高度
//
////        pullListView.doPullRefreshing(true, 500);
//    }
//
//    private void initListView() {
//        if (null == onlineMangaListAdapter) {
//            onlineMangaListAdapter = new OnlineMangaListAdapter(
//                    this, collectedMangaList);
//            mangaListLv.setAdapter(onlineMangaListAdapter);
//            mangaListLv.setFocusable(true);
//            mangaListLv.setEmptyView(emptyView);
//            mangaListLv.setFocusableInTouchMode(true);
//            mangaListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(CollectedActivity.this, WebMangaDetailsActivity.class);
//                    intent.putExtra("mangaUrl", collectedMangaList.get(position).getUrl());
//                    startActivity(intent);
//                }
//            });
//            //变色太难看了
////            mangaListLv.setOnScrollListener(new AbsListView.OnScrollListener() {
////                @Override
////                public void onScrollStateChanged(AbsListView view, int scrollState) {
////
////                }
////
////                @Override
////                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
////                    topBar.computeAndsetBackgroundAlpha(getScrollY(firstVisibleItem), gradientMagicNum);
////                }
////            });
//        } else {
//            onlineMangaListAdapter.setList(collectedMangaList);
//            onlineMangaListAdapter.notifyDataSetChanged();
//        }
//        pullListView.onPullDownRefreshComplete();// 动画结束方法
//        pullListView.onPullUpRefreshComplete();
//    }
//
//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//        doGetData();
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//        pullListView.onPullDownRefreshComplete();// 动画结束方法
//        pullListView.onPullUpRefreshComplete();
//    }
//}
