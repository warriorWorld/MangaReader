//package com.truthower.suhang.mangareader.business.rxdownload;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import com.nostra13.universalimageloader.core.ImageLoader;
//import com.truthower.suhang.mangareader.R;
//import com.truthower.suhang.mangareader.adapter.RxDownloadAdapter;
//import com.truthower.suhang.mangareader.base.BaseActivity;
//import com.truthower.suhang.mangareader.bean.RxDownloadBean;
//import com.truthower.suhang.mangareader.config.Configure;
//import com.truthower.suhang.mangareader.config.ShareKeys;
//import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
//import com.truthower.suhang.mangareader.utils.ServiceUtil;
//import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
//import com.truthower.suhang.mangareader.widget.bar.TopBar;
//import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
//import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;
//
//import org.greenrobot.eventbus.Subscribe;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
///**
// * Created by Administrator on 2017/7/24.
// * <p>
// * 已弃用
// */
//
//public class RxDownloadActivity extends BaseActivity implements View.OnClickListener, DownloadContract.View {
//    private ImageView thumbnailIv;
//    private Button downloadBtn;
//    private RelativeLayout emptyView;
//    private RecyclerView downloadRcv;
//    private DownloadContract.Presenter mPresenter;
//    private RxDownloadAdapter adapter;
//    private RxDownloadBean mDownloadBean;
//
//    @Override
//    public void setPresenter(DownloadContract.Presenter presenter) {
//        this.mPresenter = presenter;
//    }
//
//    @Override
//    public void displayInfo(RxDownloadBean downloadBean) {
//        mDownloadBean = downloadBean;
//        try {
//            ImageLoader.getInstance().displayImage
//                    (mDownloadBean.getThumbnailUrl(),
//                            thumbnailIv, Configure.normalImageOptions);
//            baseTopBar.setTitle("下载" + mDownloadBean.getMangaName());
//
//            toggleDownloading(ServiceUtil.isServiceWork(this,
//                    "com.truthower.suhang.mangareader.business.rxdownload.DownloadService"));
//            initRec();
//            toggleEmpty(false);
//        } catch (Exception e) {
//            toggleEmpty(true);
//        }
//    }
//
//    @Override
//    public void displayStart() {
//        toggleDownloading(true);
//    }
//
//    @Override
//    public void displayStop() {
//        toggleDownloading(false);
//    }
//
//    @Override
//    public void displayUpdate() {
//
//    }
//
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
//        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TUTORIAL, true)) {
//            MangaDialog dialog = new MangaDialog(this);
//            dialog.show();
//            dialog.setTitle("教程");
//            dialog.setMessage("1,刚开始下载时和点击停止下载时下载中的状态切换会有一定延时,有点耐心." +
//                    "\n2,这个下载是以一张一张图片为单位的,所以不用等整个漫画下载完成就可以看,回本地" +
//                    "漫画下拉刷新下就可以看到已经下载下来的漫画了");
//        }
//        setPresenter(new DownloadPresenter(this, this));
//        mPresenter.getDownloadInfo();
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_rxdownload;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mPresenter.updateDownload();
//    }
//
//    private void initUI() {
//        thumbnailIv = (ImageView) findViewById(R.id.thumbnail_iv);
//        downloadRcv = findViewById(R.id.download_rcv);
//        downloadRcv.setLayoutManager
//                (new LinearLayoutMangerWithoutBug
//                        (this, LinearLayoutManager.VERTICAL, false));
//        downloadRcv.setFocusable(false);
//        downloadRcv.setHasFixedSize(true);
//
//        emptyView = (RelativeLayout) findViewById(R.id.empty_view);
//        downloadBtn = (Button) findViewById(R.id.download_btn);
//        downloadBtn.setOnClickListener(this);
//        baseTopBar.setRightText("清空全部");
//        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
//            @Override
//            public void onLeftClick() {
//                RxDownloadActivity.this.finish();
//            }
//
//            @Override
//            public void onRightClick() {
//                Intent stopService = new Intent(RxDownloadActivity.this, DownloadService.class);
//                stopService(stopService);
//                DownloadCaretaker.clean(RxDownloadActivity.this);
//                displayInfo(null);
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
//    private void initRec() {
//        try {
//            if (null == mDownloadBean || null == mDownloadBean.getChapters() || mDownloadBean.getChapters().size() <= 0) {
//                emptyView.setVisibility(View.VISIBLE);
//            } else {
//                emptyView.setVisibility(View.GONE);
//            }
//            if (null == adapter) {
//                adapter = new RxDownloadAdapter(this);
//                adapter.setList(mDownloadBean.getChapters());
//                downloadRcv.setAdapter(adapter);
//            } else {
//                adapter.setList(mDownloadBean.getChapters());
//                adapter.notifyDataSetChanged();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
//    public void onEventMainThread(final RxDownloadEvent event) {
//        try {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (null == event)
//                        return;
//                    switch (event.getEventType()) {
//                        case EventBusEvent.DOWNLOAD_PAGE_FINISH_EVENT:
//                            try {
//                                if (null != adapter) {
//                                    adapter.setList(event.getDownloadBean().getChapters());
//                                    adapter.notifyItemChanged(event.getPosition(), "not null");
//                                }
//                                toggleDownloading(true);
//                                toggleEmpty(false);
//                            } catch (Exception e) {
//                                toggleEmpty(true);
//                            }
//                            break;
//                        case EventBusEvent.DOWNLOAD_FINISH_EVENT:
//                            toggleEmpty(true);
//                            displayInfo(null);
//                            DownloadCaretaker.clean(RxDownloadActivity.this);
//                            MangaDialog dialog = new MangaDialog(RxDownloadActivity.this);
//                            dialog.show();
//                            dialog.setTitle("全部下载完成!");
//                            break;
//                        case EventBusEvent.DOWNLOAD_CHAPTER_FINISH_EVENT:
//                            adapter.remove(event.getPosition());
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
//                mPresenter.stopDownload();
//                toggleDownloading(ServiceUtil.isServiceWork(RxDownloadActivity.this,
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
//                        mPresenter.startDownload();
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
//        mPresenter.onDestroy();
//    }
//}
