package com.truthower.suhang.mangareader.business.download;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.DownloadBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.utils.ServiceUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2017/7/24.
 * <p>
 * 已弃用
 */

public class DownloadActivity extends BaseActivity implements View.OnClickListener {
    private ImageView thumbnailIv;
    private TextView mangaNameTv;
    private TextView mangaChapterNameTv;
    private ProgressBar downloadProgressBar;
    private PullToRefreshGridView ptfGridView;
    private RelativeLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_download;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI();
    }

    private void initUI() {
        thumbnailIv = (ImageView) findViewById(R.id.thumbnail_iv);
        mangaNameTv = (TextView) findViewById(R.id.manga_name_tv);
        mangaChapterNameTv = (TextView) findViewById(R.id.manga_chapter_name_tv);
        downloadProgressBar = (ProgressBar) findViewById(R.id.download_progress_bar);
        ptfGridView = (PullToRefreshGridView) findViewById(R.id.ptf_grid_view);
        emptyView = (RelativeLayout) findViewById(R.id.empty_view);

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
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void updateUI() {
        try {
            mangaChapterNameTv.setText("章    节:  第" +
                    DownloadMangaManager.getInstance().
                            getCurrentChapter(this).getChapter_title() + "话");
            toggleDownloading(true);
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void toggleDownloading(boolean ing) {
        if (ing) {
            baseTopBar.setRightText("停止下载");
        } else {
            baseTopBar.setRightText("开始下载");
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
                    case EventBusEvent.DOWNLOAD_EVENT:
                        break;
                    case EventBusEvent.DOWNLOAD_FINISH_EVENT:
                        break;
                    case EventBusEvent.DOWNLOAD_FAIL_EVENT:
                        break;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
