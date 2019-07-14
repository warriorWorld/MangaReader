package com.truthower.suhang.mangareader.business.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.insightsurfface.stylelibrary.keyboard.English9KeyBoardView;
import com.insightsurfface.stylelibrary.listener.OnKeyboardChangeListener;
import com.insightsurfface.stylelibrary.listener.OnKeyboardListener;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.SearchMangaRecyclerAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.other.KeyboardSettingActivity;
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.ListDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;

import java.util.ArrayList;

public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout websiteRl;
    private TextView selectedWebsiteTv;
    private RelativeLayout searchTypeRl;
    private TextView selectedSearchTypeTv;
    private EditText mangaSearchEt;
    private ImageView searchIv;
    private RecyclerView searchResultRcv;
    private SearchMangaRecyclerAdapter adapter;
    private View emptyView;
    private ArrayList<MangaBean> searchResultList = new ArrayList<>();
    private SpiderBase spider;
    private String selectedWebSite = "MangaReader", keyWord;
    private String[] searchTypeOptions = {"按漫画名称搜索", "按漫画作者搜索"};
    private SpiderBase.SearchType selectedSearchType = SpiderBase.SearchType.BY_MANGA_NAME;
    private boolean immediateSearch = false;
    private English9KeyBoardView mKeyBoardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String searchType = intent.getStringExtra("searchType");
        if ("name".equals(searchType)) {
            selectedSearchType = SpiderBase.SearchType.BY_MANGA_NAME;
        } else if ("author".equals(searchType)) {
            selectedSearchType = SpiderBase.SearchType.BY_MANGA_AUTHOR;
        }
        String t = intent.getStringExtra("selectedWebSite");
        if (!TextUtils.isEmpty(t)) {
            selectedWebSite = t;
        }
        keyWord = intent.getStringExtra("keyWord");
        immediateSearch = intent.getBooleanExtra("immediateSearch", false);

        initUI();
        initSpider(selectedWebSite);
        refreshUI();
        if (immediateSearch) {
            doSearch();
        }
    }


    private void refreshUI() {
        selectedWebsiteTv.setText(selectedWebSite);
        switch (selectedSearchType) {
            case BY_MANGA_NAME:
                selectedSearchTypeTv.setText("按漫画名称搜索");
                break;
            case BY_MANGA_AUTHOR:
                selectedSearchTypeTv.setText("按漫画作者搜索");
                break;
        }
        mangaSearchEt.setText(keyWord);
    }


    private void initUI() {
        emptyView = findViewById(R.id.empty_view);
        websiteRl = (RelativeLayout) findViewById(R.id.website_rl);
        selectedWebsiteTv = (TextView) findViewById(R.id.selected_website_tv);
        searchTypeRl = (RelativeLayout) findViewById(R.id.search_type_rl);
        selectedSearchTypeTv = (TextView) findViewById(R.id.selected_search_type_tv);
        mKeyBoardView = (English9KeyBoardView) findViewById(R.id.keyboard_v);
        mKeyBoardView.setOnKeyboardChangeListener(new OnKeyboardChangeListener() {
            @Override
            public void onChange(String res) {

            }

            @Override
            public void onFinish(String res) {
                doSearch();
                mKeyBoardView.hide();
            }
        });
        mKeyBoardView.setOnKeyboardListener(new OnKeyboardListener() {
            @Override
            public void onOptionsClick() {
                Intent intent = new Intent(SearchActivity.this, KeyboardSettingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onQuestionClick() {
                baseToast.showToast("点按后滑动输入");
            }
        });
        mangaSearchEt = (EditText) findViewById(R.id.manga_search_et);
        mangaSearchEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //因为DOWN和UP都算回车 所以这样写 避免调用两次
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            doSearch();
                            break;
                    }
                }
                return false;
            }
        });
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData
                (this, ShareKeys.CLOSE_SH_KEYBOARD, false)) {
            mKeyBoardView.attachTo(mangaSearchEt);
            mangaSearchEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mKeyBoardView.show();
                }
            });
        }
        searchIv = (ImageView) findViewById(R.id.search_iv);
        searchResultRcv = (RecyclerView) findViewById(R.id.search_result_rcv);
        searchResultRcv.setLayoutManager
                (new LinearLayoutMangerWithoutBug
                        (this, LinearLayoutManager.VERTICAL, false));
        searchResultRcv.setFocusable(false);
        searchResultRcv.setHasFixedSize(true);

        websiteRl.setOnClickListener(this);
        searchTypeRl.setOnClickListener(this);
        searchIv.setOnClickListener(this);

        baseTopBar.setTitle("搜索漫画");
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
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

    private void doGetData() {
        spider.getSearchResultList(selectedSearchType, keyWord, new JsoupCallBack<MangaListBean>() {
            @Override
            public void loadSucceed(final MangaListBean result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchResultList = result.getMangaList();
                        if (null == searchResultList || searchResultList.size() == 0) {
                            showNothingToShowDialog();
                        }
                        initSearchResultRv();
                        closeKeyBroad();
                    }
                });
            }

            @Override
            public void loadFailed(String error) {

            }
        });
    }

    private void showNothingToShowDialog() {
        MangaDialog dialog = new MangaDialog(this);
        dialog.show();
        dialog.setTitle("没有搜索结果!");
        dialog.setOkText("知道了");
    }

    @Override
    public void onBackPressed() {
        if (mKeyBoardView.isShown()) {
            mKeyBoardView.hide();
        } else {
            super.onBackPressed();
        }
    }

    private void initSearchResultRv() {
        try {
            if (null == searchResultList || searchResultList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new SearchMangaRecyclerAdapter(this, searchResultList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(SearchActivity.this, WebMangaDetailsActivity.class);
                        intent.putExtra("mangaUrl", searchResultList.get(position).getUrl());
                        startActivity(intent);
                    }
                });
                searchResultRcv.setAdapter(adapter);
            } else {
                adapter.setList(searchResultList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }

    private void showSearchTypeSelectorDialog() {
        ListDialog listDialog = new ListDialog(this);
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
                        selectedSearchType = SpiderBase.SearchType.BY_MANGA_NAME;
                        break;
                    case 1:
                        selectedSearchType = SpiderBase.SearchType.BY_MANGA_AUTHOR;
                        break;
                }
                refreshUI();
                doSearch();
            }
        });
        listDialog.show();
        listDialog.setOptionsList(searchTypeOptions);
    }

    private void showWebsiteSelectorDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {
                selectedWebSite = selectedRes;
                initSpider(selectedWebSite);
                refreshUI();
                doSearch();
            }

            @Override
            public void onItemClick(int position) {
            }
        });
        listDialog.show();
        if (LoginBean.getInstance().isMaster()) {
            listDialog.setOptionsList(Configure.masterWebsList);
        } else {
            listDialog.setOptionsList(Configure.websList);
        }
    }

    private void closeKeyBroad() {
        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mangaSearchEt, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(mangaSearchEt.getWindowToken(), 0);
    }

    private void doSearch() {
        if (TextUtils.isEmpty(mangaSearchEt.getText().toString())) {
            baseToast.showToast("请输入搜索内容");
            return;
        }
        keyWord = mangaSearchEt.getText().toString();
        doGetData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_iv:
                doSearch();
                break;
            case R.id.search_type_rl:
                showSearchTypeSelectorDialog();
                break;
            case R.id.website_rl:
                showWebsiteSelectorDialog();
                break;
        }
        closeKeyBroad();
    }
}
