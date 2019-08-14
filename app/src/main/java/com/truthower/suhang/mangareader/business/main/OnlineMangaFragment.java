package com.truthower.suhang.mangareader.business.main;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.insightsurfface.stylelibrary.keyboard.KeyBoardDialog;
import com.insightsurfface.stylelibrary.listener.OnKeyboardChangeListener;
import com.insightsurfface.stylelibrary.listener.OnKeyboardListener;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.other.KeyboardSettingActivity;
import com.truthower.suhang.mangareader.business.search.SearchActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnEditResultListener;
import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.test.TestActivity;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.PermissionUtil;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.ListDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaEditDialog;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshListView;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.SingleSelectorDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;


public class OnlineMangaFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener<ListView> {
    private PullToRefreshListView pullListView;
    private SpiderBase spider;
    private ListView mangaListLv;
    private View emptyView;
    private TextView emptyTv;
    private TextView currentPageTv;
    private OnlineMangaListAdapter onlineMangaListAdapter;
    //总的漫画列表和一次请求获得的漫画列表
    private ArrayList<MangaBean> totalMangaList = new ArrayList<>(),
            currentMangaList = new ArrayList<>();

    private TopBar topBar;
    private int gradientMagicNum = 500;
    private String[] optionsList = {"切换站点", "搜索", "分类", "跳转"};
    private SingleSelectorDialog  typesSelector, webSelector;
    private MangaEditDialog searchDialog, toPageDialog;
    private int startPage = 1;
    private boolean onLoadingMore = false;
    private ClipboardManager clip;//复制文本用

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        clip = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        View v = inflater.inflate(R.layout.fragment_online_manga_list, container, false);
        initUI(v);
        initPullListView();

        initSpider(BaseParameterUtil.getInstance().getCurrentWebSite(getActivity()));

        if (!isWifi(getActivity()) &&
                SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(), ShareKeys.ECONOMY_MODE, false)) {
            //没WiFi并且是省流量模式
            emptyTv.setText("当前未连接WiFi,如果你流量多就刷新.");
            initListView();
        } else {
            if (null != ShareObjUtil.getObject(getActivity(), ShareKeys.MAIN_PAGE_CHCHE)) {
                try {
                    currentMangaList = (ArrayList<MangaBean>) ShareObjUtil.getObject(getActivity(), ShareKeys.MAIN_PAGE_CHCHE);
                    initListView();
                    refreshTopBarTitle();
                } catch (Exception e) {
                    doGetData();
                }
            } else {
                doGetData();
            }
        }
        return v;
    }


    private void initUI(View v) {
        pullListView = (PullToRefreshListView) v.findViewById(R.id.home_ptf);
        mangaListLv = pullListView.getRefreshableView();
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
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                showOptionsSelectorDialog();
            }

            @Override
            public void onTitleClick() {
                showKeyboardDialog();
            }
        });
        topBar.setTopBarLongClickLister(new TopBar.OnTopBarLongClickListener() {
            @Override
            public void onLeftLongClick() {

            }

            @Override
            public void onRightLongClick() {

            }

            @Override
            public void onTitleLongClick() {
                if (Configure.isTest) {
                    Intent intent = new Intent(getActivity(), TestActivity.class);
                    startActivity(intent);
                }
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

    private void showKeyboardDialog() {
        KeyBoardDialog dialog = new KeyBoardDialog(getActivity());
        dialog.setOnKeyboardChangeListener(new OnKeyboardChangeListener() {
            @Override
            public void onChange(String res) {
            }

            @Override
            public void onFinish(String res) {
                clip.setText(res);
            }
        });
        dialog.setOnKeyboardListener(new OnKeyboardListener() {
            @Override
            public void onOptionsClick() {
                Intent intent = new Intent(getActivity(), KeyboardSettingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onQuestionClick() {
                baseToast.showToast("点按后滑动输入");
            }
        });
        dialog.show();
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
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        spider.getMangaList(BaseParameterUtil.getInstance().getCurrentType(getActivity()),
                BaseParameterUtil.getInstance().getCurrentPage(getActivity()) + "", new JsoupCallBack<MangaListBean>() {
                    @Override
                    public void loadSucceed(final MangaListBean result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SingleLoadBarUtil.getInstance().dismissLoadBar();
                                currentMangaList = result.getMangaList();
                                ShareObjUtil.saveObject(getActivity(), currentMangaList, ShareKeys.MAIN_PAGE_CHCHE);
                                initListView();
                                refreshTopBarTitle();
                            }
                        });
                    }

                    @Override
                    public void loadFailed(String error) {

                    }
                });
    }

    private void initListView() {
        pullListView.onPullDownRefreshComplete();// 动画结束方法
        pullListView.onPullUpRefreshComplete();

        if (BaseParameterUtil.getInstance().getCurrentPage(getActivity()) > startPage) {
            //如果不是首页 那就加上之后的
            totalMangaList.addAll(currentMangaList);
        } else {
            totalMangaList = currentMangaList;
        }


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
                    Intent intent = new Intent(getActivity(), WebMangaDetailsActivity.class);
                    intent.putExtra("mangaUrl", totalMangaList.get(position).getUrl());
                    startActivity(intent);
                }
            });
            mangaListLv.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    topBar.computeAndsetBackgroundAlpha(getScrollY(firstVisibleItem), gradientMagicNum);
                    if (firstVisibleItem == 0) {
                        View firstVisibleItemView = mangaListLv.getChildAt(0);
                        if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                            Log.d("ListView", "##### 滚动到顶部 #####");
                        }
                    } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                        View lastVisibleItemView = mangaListLv.getChildAt(mangaListLv.getChildCount() - 1);
                        if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == mangaListLv.getHeight()) {
                            Log.d("ListView", "##### 滚动到底部 ######");
                            loadMore();
                        }
                    }
                }
            });
        } else {
            onlineMangaListAdapter.setList(totalMangaList);
            onlineMangaListAdapter.notifyDataSetChanged();
        }
        int displayPage = (BaseParameterUtil.getInstance().getCurrentPage(getActivity()) - 1) / spider.nextPageNeedAddCount() + 1;
        currentPageTv.setText(displayPage + "");

        onLoadingMore = false;
    }

    private void showOptionsSelectorDialog() {
        ListDialog listDialog = new ListDialog(getActivity());
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        //切换站点
                        showWebsSelector();
                        MobclickAgent.onEvent(getActivity(), "select_website");
                        break;
                    case 1:
                        Intent intent = new Intent(getActivity(), SearchActivity.class);
                        intent.putExtra("selectedWebSite", BaseParameterUtil.getInstance().getCurrentWebSite(getActivity()));
                        startActivity(intent);
                        MobclickAgent.onEvent(getActivity(), "search");
                        break;
                    case 2:
                        MobclickAgent.onEvent(getActivity(), "select_type");
                        //分类
                        showTypesSelector();
                        break;
                    case 3:
                        //跳转
                        showToPageDialog("跳转");
                        MobclickAgent.onEvent(getActivity(), "jump_page");
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(optionsList);
    }

    private void showTypesSelector() {
        if (null == typesSelector) {
            typesSelector = new SingleSelectorDialog(getActivity());
            typesSelector.setCancelable(true);
        }
        typesSelector.setOnSingleSelectedListener(new SingleSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                if (spider.getMangaTypeCodes().length > 0) {
                    toggleTag(selectedRes, selectedCodeRes);
                } else {
                    toggleTag(selectedRes);
                }
            }

            @Override
            public void onOkBtnClick(int position) {
            }
        });
        typesSelector.show();
        typesSelector.setWheelViewTitle("切换标签");
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
        BaseParameterUtil.getInstance().saveCurrentType(getActivity(), selectedCode);
        refreshTopBarTitle();
        doGetData();
    }


    private void refreshTopBarTitle() {
        String type = BaseParameterUtil.getInstance().getCurrentType(getActivity());
        for (int i = 0; i < spider.getMangaTypeCodes().length; i++) {
            if (type.equals(spider.getMangaTypeCodes()[i])) {
                type = spider.getMangaTypes()[i];
                break;
            }
        }
        int actualPage = (BaseParameterUtil.getInstance().getCurrentPage(getActivity()) / spider.nextPageNeedAddCount());
        topBar.setTitle(BaseParameterUtil.getInstance().getCurrentWebSite(getActivity())
                + "(" + type + "-" + actualPage + ")");
    }

    private void showWebsSelector() {
        if (null == webSelector) {
            webSelector = new SingleSelectorDialog(getActivity());
            webSelector.setCancelable(true);
        }
        webSelector.setOnSingleSelectedListener(new SingleSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                initSpider(selectedRes);
                initToFirstPage();
                BaseParameterUtil.getInstance().saveCurrentType(getActivity(), spider.getMangaTypes()[0]);
                BaseParameterUtil.getInstance().saveCurrentWebSite(getActivity(), selectedRes);
                refreshTopBarTitle();
                if (selectedRes.equals("KaKaLot")) {
//                    baseToast.showToast("该网站中大部分漫画需要开启VPN后浏览");
                }
                doGetData();
            }

            @Override
            public void onOkBtnClick(int position) {
            }
        });
        webSelector.show();
        webSelector.setWheelViewTitle("选择站点");
        if (PermissionUtil.isMaster(getActivity())) {
            webSelector.initOptionsData(Configure.masterWebsList);
        } else {
            webSelector.initOptionsData(Configure.websList);
        }
    }

    private void showToPageDialog(String title) {
        if (null == toPageDialog) {
            toPageDialog = new MangaEditDialog(getActivity());
            toPageDialog.setOnEditResultListener(new OnEditResultListener() {
                @Override
                public void onResult(String text) {
                    try {
                        BaseParameterUtil.getInstance().saveCurrentPage(getActivity(), (Integer.valueOf(text) - 1) * spider.nextPageNeedAddCount());
                        startPage = BaseParameterUtil.getInstance().getCurrentPage(getActivity());
                        refreshTopBarTitle();
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

    private void initPullListView() {
        // 上拉加载更多
        pullListView.setPullLoadEnabled(false);
        // 滚到底部自动加载
        pullListView.setScrollLoadEnabled(false);
        pullListView.setOnRefreshListener(this);

        mangaListLv.setCacheColorHint(0xFFCCCCCC);// 点击后颜色
        // // mListView.setScrollBarStyle(ScrollView.);
//        mangaListLv.setDivider(getResources().getDrawable(R.color.colorAccent));// 线的颜色
        mangaListLv.setDividerHeight(0);// 线的高度

//        pullListView.doPullRefreshing(true, 500);
    }

    private void initToFirstPage() {
        BaseParameterUtil.getInstance().saveCurrentPage(getActivity(), 1);
        startPage = 1;
    }

    private void loadMore() {
        if (!onLoadingMore) {
            onLoadingMore = true;
            BaseParameterUtil.getInstance().saveCurrentPage
                    (getActivity(), BaseParameterUtil.getInstance().getCurrentPage(getActivity()) + spider.nextPageNeedAddCount());
            doGetData();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        initToFirstPage();
        doGetData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        loadMore();
    }
}
