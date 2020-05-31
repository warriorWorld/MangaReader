package com.truthower.suhang.mangareader.test;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.bean.WordsBookBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.db.DbAdapter;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.SerializableSparseArray;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.imageview.GlidePhotoView;
import com.truthower.suhang.mangareader.widget.imageview.WrapPhotoView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActivity extends BaseActivity implements View.OnClickListener {
    private WrapPhotoView wrapPv;
    private GlidePhotoView glidePv;
    private boolean urlSwitch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setImageUrl();
    }

    private void initView() {
        wrapPv = (WrapPhotoView) findViewById(R.id.wrap_pv);
        glidePv = (GlidePhotoView) findViewById(R.id.glide_pv);
        baseTopBar.setTitle("测试");
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {
                ImageLoader.getInstance().clearDiskCache();
                ImageLoader.getInstance().clearMemoryCache();
                Glide.get(TestActivity.this).clearMemory();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(TestActivity.this).clearDiskCache();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setImageUrl();
                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void setImageUrl() {
        if (urlSwitch) {
            wrapPv.setImgUrl("https://i7.imggur.net/one-piece/114/one-piece-2422737.jpg", Configure.normalImageOptions);
            glidePv.setImgUrl("https://i7.imggur.net/one-piece/114/one-piece-2422737.jpg");
        } else {
            wrapPv.setImgUrl("file:///storage/emulated/0/aSpider/Nanatsu no Taizai/5/manga_5_2.png", Configure.normalImageOptions);
            glidePv.setImgUrl("file:///storage/emulated/0/aSpider/Nanatsu no Taizai/5/manga_5_2.png");
        }
        urlSwitch = !urlSwitch;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }


    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
