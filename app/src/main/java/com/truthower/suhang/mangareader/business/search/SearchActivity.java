package com.truthower.suhang.mangareader.business.search;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;

public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private RelativeLayout websiteRl;
    private TextView selectedWebsiteTv;
    private RelativeLayout searchTypeRl;
    private TextView selectedSearchTypeTv;
    private EditText mangaSearchEt;
    private ImageView searchIv;
    private RecyclerView searchResultRcv;

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
        websiteRl = (RelativeLayout) findViewById(R.id.website_rl);
        selectedWebsiteTv = (TextView) findViewById(R.id.selected_website_tv);
        searchTypeRl = (RelativeLayout) findViewById(R.id.search_type_rl);
        selectedSearchTypeTv = (TextView) findViewById(R.id.selected_search_type_tv);
        mangaSearchEt = (EditText) findViewById(R.id.manga_search_et);
        searchIv = (ImageView) findViewById(R.id.search_iv);
        searchResultRcv = (RecyclerView) findViewById(R.id.search_result_rcv);

        baseTopBar.setTitle("搜索漫画");
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
