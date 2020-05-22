//package com.truthower.suhang.mangareader.business.rxdownload;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//
//import com.android.volley.Request;
//import com.android.volley.VolleyError;
//import com.truthower.suhang.mangareader.base.DependencyInjector;
//import com.truthower.suhang.mangareader.bean.YoudaoResponse;
//import com.truthower.suhang.mangareader.config.Configure;
//import com.truthower.suhang.mangareader.volley.VolleyCallBack;
//import com.truthower.suhang.mangareader.volley.VolleyTool;
//
//import java.util.HashMap;
//
//public class DownloadPresenter implements DownloadContract.Presenter {
//    private Context mContext;
//    private DownloadContract.View mView;
//
//    public DownloadPresenter(Context context, DownloadContract.View view) {
//        this.mContext = context;
//        this.mView = view;
//    }
//
//    @Override
//    public void getDownloadInfo() {
//        mView.displayInfo(DownloadCaretaker.getDownloadMemoto(mContext));
//    }
//
//    @Override
//    public void startDownload() {
//        Intent intent = new Intent(mContext, DownloadService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mContext.startForegroundService(intent);
//        } else {
//            mContext.startService(intent);
//        }
//        mView.displayStart();
//    }
//
//    @Override
//    public void stopDownload() {
//        Intent intent = new Intent(mContext, DownloadService.class);
//        mContext.stopService(intent);
//        mView.displayStop();
//    }
//
//    @Override
//    public void updateDownload() {
//        mView.displayUpdate();
//    }
//
//    @Override
//    public void onDestroy() {
//        mContext = null;
//        mView = null;
//    }
//}
