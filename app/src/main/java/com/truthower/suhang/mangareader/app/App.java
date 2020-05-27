package com.truthower.suhang.mangareader.app;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.truthower.suhang.mangareader.bean.DownloadBean;
import com.truthower.suhang.mangareader.business.download.DownloadMangaManager;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.crash.CrashHandler;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.youdao.sdk.app.YouDaoApplication;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;

/**
 * Created by Administrator on 2017/7/19.
 */

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader(getApplicationContext());
//        initCrashHandler();
        dealFileUriExposedException();
        initDownloadManger();
//        initUmeng();
        initYouDao();
        Configure.isPad = DisplayUtil.isPad(this);
        checkMemoryLeak();
    }

    private void checkMemoryLeak() {
        if (!BuildConfig.DEBUG)
            return;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        builder
                .detectActivityLeaks()
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects()
                .detectLeakedSqlLiteObjects();
        builder.penaltyLog();
        StrictMode.setVmPolicy(builder.build());
    }

    private void initYouDao() {
        YouDaoApplication.init(this, "627fbbbd88df98c2");
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
