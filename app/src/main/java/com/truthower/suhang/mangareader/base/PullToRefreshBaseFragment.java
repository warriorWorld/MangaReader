package com.truthower.suhang.mangareader.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshListView;


public abstract class PullToRefreshBaseFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {
    protected PullToRefreshListView pullListView;
    protected ListView investLv;
    protected View emptyView;
    protected int page = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_only_pulltorefresh, container, false);
        initUI(v);
        initPullListView();
        doGetData(page + "");
        return v;
    }


    private void initUI(View v) {
        pullListView = (PullToRefreshListView) v.findViewById(R.id.pull_to_refresh);
        investLv = pullListView.getRefreshableView();
        emptyView = v.findViewById(R.id.empty_view);
    }


    protected abstract void doGetData(String page);

    protected abstract boolean needUpRefresh();

    protected abstract void initListView();

    private void initPullListView() {
        // 上拉加载更多
        pullListView.setPullLoadEnabled(true);
        // 滚到底部自动加载
        pullListView.setScrollLoadEnabled(false);
        pullListView.setOnRefreshListener(this);

        investLv.setCacheColorHint(0xFFCCCCCC);// 点击后颜色
        // // mListView.setScrollBarStyle(ScrollView.);
//        investLv.setDivider(getResources().getDrawable(R.color.colorAccent));// 线的颜色
        investLv.setDividerHeight(0);// 线的高度

//        pullListView.doPullRefreshing(true, 500);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected void noMoreDate() {
        pullListView.onPullUpRefreshComplete();
        pullListView.onPullDownRefreshComplete();
//        baseToast.showToast(getResources().getString(R.string.no_more_data));
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        page = 1;
        doGetData(page + "");
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (needUpRefresh()) {
            page++;
            doGetData(page + "");
        } else {
            pullListView.onPullUpRefreshComplete();
        }
    }
}
