package com.truthower.suhang.mangareader.business.search;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.SearchMangaRecyclerAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.user.CollectedActivity;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initUI() {
        emptyView = findViewById(R.id.empty_view);
        websiteRl = (RelativeLayout) findViewById(R.id.website_rl);
        selectedWebsiteTv = (TextView) findViewById(R.id.selected_website_tv);
        searchTypeRl = (RelativeLayout) findViewById(R.id.search_type_rl);
        selectedSearchTypeTv = (TextView) findViewById(R.id.selected_search_type_tv);
        mangaSearchEt = (EditText) findViewById(R.id.manga_search_et);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
