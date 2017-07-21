package com.truthower.suhang.mangareader.business.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.spider.MangaReaderSpider;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshListView;

import java.util.ArrayList;


public class OnlineMangaFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {
    private PullToRefreshListView pullListView;
    private SpiderBase spider;
    private ListView mangaListLv;
    private View emptyView;
    private OnlineMangaListAdapter onlineMangaListAdapter;
    //总的漫画列表和一次请求获得的漫画列表
    private ArrayList<MangaBean> totalMangaList = new ArrayList<>(),
            currentMangaList = new ArrayList<>();

    private TopBar topBar;
    private int gradientMagicNum = 500;

    private boolean isHidden = true;
    private int nowPage = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_online_manga_list, container, false);
        initUI(v);
        initPullListView();
        initSpider("MangaReader");

        doGetData();
        return v;
    }


    private void initUI(View v) {
        pullListView = (PullToRefreshListView) v.findViewById(R.id.home_ptf);
        mangaListLv = pullListView.getRefreshableView();
        emptyView = v.findViewById(R.id.empty_view);

        topBar = (TopBar) v.findViewById(R.id.gradient_bar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void initSpider(String spiderName) {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.truthower.suhang.mangareader.spider." + spiderName + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidden = hidden;
        if (!isHidden) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden) {
        }
    }

    private void doGetData() {
        spider.getMangaList("all", nowPage + "", new JsoupCallBack<MangaListBean>() {
            @Override
            public void loadSucceed(final MangaListBean result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentMangaList = result.getMangaList();
                        initListView();
                    }
                });
            }

            @Override
            public void loadFailed(String error) {

            }
        });
    }

    private void initListView() {
        totalMangaList.addAll(currentMangaList);

        if (null == onlineMangaListAdapter) {
            onlineMangaListAdapter = new OnlineMangaListAdapter(
                    getActivity(), totalMangaList);
            mangaListLv.setAdapter(onlineMangaListAdapter);
            mangaListLv.setFocusable(true);
            mangaListLv.setEmptyView(emptyView);
            mangaListLv.setFocusableInTouchMode(true);
            mangaListLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
            //变色太难看了
//            mangaListLv.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    topBar.computeAndsetBackgroundAlpha(getScrollY(firstVisibleItem), gradientMagicNum);
//                }
//            });
        } else {
            onlineMangaListAdapter.setList(totalMangaList);
            onlineMangaListAdapter.notifyDataSetChanged();
        }
        pullListView.onPullDownRefreshComplete();// 动画结束方法
        pullListView.onPullUpRefreshComplete();
    }

    public int getScrollY(int firstVisibleItem) {
        View c = mangaListLv.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int top = c.getTop();
        return -top + firstVisibleItem * gradientMagicNum;
    }

    private void initPullListView() {
        // 上拉加载更多
        pullListView.setPullLoadEnabled(true);
        // 滚到底部自动加载
        pullListView.setScrollLoadEnabled(false);
        pullListView.setOnRefreshListener(this);

        mangaListLv.setCacheColorHint(0xFFCCCCCC);// 点击后颜色
        // // mListView.setScrollBarStyle(ScrollView.);
//        mangaListLv.setDivider(getResources().getDrawable(R.color.colorAccent));// 线的颜色
        mangaListLv.setDividerHeight(0);// 线的高度

//        pullListView.doPullRefreshing(true, 500);
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        //下拉不刷新这个 暂定
//        doGetBannerData();
        nowPage = 1;
        doGetData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        nowPage += spider.nextPageNeedAddCount();
        doGetData();
    }
}
