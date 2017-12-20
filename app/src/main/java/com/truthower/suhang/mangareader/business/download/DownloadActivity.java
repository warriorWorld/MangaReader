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
 * <p>
 * 已弃用
 */

public class DownloadActivity extends BaseActivity implements View.OnClickListener {

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
    }

    private void initUI() {
        baseTopBar.setTitle("下载");
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
                        baseToast.showToast("test");
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
