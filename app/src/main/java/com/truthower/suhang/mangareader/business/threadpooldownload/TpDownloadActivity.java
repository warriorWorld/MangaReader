package com.truthower.suhang.mangareader.business.threadpooldownload;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.DownloadRecyclerAdapter;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailAdapter;
import com.truthower.suhang.mangareader.adapter.OnlineMangaRecyclerListAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.business.detail.LocalMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.download.DownloadMangaManager;
import com.truthower.suhang.mangareader.business.rxdownload.DownloadCaretaker;
import com.truthower.suhang.mangareader.business.rxdownload.DownloadContract;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemLongClickListener;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.ServiceUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;

import org.greenrobot.eventbus.Subscribe;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by Administrator on 2017/7/24.
 * <p>
 * 已弃用
 */

public class TpDownloadActivity extends BaseActivity implements View.OnClickListener, DownloadContract.View {
    private ImageView thumbnailIv;
    private TextView mangaNameTv;
    private TextView mangaChapterNameTv;
    private TextView chapterProgressTv;
    private ProgressBar downloadProgressBar;
    private TextView totalProgressTv;
    private ProgressBar totalProgressBar;
    private RecyclerView chapterRcv;
    private Button downloadBtn;
    private View emptyView;
    private DownloadContract.Presenter mPresenter;
    private RxDownloadBean mDownloadBean;
    private DownloadRecyclerAdapter adapter;

    @Override
    public void setPresenter(DownloadContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void displayInfo(RxDownloadBean downloadBean) {
        mDownloadBean = downloadBean;
        try {
            ImageLoader.getInstance().displayImage
                    (mDownloadBean.getThumbnailUrl(),
                            thumbnailIv, Configure.normalImageOptions);
            mangaNameTv.setText("漫画名称:  " + mDownloadBean.getMangaName());

            toggleDownloading(ServiceUtil.isServiceWork(this,
                    "com.truthower.suhang.mangareader.business.threadpooldownload.TpDownloadService"));
            initRec();
            toggleEmpty(false);
        } catch (Exception e) {
            toggleEmpty(true);
        }
    }

    @Override
    public void displayStart() {
        toggleDownloading(true);
    }

    @Override
    public void displayStop() {
        toggleDownloading(false);
    }

    @Override
    public void displayUpdate() {
        try {
            if (DownloadBean.getInstance().isOne_shot()) {
                mangaChapterNameTv.setText("章        节:  ONE SHOT");
                downloadProgressBar.setMax(DownloadMangaManager.getInstance().getOneShotListTotalSize());
                downloadProgressBar.setProgress(DownloadMangaManager.getInstance().getOneShotListTotalSize()
                        - DownloadMangaManager.getInstance().getOneShotLeftSize());
            } else {
                if (null != DownloadMangaManager.getInstance().
                        getCurrentChapter() && null != DownloadMangaManager.getInstance().
                        getCurrentChapter().getPages()) {
                    mangaChapterNameTv.setText("章        节:  第" +
                            DownloadMangaManager.getInstance().
                                    getCurrentChapter().getChapter_title() + "话");
                    downloadProgressBar.setMax(DownloadMangaManager.getInstance().
                            getCurrentChapter().getChapter_size());
                    downloadProgressBar.setProgress(DownloadMangaManager.getInstance().
                            getCurrentChapter().getChapter_size() - DownloadMangaManager.
                            getInstance().getCurrentChapter().getPages().size());
                }
            }
            toggleDownloading(true);
            toggleEmpty(false);
        } catch (Exception e) {
            toggleEmpty(true);
        }
    }


    private enum DownloadState {
        ON_GOING,
        STOPED
    }

    private DownloadState downloadState = DownloadState.STOPED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TUTORIAL, true)) {
            MangaDialog dialog = new MangaDialog(this);
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("1,刚开始下载时和点击停止下载时下载中的状态切换会有一定延时,有点耐心." +
                    "\n2,这个下载是以一张一张图片为单位的,所以不用等整个漫画下载完成就可以看,回本地" +
                    "漫画下拉刷新下就可以看到已经下载下来的漫画了");
        }
        setPresenter(new TpDownloadPresenter(this, this));
        mPresenter.getDownloadInfo();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tpdownload;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.updateDownload();
    }

    private void initUI() {
        thumbnailIv = (ImageView) findViewById(R.id.thumbnail_iv);
        mangaNameTv = (TextView) findViewById(R.id.manga_name_tv);
        mangaChapterNameTv = (TextView) findViewById(R.id.manga_chapter_name_tv);
        chapterProgressTv = (TextView) findViewById(R.id.chapter_progress_tv);
        downloadProgressBar = (ProgressBar) findViewById(R.id.download_progress_bar);
        totalProgressTv = (TextView) findViewById(R.id.total_progress_tv);
        totalProgressBar = (ProgressBar) findViewById(R.id.total_progress_bar);
        chapterRcv = (RecyclerView) findViewById(R.id.chapter_rcv);
        if (Configure.isPad) {
            chapterRcv.setLayoutManager(new StaggeredGridLayoutManager(8, StaggeredGridLayoutManager.VERTICAL));
        } else {
            chapterRcv.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
        }
        chapterRcv.setFocusableInTouchMode(false);
        chapterRcv.setFocusable(false);
        chapterRcv.setHasFixedSize(true);
        downloadBtn = (Button) findViewById(R.id.download_btn);
        emptyView = (View) findViewById(R.id.empty_view);

        downloadBtn.setOnClickListener(this);
        baseTopBar.setRightText("清空全部");
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                TpDownloadActivity.this.finish();
            }

            @Override
            public void onRightClick() {
                Intent stopService = new Intent(TpDownloadActivity.this, TpDownloadService.class);
                stopService(stopService);
                DownloadCaretaker.clean(TpDownloadActivity.this);
                displayInfo(null);
            }

            @Override
            public void onTitleClick() {

            }
        });

        baseTopBar.setTitle("下载");
    }

    private void initRec() {
        try {
            toggleEmpty(null == mDownloadBean || null == mDownloadBean.getChapters() ||
                    mDownloadBean.getChapters().size() <= 0);
            if (null == adapter) {
                adapter = new DownloadRecyclerAdapter(this);
                adapter.setList(mDownloadBean.getChapters());
                chapterRcv.setAdapter(adapter);
                ColorDrawable dividerDrawable = new ColorDrawable(0x00000000) {
                    @Override
                    public int getIntrinsicHeight() {
                        return DisplayUtil.dip2px(TpDownloadActivity.this, 8);
                    }

                    @Override
                    public int getIntrinsicWidth() {
                        return DisplayUtil.dip2px(TpDownloadActivity.this, 8);
                    }
                };
                RecyclerGridDecoration itemDecoration = new RecyclerGridDecoration(TpDownloadActivity.this,
                        dividerDrawable, true);
                chapterRcv.addItemDecoration(itemDecoration);
            } else {
                adapter.setList(mDownloadBean.getChapters());
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
            toggleEmpty(true);
        }
    }

    private void toggleEmpty(boolean empty) {
        if (empty) {
            emptyView.setVisibility(View.VISIBLE);
            downloadBtn.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            downloadBtn.setVisibility(View.VISIBLE);
        }
    }

    private void toggleDownloading(boolean ing) {
        if (ing) {
            downloadBtn.setText("停止下载");
            downloadState = DownloadState.ON_GOING;
        } else {
            downloadBtn.setText("开始下载");
            downloadState = DownloadState.STOPED;
        }
    }

    /**
     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(final DownLoadEvent event) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (null == event)
                        return;
                    switch (event.getEventType()) {
                        case EventBusEvent.DOWNLOAD_PAGE_FINISH_EVENT:
                            displayUpdate();
                            break;
                        case EventBusEvent.DOWNLOAD_FINISH_EVENT:
                            toggleEmpty(true);
                            DownloadCaretaker.clean(TpDownloadActivity.this);
                            MangaDialog dialog = new MangaDialog(TpDownloadActivity.this);
                            dialog.show();
                            dialog.setTitle("全部下载完成!");
                            break;
                        case EventBusEvent.DOWNLOAD_CHAPTER_FINISH_EVENT:
                            initRec();
                            displayUpdate();
                            break;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showStopDownloadConfirmDialog() {
        MangaDialog dialog = new MangaDialog(this);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                mPresenter.stopDownload();
                toggleDownloading(ServiceUtil.isServiceWork(TpDownloadActivity.this,
                        "com.truthower.suhang.mangareader.business.download.DownloadIntentService"));
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("是否暂停下载?");
        dialog.setOkText("是");
        dialog.setCancelText("否");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_btn:
                switch (downloadState) {
                    case STOPED:
                        mPresenter.startDownload();
                        break;
                    case ON_GOING:
                        showStopDownloadConfirmDialog();
                        break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}
