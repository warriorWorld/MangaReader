//package com.truthower.suhang.mangareader.business.download;
//
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.truthower.suhang.mangareader.R;
//import com.truthower.suhang.mangareader.adapter.OneShotDetailsAdapter;
//import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailAdapter;
//import com.truthower.suhang.mangareader.base.BaseActivity;
//import com.truthower.suhang.mangareader.bean.DownloadBean;
//import com.truthower.suhang.mangareader.config.Configure;
//import com.truthower.suhang.mangareader.config.ShareKeys;
//import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
//import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
//import com.truthower.suhang.mangareader.utils.ServiceUtil;
//import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
//import com.truthower.suhang.mangareader.widget.bar.TopBar;
//import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
//import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
//import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;
//
//import org.greenrobot.eventbus.Subscribe;
//
///**
// * Created by Administrator on 2017/7/24.
// * <p>
// * 已弃用
// */
//
//public class DownloadActivity extends BaseActivity implements View.OnClickListener,
//        PullToRefreshBase.OnRefreshListener<GridView> {
//    private ImageView thumbnailIv;
//    private TextView mangaNameTv;
//    private TextView mangaChapterNameTv;
//    private Button downloadBtn;
//    private ProgressBar downloadProgressBar;
//    private PullToRefreshGridView ptfGridView;
//    private RelativeLayout emptyView;
//    private GridView mangaGV;
//    private OnlineMangaDetailAdapter adapter;
//    private OneShotDetailsAdapter oneShotAdapter;
//
//    private enum DownloadState {
//        ON_GOING,
//        STOPED
//    }
//
//    private DownloadState downloadState = DownloadState.STOPED;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initUI();
//        initPullGridView();
//        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TUTORIAL, true)) {
//            MangaDialog dialog = new MangaDialog(this);
//            dialog.show();
//            dialog.setTitle("教程");
//            dialog.setMessage("1,刚开始下载时和点击停止下载时下载中的状态切换会有一定延时,有点耐心." +
//                    "\n2,这个下载是以一张一张图片为单位的,所以不用等整个漫画下载完成就可以看,回本地" +
//                    "漫画下拉刷新下就可以看到已经下载下来的漫画了");
//        }
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_download;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        refreshUI();
//        if (DownloadBean.getInstance().isOne_shot()) {
//            initOneShotGridView();
//        } else {
//            initGridView();
//        }
//    }
//
//    private void initUI() {
//        thumbnailIv = (ImageView) findViewById(R.id.thumbnail_iv);
//        mangaNameTv = (TextView) findViewById(R.id.manga_name_tv);
//        mangaChapterNameTv = (TextView) findViewById(R.id.manga_chapter_name_tv);
//        downloadProgressBar = (ProgressBar) findViewById(R.id.download_progress_bar);
//        ptfGridView = (PullToRefreshGridView) findViewById(R.id.ptf_grid_view);
//        emptyView = (RelativeLayout) findViewById(R.id.empty_view);
//        mangaGV = (GridView) ptfGridView.getRefreshableView();
//        downloadBtn = (Button) findViewById(R.id.download_btn);
//        downloadBtn.setOnClickListener(this);
//        baseTopBar.setRightText("清空全部");
//        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
//            @Override
//            public void onLeftClick() {
//                DownloadActivity.this.finish();
//            }
//
//            @Override
//            public void onRightClick() {
//                DownloadMangaManager.getInstance().reset(DownloadActivity.this);
//                refreshUI();
//            }
//
//            @Override
//            public void onTitleClick() {
//
//            }
//        });
//
//        baseTopBar.setTitle("下载");
//    }
//
//    private void refreshUI() {
//        try {
//            ImageLoader.getInstance().displayImage
//                    (DownloadBean.getInstance().getCurrentManga().getWebThumbnailUrl(),
//                            thumbnailIv, Configure.normalImageOptions);
//            mangaNameTv.setText("漫画名称:  " + DownloadBean.getInstance().getCurrentManga().getName());
//            if (null != DownloadMangaManager.getInstance().
//                    getCurrentChapter()) {
//                mangaChapterNameTv.setText("章        节:  第" +
//                        DownloadMangaManager.getInstance().
//                                getCurrentChapter().getChapter_title() + "话");
//            }
//            toggleDownloading(ServiceUtil.isServiceWork(this,
//                    "com.truthower.suhang.mangareader.business.download.DownloadIntentService"));
//            if (null != DownloadMangaManager.getInstance().
//                    getCurrentChapter()) {
//                downloadProgressBar.setMax(DownloadMangaManager.getInstance().getCurrentChapter().getChapter_size());
//            }
//            if (null != DownloadMangaManager.getInstance().
//                    getCurrentChapter() && null != DownloadMangaManager.getInstance().
//                    getCurrentChapter().getPages()) {
//                downloadProgressBar.setMax(DownloadMangaManager.getInstance().
//                        getCurrentChapter().getChapter_size());
//                downloadProgressBar.setProgress(DownloadMangaManager.getInstance().
//                        getCurrentChapter().getChapter_size() - DownloadMangaManager.
//                        getInstance().getCurrentChapter().getPages().size());
//            }
//            toggleEmpty(false);
//        } catch (Exception e) {
//            toggleEmpty(true);
//        }
//    }
//
//    //这个方法必须是下载的时候调用
//    private void updateUI() {
//        try {
//            if (DownloadBean.getInstance().isOne_shot()) {
//                mangaChapterNameTv.setText("章        节:  ONE SHOT");
//                downloadProgressBar.setMax(DownloadMangaManager.getInstance().getOneShotListTotalSize());
//                downloadProgressBar.setProgress(DownloadMangaManager.getInstance().getOneShotListTotalSize()
//                        - DownloadMangaManager.getInstance().getOneShotLeftSize());
//            } else {
//                if (null != DownloadMangaManager.getInstance().
//                        getCurrentChapter() && null != DownloadMangaManager.getInstance().
//                        getCurrentChapter().getPages()) {
//                    mangaChapterNameTv.setText("章        节:  第" +
//                            DownloadMangaManager.getInstance().
//                                    getCurrentChapter().getChapter_title() + "话");
//                    downloadProgressBar.setMax(DownloadMangaManager.getInstance().
//                            getCurrentChapter().getChapter_size());
//                    downloadProgressBar.setProgress(DownloadMangaManager.getInstance().
//                            getCurrentChapter().getChapter_size() - DownloadMangaManager.
//                            getInstance().getCurrentChapter().getPages().size());
//                }
//            }
//            toggleDownloading(true);
//            toggleEmpty(false);
//        } catch (Exception e) {
//            toggleEmpty(true);
//        }
//    }
//
//    private void initGridView() {
//        try {
//            if (null == adapter) {
//                adapter = new OnlineMangaDetailAdapter(this,
//                        DownloadBean.getInstance().getCurrentManga().getChapters());
//                mangaGV.setAdapter(adapter);
//                mangaGV.setColumnWidth(50);
//                mangaGV.setNumColumns(5);
//                mangaGV.setVerticalSpacing(10);
//                mangaGV.setHorizontalSpacing(3);
//            } else {
//                adapter.setChapters(DownloadBean.getInstance().getCurrentManga().getChapters());
//                adapter.notifyDataSetChanged();
//            }
//            ptfGridView.onPullDownRefreshComplete();// 动画结束方法
//            ptfGridView.onPullUpRefreshComplete();
//        } catch (Exception e) {
//            toggleEmpty(true);
//        }
//    }
//
//    private void initOneShotGridView() {
//        try {
//            if (null == oneShotAdapter) {
//                oneShotAdapter = new OneShotDetailsAdapter(this,
//                        DownloadBean.getInstance().getCurrentManga().getChapters());
//                mangaGV.setAdapter(oneShotAdapter);
//                mangaGV.setNumColumns(2);
//                mangaGV.setVerticalSpacing(12);
//                mangaGV.setGravity(Gravity.CENTER);
//                mangaGV.setHorizontalSpacing(15);
//            } else {
//                oneShotAdapter.setChapterList(DownloadBean.getInstance().getCurrentManga().getChapters());
//                oneShotAdapter.notifyDataSetChanged();
//            }
//            ptfGridView.onPullDownRefreshComplete();// 动画结束方法
//            ptfGridView.onPullUpRefreshComplete();
//        } catch (Exception e) {
//            toggleEmpty(true);
//        }
//    }
//
//    private void initPullGridView() {
//        // 上拉加载更多
//        ptfGridView.setPullLoadEnabled(true);
//        // 滚到底部自动加载
//        ptfGridView.setScrollLoadEnabled(false);
//        ptfGridView.setOnRefreshListener(this);
//    }
//
//    private void toggleEmpty(boolean empty) {
//        if (empty) {
//            emptyView.setVisibility(View.VISIBLE);
//            downloadBtn.setVisibility(View.GONE);
//        } else {
//            emptyView.setVisibility(View.GONE);
//            downloadBtn.setVisibility(View.VISIBLE);
//        }
//    }
//
//    private void toggleDownloading(boolean ing) {
//        if (ing) {
//            downloadBtn.setText("停止下载");
//            downloadState = DownloadState.ON_GOING;
//        } else {
//            downloadBtn.setText("开始下载");
//            downloadState = DownloadState.STOPED;
//        }
//    }
//
//    /**
//     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
//     *
//     * @param event
//     */
//    @Subscribe
//    public void onEventMainThread(final DownLoadEvent event) {
//        try {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (null == event)
//                        return;
//                    switch (event.getEventType()) {
//                        case EventBusEvent.DOWNLOAD_PAGE_FINISH_EVENT:
//                            updateUI();
//                            break;
//                        case EventBusEvent.DOWNLOAD_FINISH_EVENT:
//                            toggleEmpty(true);
//                            DownloadMangaManager.getInstance().reset(DownloadActivity.this);
//                            MangaDialog dialog = new MangaDialog(DownloadActivity.this);
//                            dialog.show();
//                            dialog.setTitle("全部下载完成!");
//                            break;
//                        case EventBusEvent.DOWNLOAD_CHAPTER_FINISH_EVENT:
//                            break;
//                        case EventBusEvent.DOWNLOAD_CHAPTER_START_EVENT:
//                            if (DownloadBean.getInstance().isOne_shot()) {
//                                //不刷新列表
////                                initOneShotGridView();
//                            } else {
//                                initGridView();
//                            }
//                            updateUI();
//                            break;
//                    }
//                }
//            });
//        } catch (Exception e) {
//
//        }
//    }
//
//    private void showStopDownloadConfirmDialog() {
//        MangaDialog dialog = new MangaDialog(this);
//        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
//            @Override
//            public void onOkClick() {
//                DownloadMangaManager.getInstance().stopDownload(getApplicationContext());
//                toggleDownloading(ServiceUtil.isServiceWork(DownloadActivity.this,
//                        "com.truthower.suhang.mangareader.business.download.DownloadIntentService"));
//            }
//
//            @Override
//            public void onCancelClick() {
//
//            }
//        });
//        dialog.show();
//        dialog.setTitle("是否暂停下载?");
//        dialog.setOkText("是");
//        dialog.setCancelText("否");
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.download_btn:
//                switch (downloadState) {
//                    case STOPED:
//                        DownloadMangaManager.getInstance().doDownload(getApplicationContext());
//                        break;
//                    case ON_GOING:
//                        showStopDownloadConfirmDialog();
//                        break;
//                }
//                break;
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//        ptfGridView.onPullDownRefreshComplete();// 动画结束方法
//        ptfGridView.onPullUpRefreshComplete();
//    }
//
//    @Override
//    public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//        ptfGridView.onPullDownRefreshComplete();// 动画结束方法
//        ptfGridView.onPullUpRefreshComplete();
//    }
//}
