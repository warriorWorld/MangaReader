package com.truthower.suhang.mangareader.business.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaListAdapter;
import com.truthower.suhang.mangareader.adapter.OnlineMangaRecyclerListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.JumpEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnEditResultListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.MatchStringUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaEditDialog;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshListView;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.WheelSelectorDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


public class OnlineMangaFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {
    private SpiderBase spider;
    private View emptyView;
    private TextView emptyTv;
    private TextView currentPageTv;
    //总的漫画列表和一次请求获得的漫画列表
    private ArrayList<MangaBean> totalMangaList = new ArrayList<>(),
            currentMangaList = new ArrayList<>();
    private OnlineMangaRecyclerListAdapter adapter;
    private RecyclerView mangaRcv;
    private SwipeToLoadLayout swipeToLoadLayout;

    private TopBar topBar;
    private int gradientMagicNum = 500;
    private String[] optionsList = {"切换站点", "搜索", "分类", "跳转"};
    private WheelSelectorDialog optionsSelector, typesSelector, webSelector;
    private MangaEditDialog searchDialog, toPageDialog;
    private int nowPage = 1, startPage = 1;
    private String nowTypeName = "all";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_online_manga_list, container, false);
        initUI(v);
        initSpider(Configure.currentWebSite);

        if (!isWifi(getActivity()) &&
                SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(), ShareKeys.ECONOMY_MODE, false)) {
            //没WiFi并且是省流量模式
            emptyTv.setText("当前未连接WiFi,如果你流量多就刷新.");
            initListView();
        } else {
            doGetData();
        }
        return v;
    }


    private void initUI(View v) {
        swipeToLoadLayout = (SwipeToLoadLayout) v.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        mangaRcv = (RecyclerView) v.findViewById(R.id.swipe_target);
        mangaRcv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mangaRcv.setFocusableInTouchMode(false);
        mangaRcv.setFocusable(false);
        mangaRcv.setHasFixedSize(true);

        emptyView = v.findViewById(R.id.empty_view);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGetData();
            }
        });
        emptyTv = (TextView) v.findViewById(R.id.empty_text);
        currentPageTv = (TextView) v.findViewById(R.id.current_page_tv);

        topBar = (TopBar) v.findViewById(R.id.gradient_bar);
        topBar.setTitle(Configure.currentWebSite);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                showOptionsSelector();
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
    }


    /**
     * make true current connect service is wifi
     *
     * @param mContext
     * @return
     */
    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    private void doGetData() {
        spider.getMangaList(nowTypeName, nowPage + "", new JsoupCallBack<MangaListBean>() {
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
        try {
            if (nowPage > startPage) {
                //如果不是首页 那就加上之后的
                totalMangaList.addAll(currentMangaList);
            } else {
                totalMangaList = currentMangaList;
            }

            if (null == totalMangaList || totalMangaList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new OnlineMangaRecyclerListAdapter(getActivity(), totalMangaList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), WebMangaDetailsActivity.class);
                        intent.putExtra("mangaUrl", totalMangaList.get(position).getUrl());
                        startActivity(intent);
                    }
                });
                mangaRcv.setAdapter(adapter);
            } else {
                adapter.setList(totalMangaList);
                adapter.notifyDataSetChanged();
            }

            int displayPage = (nowPage - 1) / spider.nextPageNeedAddCount() + 1;
            currentPageTv.setText(displayPage + "");
        } catch (Exception e) {
        }
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }


    private void showOptionsSelector() {
        if (null == optionsList || optionsList.length == 0) {
            baseToast.showToast("没有筛选条件");
            return;
        }
        if (null == optionsSelector) {
            optionsSelector = new WheelSelectorDialog(getActivity());
            optionsSelector.setCancelable(true);
        }
        optionsSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
                switch (position) {
                    case 0:
                        //切换站点
                        showWebsSelector();
                        break;
                    case 1:
                        //搜索
                        showSearchDialog("搜索漫画");
                        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(), ShareKeys.CLOSE_TUTORIAL, false)) {
                            MangaDialog dialog = new MangaDialog(getActivity());
                            dialog.show();
                            dialog.setTitle("教程");
                            dialog.setMessage("这个搜索只支持精确搜索,必须输入漫画全名(单词间空格分隔)" +
                                    "才能搜索\nPS:kakalot这个站点的搜索只能输入网址搜索");
                        }
                        break;
                    case 2:
                        //分类
                        showTypesSelector();
                        break;
                    case 3:
                        //跳转
                        showToPageDialog("跳转");
                        break;
                }
            }
        });
        optionsSelector.show();

        optionsSelector.initOptionsData(optionsList);
    }

    private void showTypesSelector() {
        if (null == typesSelector) {
            typesSelector = new WheelSelectorDialog(getActivity());
            typesSelector.setCancelable(true);
        }
        typesSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                if (spider.getMangaTypeCodes().length > 0) {
                    toggleTag(selectedRes, selectedCodeRes);
                } else {
                    toggleTag(selectedRes);
                }
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
            }
        });
        typesSelector.show();
        if (spider.getMangaTypeCodes().length > 0) {
            typesSelector.initOptionsData(spider.getMangaTypes(), spider.getMangaTypeCodes());
        } else {
            typesSelector.initOptionsData(spider.getMangaTypes());
        }
    }

    public void toggleTag(String selectedRes) {
        toggleTag(selectedRes, selectedRes);
    }

    public void toggleTag(String selectedRes, String selectedCode) {
        initToFirstPage();
        nowTypeName = selectedCode;
        topBar.setTitle(Configure.currentWebSite + "(" + selectedRes + ")");
        doGetData();
    }

    private void showWebsSelector() {
        if (null == webSelector) {
            webSelector = new WheelSelectorDialog(getActivity());
            webSelector.setCancelable(true);
        }
        webSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                initSpider(selectedRes);
                initToFirstPage();
                nowTypeName = spider.getMangaTypes()[0];
                Configure.currentWebSite = selectedRes;
                topBar.setTitle(Configure.currentWebSite + "(" + nowTypeName + ")");
                if (selectedRes.equals("KaKaLot")) {
                    baseToast.showToast("该网站中大部分漫画需要开启VPN后浏览");
                }
                doGetData();
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
            }
        });
        webSelector.show();
        if (LoginBean.getInstance().isMaster()) {
            webSelector.initOptionsData(Configure.masterWebsList);
        } else {
            webSelector.initOptionsData(Configure.websList);
        }
    }

    private void showSearchDialog(String title) {
        if (null == searchDialog) {
            searchDialog = new MangaEditDialog(getActivity());
            searchDialog.setOnEditResultListener(new OnEditResultListener() {
                @Override
                public void onResult(String text) {
                    if (!MatchStringUtil.isURL(text)) {
                        text = text.replaceAll(" ", "-");
                        text = spider.getWebUrl() + text;
                    }
                    if (!spider.isOneShot()) {
                        Intent intent = new Intent(getActivity(), WebMangaDetailsActivity.class);
                        intent.putExtra("mangaUrl", text);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelClick() {

                }
            });
            searchDialog.setCancelable(true);
        }
        searchDialog.show();
        searchDialog.setTitle(title);
        searchDialog.setHint("单词间空格分隔,如one piece");
        searchDialog.clearEdit();
    }

    private void showToPageDialog(String title) {
        if (null == toPageDialog) {
            toPageDialog = new MangaEditDialog(getActivity());
            toPageDialog.setOnEditResultListener(new OnEditResultListener() {
                @Override
                public void onResult(String text) {
                    try {
                        nowPage = (Integer.valueOf(text) - 1) * spider.nextPageNeedAddCount();
                        startPage = nowPage;
                        int actualPage = (startPage / spider.nextPageNeedAddCount()) + 1;
                        topBar.setTitle(Configure.currentWebSite + "(" + actualPage + ")");
                        doGetData();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelClick() {

                }
            });
            toPageDialog.setCancelable(true);
        }
        toPageDialog.show();
        toPageDialog.setTitle(title);
        toPageDialog.setHint("输入要跳转的页数");
        toPageDialog.setOnlyNumInput(true);
        toPageDialog.clearEdit();
    }

    private void initToFirstPage() {
        nowPage = 1;
        startPage = 1;
    }

    @Override
    public void onLoadMore() {
        nowPage += spider.nextPageNeedAddCount();
        doGetData();
    }

    @Override
    public void onRefresh() {
        initToFirstPage();
        doGetData();
    }
}
