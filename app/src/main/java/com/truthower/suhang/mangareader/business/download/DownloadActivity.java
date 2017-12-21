package com.truthower.suhang.mangareader.business.download;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OneShotDetailsAdapter;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.DownloadBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.utils.ServiceUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2017/7/24.
 * <p>
 * 已弃用
 */

public class DownloadActivity extends BaseActivity implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener<GridView> {
    private ImageView thumbnailIv;
    private TextView mangaNameTv;
    private TextView mangaChapterNameTv;
    private Button downloadBtn;
    private ProgressBar downloadProgressBar;
    private PullToRefreshGridView ptfGridView;
    private RelativeLayout emptyView;
    private GridView mangaGV;
    private OnlineMangaDetailAdapter adapter;
    private OneShotDetailsAdapter oneShotAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initPullGridView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
        if (DownloadBean.getInstance().isOne_shot()) {
            initOneShotGridView();
        } else {
            initGridView();
        }
    }

    private void initUI() {
        thumbnailIv = (ImageView) findViewById(R.id.thumbnail_iv);
        mangaNameTv = (TextView) findViewById(R.id.manga_name_tv);
        mangaChapterNameTv = (TextView) findViewById(R.id.manga_chapter_name_tv);
        downloadProgressBar = (ProgressBar) findViewById(R.id.download_progress_bar);
        ptfGridView = (PullToRefreshGridView) findViewById(R.id.ptf_grid_view);
        emptyView = (RelativeLayout) findViewById(R.id.empty_view);
        mangaGV = (GridView) ptfGridView.getRefreshableView();
        downloadBtn = (Button) findViewById(R.id.download_btn);
        downloadBtn.setOnClickListener(this);
        baseTopBar.setRightText("清空全部");

        baseTopBar.setTitle("下载");
    }

    private void refreshUI() {
        try {
            ImageLoader.getInstance().displayImage
                    (DownloadBean.getInstance().getCurrentManga().getWebThumbnailUrl(),
                            thumbnailIv, Configure.normalImageOptions);
            mangaNameTv.setText("漫画名称:  " + DownloadBean.getInstance().getCurrentManga().getName());
            mangaChapterNameTv.setText("章    节:  第" +
                    DownloadMangaManager.getInstance().
                            getCurrentChapter(this).getChapter_title() + "话");
            toggleDownloading(ServiceUtil.isServiceWork(this,
                    "com.truthower.suhang.mangareader.business.download.DownloadIntentService"));
            emptyView.setVisibility(View.GONE);
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void updateUI() {
        try {
            emptyView.setVisibility(View.GONE);
            mangaChapterNameTv.setText("章    节:  第" +
                    DownloadMangaManager.getInstance().
                            getCurrentChapter(this).getChapter_title() + "话");
            downloadProgressBar.setProgress(DownloadMangaManager.getInstance().
                    getCurrentChapter(this).getChapter_size() - DownloadMangaManager.
                    getInstance().getCurrentChapter(this).getPages().size());
            toggleDownloading(true);
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new OnlineMangaDetailAdapter(this,
                    DownloadBean.getInstance().getCurrentManga().getChapters());
            mangaGV.setAdapter(adapter);
            mangaGV.setColumnWidth(50);
            mangaGV.setNumColumns(5);
            mangaGV.setVerticalSpacing(10);
            mangaGV.setHorizontalSpacing(3);
        } else {
            adapter.setChapters(DownloadBean.getInstance().getCurrentManga().getChapters());
            adapter.notifyDataSetChanged();
        }
        ptfGridView.onPullDownRefreshComplete();// 动画结束方法
        ptfGridView.onPullUpRefreshComplete();
    }

    private void initOneShotGridView() {
        if (null == oneShotAdapter) {
            oneShotAdapter = new OneShotDetailsAdapter(this,
                    DownloadBean.getInstance().getCurrentManga().getChapters());
            mangaGV.setAdapter(oneShotAdapter);
            mangaGV.setNumColumns(2);
            mangaGV.setVerticalSpacing(12);
            mangaGV.setGravity(Gravity.CENTER);
            mangaGV.setHorizontalSpacing(15);
        } else {
            oneShotAdapter.setChapterList(DownloadBean.getInstance().getCurrentManga().getChapters());
            oneShotAdapter.notifyDataSetChanged();
        }
        ptfGridView.onPullDownRefreshComplete();// 动画结束方法
        ptfGridView.onPullUpRefreshComplete();
    }

    private void initPullGridView() {
        // 上拉加载更多
        ptfGridView.setPullLoadEnabled(true);
        // 滚到底部自动加载
        ptfGridView.setScrollLoadEnabled(false);
        ptfGridView.setOnRefreshListener(this);
    }

    private void toggleDownloading(boolean ing) {
        if (ing) {
            downloadBtn.setText("停止下载");
        } else {
            downloadBtn.setText("开始下载");
        }
    }

    /**
     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(final DownLoadEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null == event)
                    return;
                switch (event.getEventType()) {
                    case EventBusEvent.DOWNLOAD_PAGE_FINISH_EVENT:
                        updateUI();
                        break;
                    case EventBusEvent.DOWNLOAD_FINISH_EVENT:
                        emptyView.setVisibility(View.VISIBLE);
                        MangaDialog dialog = new MangaDialog(DownloadActivity.this);
                        dialog.show();
                        dialog.setTitle("全部下载完成!");
                        break;
                    case EventBusEvent.DOWNLOAD_CHAPTER_FINISH_EVENT:
                        break;
                    case EventBusEvent.DOWNLOAD_CHAPTER_START_EVENT:
                        downloadProgressBar.setMax(DownloadMangaManager.getInstance().
                                getCurrentChapter(DownloadActivity.this).getChapter_size());
                        if (DownloadBean.getInstance().isOne_shot()) {
                            initOneShotGridView();
                        } else {
                            initGridView();
                        }
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_btn:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
        ptfGridView.onPullDownRefreshComplete();// 动画结束方法
        ptfGridView.onPullUpRefreshComplete();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
        ptfGridView.onPullDownRefreshComplete();// 动画结束方法
        ptfGridView.onPullUpRefreshComplete();
    }
}
