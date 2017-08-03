package com.truthower.suhang.mangareader.business.tag;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.LocalMangaListAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.ThreeDESUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.WheelSelectorDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */

public class TagFilterActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        PullToRefreshBase.OnRefreshListener<GridView> {
    private PullToRefreshGridView pullToRefreshGridView;
    private View emptyView;
    private ImageView emptyIV;
    private TextView emptyTV;
    private GridView mangaGV;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private LocalMangaListAdapter adapter;
    private TopBar topBar;
    private ArrayList<String> pathList;
    private WheelSelectorDialog tagsSelector;
    private String[] tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initPullGridView();
        initGridView();
        doGetTags();
    }

    /**
     * 获取所有已有标签
     */
    private void doGetTags() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            this.finish();
            return;
        }
        AVQuery<AVObject> query = new AVQuery<>("TagList");
        query.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(TagFilterActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        tags = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            tags[i] = ThreeDESUtil.decode(Configure.key, list.get(i).getString("tag"));
                        }
                    }
                }
            }
        });
    }

    private void doGetImagesByTag(final String tag) {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            this.finish();
            return;
        }
        baseTopBar.setTitle("标签筛选(" + tag + ")");
        AVQuery<AVObject> ownerQuery = new AVQuery<>("Tags");
        ownerQuery.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        AVQuery<AVObject> tagQuery = new AVQuery<>("Tags");
        tagQuery.whereEqualTo("tags", ThreeDESUtil.encode(Configure.key, tag));

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(ownerQuery, tagQuery));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(TagFilterActivity.this, e)) {
                    mangaList = new ArrayList<MangaBean>();
                    pathList = new ArrayList<String>();
                    if (null != list && list.size() > 0) {
                        MangaBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new MangaBean();
                            item.setName((i + 1) + "");
                            item.setLocalThumbnailUrl(list.get(i).getString("imgUrl"));
                            pathList.add(list.get(i).getString("imgUrl"));
                            mangaList.add(item);
                        }
                    }
                    initGridView();
                }
            }
        });
    }


    private void initGridView() {
        if (null == adapter) {
            adapter = new LocalMangaListAdapter(this, mangaList);
            mangaGV.setAdapter(adapter);
            mangaGV.setOnItemClickListener(this);
            mangaGV.setEmptyView(emptyView);
//            mangaGV.setColumnWidth(100);
            mangaGV.setNumColumns(2);
            mangaGV.setVerticalSpacing(12);
            mangaGV.setGravity(Gravity.CENTER);
            mangaGV.setHorizontalSpacing(15);
        } else {
            adapter.setMangaList(mangaList);
            adapter.notifyDataSetChanged();
        }
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
    }


    private void initUI() {
        pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.ptf_local_grid_view);
        topBar = (TopBar) findViewById(R.id.gradient_bar);
        topBar.setVisibility(View.GONE);

        mangaGV = (GridView) pullToRefreshGridView.getRefreshableView();
        emptyView = findViewById(R.id.empty_view);
        emptyIV = (ImageView) findViewById(R.id.empty_image);
        emptyTV = (TextView) findViewById(R.id.empty_text);
        emptyTV.setText("没有内容~");
        baseTopBar.setTitle("标签筛选");
        baseTopBar.setRightText("筛选");
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                TagFilterActivity.this.finish();
            }

            @Override
            public void onRightClick() {
                showTagsSelector();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    private void initPullGridView() {
        // 上拉加载更多
        pullToRefreshGridView.setPullLoadEnabled(true);
        // 滚到底部自动加载
        pullToRefreshGridView.setScrollLoadEnabled(false);
        pullToRefreshGridView.setOnRefreshListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        intent = new Intent(TagFilterActivity.this, ReadMangaActivity.class);
        Bundle pathListBundle = new Bundle();
        pathListBundle.putSerializable("pathList", pathList);
        intent.putExtras(pathListBundle);
        if (null != intent) {
            startActivity(intent);
        }
    }

    private void showTagsSelector() {
        if (null == tags || tags.length <= 0) {
            return;
        }
        if (null == tagsSelector) {
            tagsSelector = new WheelSelectorDialog(TagFilterActivity.this);
            tagsSelector.setCancelable(true);
        }
        tagsSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                doGetImagesByTag(selectedRes);
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
            }
        });
        tagsSelector.show();

        tagsSelector.initOptionsData(tags);
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_local;
    }
}
