package com.truthower.suhang.mangareader.business.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.LocalMangaListAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.sort.FileComparator;
import com.truthower.suhang.mangareader.sort.FileComparatorAllNum;
import com.truthower.suhang.mangareader.sort.FileComparatorWithBracket;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class LocalMangaDetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        PullToRefreshBase.OnRefreshListener<GridView>, AdapterView.OnItemLongClickListener {
    private PullToRefreshGridView pullToRefreshGridView;
    private View emptyView;
    private ImageView emptyIV;
    private TextView emptyTV;
    private GridView mangaGV;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private LocalMangaListAdapter adapter;
    private TopBar topBar;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        filePath = intent.getStringExtra("filePath");
        if (TextUtils.isEmpty(filePath)) {
            this.finish();
        }

        initUI();
        initPullGridView();
        initGridView();

        initFile();
    }


    private void initFile() {
        mangaList.clear();
        mangaList = FileSpider.getInstance().getMangaList(filePath);
        sortFiles();
        initGridView();
    }

    private void sortFiles() {
        if (!isNextDirectory(mangaList.get(0).getUrl())) {
            //只有这页是阅读页的前一页才重新排序

            ArrayList<String> pathList = new ArrayList<>();
            for (int i = 0; i < mangaList.size(); i++) {
                pathList.add(mangaList.get(i).getLocalThumbnailUrl());
            }

            //获取第一张图片的路径
            String firstImgName = pathList.get(0);
            if (firstImgName.contains(".jpg") || firstImgName.contains(".png") || firstImgName.contains(".bmp")) {
                firstImgName = firstImgName.substring(0, firstImgName.length() - 1 - 3);
                Log.d("s", "裁剪后的字符串" + firstImgName);
            } else if (firstImgName.contains(".jpeg")) {
                firstImgName = firstImgName.substring(0, firstImgName.length() - 1 - 4);
                Log.d("s", "裁剪后的字符串" + firstImgName);
            }
            String[] arr = firstImgName.split("_");
            if (arr.length == 0) {
                arr = firstImgName.split("-");
            }

            if (pathList.get(0).contains("_") ||
                    pathList.get(0).contains("-")) {
                //正常的漫画
                if (arr.length != 3) {
                    return;
                }
                FileComparator comparator = new FileComparator();
                Collections.sort(pathList, comparator);
            } else if (pathList.get(0).contains("(")) {
                FileComparatorWithBracket comparator1 = new FileComparatorWithBracket();
                Collections.sort(pathList, comparator1);
            } else {
                String[] arri = firstImgName.split("/");
                //最终获得图片名字
                firstImgName = arri[arri.length - 1];
                try {
                    //用于判断是否位数字的异教徒写法
                    int isInt = Integer.valueOf(firstImgName);
                    //没抛出异常 所以是纯数字
                    FileComparatorAllNum comparator2 = new FileComparatorAllNum();
                    Collections.sort(pathList, comparator2);
                } catch (NumberFormatException e) {

                }
            }

            for (int i = 0; i < pathList.size(); i++) {
                mangaList.get(i).setLocalThumbnailUrl(pathList.get(i));
                mangaList.get(i).setName((i + 1) + "");
            }
        }
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new LocalMangaListAdapter(this, mangaList);
            mangaGV.setAdapter(adapter);
            mangaGV.setOnItemClickListener(this);
            mangaGV.setOnItemLongClickListener(this);
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
        baseTopBar.setTitle("详情");
        mangaGV = (GridView) pullToRefreshGridView.getRefreshableView();
        emptyView = findViewById(R.id.empty_view);
        emptyIV = (ImageView) findViewById(R.id.image);
        emptyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initFile();
            }
        });
        emptyTV = (TextView) findViewById(R.id.text);
        emptyTV.setText("没有内容~");
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
//        baseToast.showToast(mangaList.get(position).getUrl());
        Intent intent = null;
        if (isNextDirectory(mangaList.get(position).getUrl())) {
            intent = new Intent(LocalMangaDetailsActivity.this, LocalMangaDetailsActivity.class);
            intent.putExtra("filePath", mangaList.get(position).getUrl());
        } else {
            baseToast.showToast("接下来就要进入看漫画页了" + mangaList.get(position).getLocalThumbnailUrl());
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    private boolean isNextDirectory(String path) {
        File f = new File(path);
        if (!f.exists()) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showDeleteDialog(i);
        return true;
    }

    private void showDeleteDialog(final int i) {
        MangaDialog deleteDialog = new MangaDialog(this);
        deleteDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                if (isNextDirectory(mangaList.get(i).getUrl())) {
                    //是文件夹就删这个路径的
                    FileSpider.getInstance().deleteFile(mangaList.get(i).getUrl());
                } else {
                    FileSpider.getInstance().deleteFile(mangaList.get(i).getLocalThumbnailUrl());
                }
                initFile();
            }

            @Override
            public void onCancelClick() {

            }
        });
        deleteDialog.show();

        deleteDialog.setTitle("确定删除?");
        deleteDialog.setOkText("删除");
        deleteDialog.setCancelText("算了");
        deleteDialog.setCancelable(true);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
        initFile();
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
