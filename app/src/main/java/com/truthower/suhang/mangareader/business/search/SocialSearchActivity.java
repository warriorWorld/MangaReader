//package com.truthower.suhang.mangareader.business.search;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.avos.avoscloud.AVException;
//import com.avos.avoscloud.AVObject;
//import com.avos.avoscloud.AVQuery;
//import com.avos.avoscloud.FindCallback;
//import com.truthower.suhang.mangareader.R;
//import com.truthower.suhang.mangareader.adapter.SearchMangaRecyclerAdapter;
//import com.truthower.suhang.mangareader.base.BaseActivity;
//import com.truthower.suhang.mangareader.bean.CommentBean;
//import com.truthower.suhang.mangareader.bean.LoginBean;
//import com.truthower.suhang.mangareader.bean.MangaBean;
//import com.truthower.suhang.mangareader.bean.MangaListBean;
//import com.truthower.suhang.mangareader.business.comment.CommentActivity;
//import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
//import com.truthower.suhang.mangareader.config.Configure;
//import com.truthower.suhang.mangareader.listener.JsoupCallBack;
//import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
//import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;
//import com.truthower.suhang.mangareader.spider.SpiderBase;
//import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
//import com.truthower.suhang.mangareader.widget.dialog.ListDialog;
//import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;
//import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;
//
//import java.util.ArrayList;
//import java.util.List;
//无法实现 每个漫画可以有很多评论 如果查询所有评论的话 太多了
//public class SocialSearchActivity extends BaseActivity implements View.OnClickListener {
//    private RecyclerView searchResultRcv;
//    private SearchMangaRecyclerAdapter adapter;
//    private ArrayList<MangaBean> searchResultList = new ArrayList<>();
//    private SpiderBase.SearchType selectedSearchType;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Intent intent = getIntent();
//        String searchType = intent.getStringExtra("searchType");
//        if ("comment".equals(searchType)) {
//            selectedSearchType = SpiderBase.SearchType.BY_COMMENT;
//        } else if ("grade".equals(searchType)) {
//            selectedSearchType = SpiderBase.SearchType.BY_GRADE;
//        }
//        initUI();
//        refreshUI();
//    }
//
//
//    private void refreshUI() {
//        switch (selectedSearchType) {
//            case BY_COMMENT:
//                baseTopBar.setTitle("有评论的所有漫画");
//                break;
//            case BY_GRADE:
//                baseTopBar.setTitle("有评分的所有漫画");
//                break;
//        }
//    }
//
//
//    private void initUI() {
//        searchResultRcv = (RecyclerView) findViewById(R.id.only_rcv);
//        searchResultRcv.setLayoutManager
//                (new LinearLayoutMangerWithoutBug
//                        (this, LinearLayoutManager.VERTICAL, false));
//        searchResultRcv.setFocusable(false);
//        searchResultRcv.setHasFixedSize(true);
//    }
//
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_only_recycler;
//    }
//
//    private void doGetData() {
//    }
//
//    private void initSearchResultRv() {
//        try {
//            if (null == adapter) {
//                adapter = new SearchMangaRecyclerAdapter(this, searchResultList);
//                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
//                    @Override
//                    public void onItemClick(int position) {
//                        Intent intent = new Intent(SocialSearchActivity.this, WebMangaDetailsActivity.class);
//                        intent.putExtra("mangaUrl", searchResultList.get(position).getUrl());
//                        startActivity(intent);
//                    }
//                });
//                searchResultRcv.setAdapter(adapter);
//            } else {
//                adapter.setList(searchResultList);
//                adapter.notifyDataSetChanged();
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//        }
//    }
//}
