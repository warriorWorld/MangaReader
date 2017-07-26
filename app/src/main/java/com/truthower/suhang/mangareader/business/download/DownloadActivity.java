package com.truthower.suhang.mangareader.business.download;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2017/7/24.
 */

public class DownloadActivity extends BaseActivity implements View.OnClickListener {
    private String mangaName, explain;
    private int folderSize = 5;// 子文件夹话数
    private int nowEpisode = 1, nowPage = 1, endEpisode;
    private String[] folderSizeList = {"1", "2", "3", "5", "10"};
    private TextView explainTv, tryAmountTv, mangaNameTv;
    private EditText episodeET, pageET, mangaNameET, endEpisodeET;
    private Button folderSizeBtn;
    private Button startBtn, stopBtn;

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
        recoverStatus();
        refreshUI();
    }

    private void initUI() {
        explainTv = (TextView) findViewById(R.id.download_explain);
        tryAmountTv = (TextView) findViewById(R.id.try_amount);
        episodeET = (EditText) findViewById(R.id.episode);
        endEpisodeET = (EditText) findViewById(R.id.end_episode);
        mangaNameET = (EditText) findViewById(R.id.manga_name);
        folderSizeBtn = (Button) findViewById(R.id.folder_size);
        pageET = (EditText) findViewById(R.id.page);
        startBtn = (Button) findViewById(R.id.start);
        stopBtn = (Button) findViewById(R.id.stop);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        folderSizeBtn.setOnClickListener(this);
    }

    /**
     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(DownLoadEvent event) {
        if (null == event)
            return;

        baseToast.showToast("download event \n" + event.getMsg() + "\n" + event.getDownloadExplain());
        switch (event.getEventType()) {
            case EventBusEvent.DOWNLOAD_EVENT:
                saveStatus(event);
                explainTv.setText(event.getDownloadExplain());
                break;
            case EventBusEvent.DOWNLOAD_FINISH_EVENT:
                Intent stopIntent = new Intent(this, DownloadService.class);
                stopService(stopIntent);
                break;
            case EventBusEvent.DOWNLOAD_FAIL_EVENT:
                saveStatus(event);
                tryAmountTv.setText(event.getDownloadExplain());
                break;
        }
    }

    private void refreshUI() {
        explainTv.setText(explain);
        mangaNameET.setText(mangaName);
        episodeET.setText(String.valueOf(nowEpisode));
        endEpisodeET.setText(String.valueOf(endEpisode));
        pageET.setText(String.valueOf(nowPage));
        folderSizeBtn.setText(String.valueOf(folderSize));
    }

    private void saveStatus(DownLoadEvent event) {
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.DOWNLOAD_EXPLAIN,
                event.getDownloadExplain());
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.CURRENT_DOWNLOAD_EPISODE,
                event.getCurrentDownloadEpisode());
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.CURRENT_DOWNLOAD_PAGE,
                event.getCurrentDownloadPage());
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.DOWNLOAD_FOLDER_SIZE,
                event.getDownloadFolderSize());
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.DOWNLOAD_END_EPISODE,
                event.getDownloadEndEpisode());
        SharedPreferencesUtils.setSharedPreferencesData(this,
                ShareKeys.DOWNLOAD_MANGA_NAME, event.getDownloadMangaName());
    }

    private void recoverStatus() {
        if (null != SharedPreferencesUtils.getSharedPreferencesData(this,
                ShareKeys.DOWNLOAD_EXPLAIN)) {
            explain = SharedPreferencesUtils.getSharedPreferencesData(this,
                    ShareKeys.DOWNLOAD_EXPLAIN);
            nowEpisode = SharedPreferencesUtils.getIntSharedPreferencesData(
                    this, ShareKeys.CURRENT_DOWNLOAD_EPISODE);
            nowPage = SharedPreferencesUtils.getIntSharedPreferencesData(this,
                    ShareKeys.CURRENT_DOWNLOAD_PAGE);
            endEpisode = SharedPreferencesUtils.getIntSharedPreferencesData(this,
                    ShareKeys.DOWNLOAD_END_EPISODE);
            folderSize = SharedPreferencesUtils.getIntSharedPreferencesData(
                    this, ShareKeys.DOWNLOAD_FOLDER_SIZE);
            mangaName = SharedPreferencesUtils.getSharedPreferencesData(this,
                    ShareKeys.DOWNLOAD_MANGA_NAME);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                //TODO
//                Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
//                Bundle pathListBundle = new Bundle();
//                pathListBundle.putSerializable("download_MangaBean", currentManga);
//                intent.putExtra("download_folderSize", 3);
//                intent.putExtra("download_startPage", 1);
//                intent.putExtra("download_currentChapter", 0);
//                intent.putExtra("download_endChapter", currentManga.getChapters().size());
//                startService(intent);
//                baseToast.showToast("开始下载!");
                break;
            case R.id.stop:
                Intent stopIntent = new Intent(this, DownloadService.class);
                stopService(stopIntent);
                break;
            case R.id.folder_size:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
