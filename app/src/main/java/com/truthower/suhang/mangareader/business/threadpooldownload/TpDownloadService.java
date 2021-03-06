package com.truthower.suhang.mangareader.business.threadpooldownload;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.SparseArray;
import android.widget.RemoteViews;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.bean.RxDownloadPageBean;
import com.truthower.suhang.mangareader.business.rxdownload.DownloadCaretaker;
import com.truthower.suhang.mangareader.business.rxdownload.FailedPageCaretaker;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.MangaDownloader;
import com.truthower.suhang.mangareader.listener.OnResultListener;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.SerializableSparseArray;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;
import com.truthower.suhang.mangareader.utils.VibratorUtil;
import com.truthower.suhang.mangareader.widget.toast.EasyToast;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class TpDownloadService extends Service {
    public static final String SERVICE_PCK_NAME = "com.truthower.suhang.mangareader.business.threadpooldownload.TpDownloadService";
    private String TAG = "TpDownloadService";
    private RxDownloadBean downloadBean;
    private List<RxDownloadChapterBean> chapters;
    private EasyToast mEasyToast;
    private MangaDownloader mDownloader;
    private NotificationCompat.Builder notificationBuilder;
    private RemoteViews remoteViews;
    private NotificationManager notificationManager;
    private RxDownloadChapterBean currentChapter;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private ArrayList<RxDownloadChapterBean> cacheChapters;
    private TpDownloadEvent mEvent;
    private CopyOnWriteArrayList<RxDownloadPageBean> failedPages = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<RxDownloadPageBean> finalFailedPages = new CopyOnWriteArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.setTag("TpDownloadService");
        downloadBean = DownloadCaretaker.getDownloadMemoto(this);
        failedPages = FailedPageCaretaker.getFailedPages(this);
        if (null == failedPages) {
            failedPages = new CopyOnWriteArrayList<>();
        }
        //chapters在很多线程中同时操作 需要线程安全
        chapters = Collections.synchronizedList(downloadBean.getChapters());
        mDownloader = downloadBean.getDownloader();
        cacheChapters = (ArrayList<RxDownloadChapterBean>) ShareObjUtil.getObject(this, downloadBean.getMangaName() + ShareKeys.BRIDGE_KEY);
        if (null == cacheChapters) {
            cacheChapters = new ArrayList<RxDownloadChapterBean>();
        }
        mEvent = new TpDownloadEvent(EventBusEvent.DOWNLOAD_PAGE_FINISH_EVENT);
        createNotification(this);
        startForeground(10, notificationBuilder.build());
        mEasyToast = new EasyToast(this);
    }

    private void createNotification(Context context) {
        try {
            notificationBuilder = new NotificationCompat.Builder(context);
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
            notificationManager = (NotificationManager) context.getSystemService
                    (context.NOTIFICATION_SERVICE);
            notificationBuilder.setSmallIcon(R.drawable.spider_128);
            notificationBuilder.setContent(remoteViews);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel("manga_channel", "manga", NotificationManager.IMPORTANCE_LOW);
                notificationManager.createNotificationChannel(mChannel);
                notificationBuilder.setChannelId("manga_channel");
            }
//        remoteViews.setImageViewResource(R.id.image, R.mipmap.timg);
            remoteViews.setTextViewText(R.id.notification_title_tv, downloadBean.getMangaName() + "下载中...");
            remoteViews.setProgressBar(R.id.notification_download_progress_bar, 10,
                    0, false);
            notificationManager.notify(10, notificationBuilder.build());

            Intent intent = new Intent(context, TpDownloadActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            notificationBuilder.setContentIntent(pendingIntent);
        } catch (Exception e) {
            Logger.d("e" + e);
        }
    }

    private void updateNotification() {
        try {
            remoteViews.setProgressBar(R.id.notification_download_progress_bar, currentChapter.getPageCount()
                    , currentChapter.getDownloadedCount(), false);
            notificationManager.notify(10, notificationBuilder.build());
        } catch (Exception e) {
            Logger.d("e" + e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getChapterInfo();
        return super.onStartCommand(intent, flags, startId);
    }

    private void getChapterInfo() {
        if (null == chapters || chapters.size() <= 0) {
            //下载完成
            if (null == failedPages || failedPages.size() <= 0) {
                downloadFinished();
            } else {
                retryFaliedPages();
            }
            return;
        }
        currentChapter = chapters.get(0);
        if (null != currentChapter.getPages() && currentChapter.getPages().size() > 0) {
            //之前获取过该章节的图片地址的情况
            Logger.d(TAG + "previous pages");
            for (RxDownloadPageBean item : currentChapter.getPages()) {
                if (!item.isDownloaded()) {
                    executeRunable(item);
                }
            }
        } else {
            mDownloader.getMangaChapterPics(this, currentChapter.getChapterUrl(), new JsoupCallBack<ArrayList<String>>() {
                @Override
                public void loadSucceed(ArrayList<String> result) {
                    currentChapter.setPageCount(result.size());
                    CopyOnWriteArrayList<RxDownloadPageBean> pages = new CopyOnWriteArrayList<>();
                    for (int i = 0; i < result.size(); i++) {
                        RxDownloadPageBean item = new RxDownloadPageBean();
                        item.setPageUrl(result.get(i));
                        item.setPageName("manga_" + currentChapter.getChapterName() + "_" + i + ".png");
                        item.setChapterName(currentChapter.getChapterName());
                        item.setMangaName(downloadBean.getMangaName());
                        pages.add(item);

                        executeRunable(item);
                    }
                    currentChapter.setPages(pages);
                    EventBus.getDefault().post(new EventBusEvent("获取图片地址成功,开始下载图片.", EventBusEvent.DOWNLOAD_MESSAGE_EVENT));
                    try {
                        //处理缓存
                        cacheChapters.add(currentChapter);
                        ShareObjUtil.saveObject(TpDownloadService.this, cacheChapters, downloadBean.getMangaName() + ShareKeys.BRIDGE_KEY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void loadFailed(String error) {
                    Logger.d("chapter load failed: " + error);
                    EventBus.getDefault().post(new EventBusEvent("获取图片地址失败:" + error, EventBusEvent.DOWNLOAD_MESSAGE_EVENT));
                    VibratorUtil.Vibrate(TpDownloadService.this, 200);
                    getChapterInfo();
//                    stopSelf();
                }
            });
        }
    }

    private void downloadFinished() {
        //下载完成
        mEasyToast.showToast(downloadBean.getMangaName() + "全部下载完成!");
        chapters.clear();
        DownloadCaretaker.clean(this);
        FailedPageCaretaker.clean(this);
        failedPages.clear();
        finalFailedPages.clear();
        mEvent.setEventType(EventBusEvent.DOWNLOAD_FINISH_EVENT);
        EventBus.getDefault().post(mEvent);
        stopSelf();
    }

    private void retryFaliedPages() {
        finalFailedPages.clear();
        currentChapter = new RxDownloadChapterBean();
        currentChapter.setPages(failedPages);
        currentChapter.setPageCount(failedPages.size());
        currentChapter.setChapterName("失败图片合集");
        for (final RxDownloadPageBean item : failedPages) {
            mExecutorService.execute(new PageDownloadRunner(item, new OnResultListener() {
                @Override
                public void onFinish() {
                    oneFailedPageFinished();
                }

                @Override
                public void onFailed() {
                    VibratorUtil.Vibrate(TpDownloadService.this, 50);
                    finalFailedPages.add(item);
                    oneFailedPageFinished();
                }
            }));
        }
    }

    private synchronized void oneFailedPageFinished() {
        currentChapter.addDownloadedCount();
        mEvent.setCurrentChapter(currentChapter);
        mEvent.setEventType(EventBusEvent.DOWNLOAD_PAGE_FINISH_EVENT);
        EventBus.getDefault().post(mEvent);
        updateNotification();
        Logger.d("downloaded: " + currentChapter.getDownloadedCount() + "/" + currentChapter.getPageCount());
        if (currentChapter.isDownloaded()) {
            if (null != finalFailedPages && finalFailedPages.size() > 0) {
                List<RxDownloadPageBean> list = mDownloader.getFixedUrls(finalFailedPages);
                if (list == null) {
                    //有些网站没有这个问题所以直接结束
                    downloadFinished();
                } else {
                    failedPages = new CopyOnWriteArrayList<>(list);
                    retryFaliedPages();
                }
            } else {
                downloadFinished();
            }
        }
    }

    private void executeRunable(final RxDownloadPageBean item) {
        try {
            mExecutorService.execute(new PageDownloadRunner(item, new OnResultListener() {
                @Override
                public void onFinish() {
                    onePageFinished();
                }

                @Override
                public void onFailed() {
                    failedPages.add(item);
                    //即使失败也继续下载
                    onePageFinished();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //addDownloadedCount 虽然能保证原子性 但该方法也必须保证原子性 否则有可能迅速调用多次该方法导致多次remove chapter
    private synchronized void onePageFinished() {
        currentChapter.addDownloadedCount();
        mEvent.setCurrentChapter(currentChapter);
        mEvent.setEventType(EventBusEvent.DOWNLOAD_PAGE_FINISH_EVENT);
        EventBus.getDefault().post(mEvent);
        updateNotification();
        Logger.d("downloaded: " + currentChapter.getDownloadedCount() + "/" + currentChapter.getPageCount());
        if (currentChapter.isDownloaded()) {
            Logger.d("one chapter downloaded; chapter:" + currentChapter.getChapterName());
            chapters.remove(0);
            mEvent.setEventType(EventBusEvent.DOWNLOAD_CHAPTER_FINISH_EVENT);
            mEvent.setDownloadBean(downloadBean);
            EventBus.getDefault().post(mEvent);
            DownloadCaretaker.saveDownloadMemoto(TpDownloadService.this, downloadBean);
            FailedPageCaretaker.saveFailedPages(TpDownloadService.this, failedPages);
            getChapterInfo();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mExecutorService.shutdown();
        DownloadCaretaker.saveDownloadMemoto(this, downloadBean);
        FailedPageCaretaker.saveFailedPages(this, failedPages);
    }
}