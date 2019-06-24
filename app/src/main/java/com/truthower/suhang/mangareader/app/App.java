package com.truthower.suhang.mangareader.app;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;

import com.avos.avoscloud.AVOSCloud;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.truthower.suhang.mangareader.bean.DownloadBean;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.download.DownloadMangaManager;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.crash.CrashHandler;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.youdao.sdk.app.YouDaoApplication;

/**
 * Created by Administrator on 2017/7/19.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
        initLeanCloud();
        initUserInfo();
//        initCrashHandler();
        dealFileUriExposedException();
        initDownloadManger();
//        initUmeng();
        initYouDaoOCR();
    }

    private void initYouDaoOCR() {
        YouDaoApplication.init(this, "627fbbbd88df98c2");
    }

    private void initUmeng() {
        /**
         注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，UMConfigure.init调用中appkey和channel参数请置为null）。
         */
        UMConfigure.init(getApplicationContext(), 0, "");

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
        mPushAgent.setNotificationClickHandler(new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage uMessage) {
                super.dealWithCustomAction(context, uMessage);
                //打开详情
                Intent intent = new Intent(getApplicationContext(), WebMangaDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("mangaUrl", uMessage.custom);
                startActivity(intent);
            }
        });
    }

    private void initDownloadManger() {
        DownloadMangaManager.getInstance().getCurrentChapter(getApplicationContext());
        DownloadBean.getInstance().setDownloadInfo(getApplicationContext(), DownloadBean.
                getInstance().getDownloadInfo(getApplicationContext()));
    }

    private void dealFileUriExposedException() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }

    private void initCrashHandler() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    private void initUserInfo() {
        LoginBean.getInstance().setLoginInfo(this, LoginBean.getLoginInfo(this));
    }

    private void initLeanCloud() {
        // 配置 SDK 储存
        AVOSCloud.setServer(AVOSCloud.SERVER_TYPE.API, "https://avoscloud.com");
        // 配置 SDK 云引擎
        AVOSCloud.setServer(AVOSCloud.SERVER_TYPE.ENGINE, "https://avoscloud.com");
        // 配置 SDK 推送
        AVOSCloud.setServer(AVOSCloud.SERVER_TYPE.PUSH, "https://avoscloud.com");
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, "VeSumXxFMzSVf1kStNrOqGMS-gzGzoHsz", "djw94yHsBRwSUPxhrkAaMJPd");
        AVOSCloud.setDebugLogEnabled(true);
    }

    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
