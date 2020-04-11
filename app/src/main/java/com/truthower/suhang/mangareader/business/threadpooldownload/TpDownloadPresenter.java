package com.truthower.suhang.mangareader.business.threadpooldownload;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.truthower.suhang.mangareader.business.rxdownload.DownloadCaretaker;
import com.truthower.suhang.mangareader.business.rxdownload.DownloadContract;

public class TpDownloadPresenter implements DownloadContract.Presenter {
    private Context mContext;
    private DownloadContract.View mView;

    public TpDownloadPresenter(Context context, DownloadContract.View view) {
        this.mContext = context;
        this.mView = view;
    }

    @Override
    public void getDownloadInfo() {
        mView.displayInfo(DownloadCaretaker.getDownloadMemoto(mContext));
    }

    @Override
    public void startDownload() {
        Intent intent = new Intent(mContext, TpDownloadService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(intent);
        } else {
            mContext.startService(intent);
        }
        mView.displayStart();
    }

    @Override
    public void stopDownload() {
        Intent intent = new Intent(mContext, TpDownloadService.class);
        mContext.stopService(intent);
        mView.displayStop();
    }

    @Override
    public void updateDownload() {
        mView.displayUpdate();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mView = null;
    }
}
