//
//package com.truthower.suhang.mangareader.business.download;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.widget.RemoteViews;
//
//import com.truthower.suhang.mangareader.R;
//import com.truthower.suhang.mangareader.bean.DownloadBean;
//import com.truthower.suhang.mangareader.bean.DownloadChapterBean;
//import com.truthower.suhang.mangareader.bean.DownloadPageBean;
//import com.truthower.suhang.mangareader.bean.MangaBean;
//import com.truthower.suhang.mangareader.config.ShareKeys;
//import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
//import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
//import com.truthower.suhang.mangareader.listener.JsoupCallBack;
//import com.truthower.suhang.mangareader.spider.SpiderBase;
//import com.truthower.suhang.mangareader.utils.Logger;
//import com.truthower.suhang.mangareader.utils.ShareObjUtil;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.ArrayList;
//
//import androidx.core.app.NotificationCompat;
//
///**
// * userInfo管理类
// * <p>
// *
// * @author Administrator
// */
//public class DownloadMangaManager {
//    private DownloadChapterBean currentChapter;
//    private SpiderBase spider;
//    private ArrayList<DownloadChapterBean> oneShotDownloadList;
//    private int oneShotListTotalSize = 0;
//    private NotificationCompat.Builder notificationBuilder;
//    private RemoteViews remoteViews;
//    private NotificationManager notificationManager;
//
//    private DownloadMangaManager() {
//        initSpider();
//    }
//
//    private static volatile DownloadMangaManager instance = null;
//
//    public static DownloadMangaManager getInstance() {
//        if (instance == null) {
//            synchronized (DownloadMangaManager.class) {
//                if (instance == null) {
//                    instance = new DownloadMangaManager();
//                }
//            }
//        }
//        return instance;
//    }
//
//    private void createNotification(Context context) {
//        try {
//            notificationBuilder = new NotificationCompat.Builder(context);
//            remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_download);
//            notificationManager = (NotificationManager) context.getSystemService
//                    (context.NOTIFICATION_SERVICE);
//            notificationBuilder.setSmallIcon(R.drawable.spider_128);
//            notificationBuilder.setContent(remoteViews);
//            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                NotificationChannel mChannel = new NotificationChannel("manga_channel", "manga", NotificationManager.IMPORTANCE_LOW);
//                notificationManager.createNotificationChannel(mChannel);
//                notificationBuilder.setChannelId("manga_channel");
//            }
////        remoteViews.setImageViewResource(R.id.image, R.mipmap.timg);
//            remoteViews.setTextViewText(R.id.notification_title_tv, DownloadBean.getInstance().getCurrentManga().getName() + "下载中...");
//            if (DownloadBean.getInstance().isOne_shot()) {
//                remoteViews.setProgressBar(R.id.notification_download_progress_bar, oneShotListTotalSize, 0, false);
//            } else {
//                remoteViews.setProgressBar(R.id.notification_download_progress_bar, 10,
//                        0, false);
//            }
//            notificationManager.notify(10, notificationBuilder.build());
//
//            Intent intent = new Intent(context, DownloadActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            notificationBuilder.setContentIntent(pendingIntent);
//        } catch (Exception e) {
//            Logger.d("e" + e);
//        }
//    }
//
//    private void updateNotification() {
//        try {
//            if (DownloadBean.getInstance().isOne_shot()) {
//                remoteViews.setProgressBar(R.id.notification_download_progress_bar,
//                        oneShotListTotalSize,
//                        oneShotListTotalSize
//                                - oneShotDownloadList.size(), false);
//            } else {
//                remoteViews.setProgressBar(R.id.notification_download_progress_bar, currentChapter.getChapter_size()
//                        , currentChapter.getChapter_size() - currentChapter.getPages().size(), false);
//            }
//            notificationManager.notify(10, notificationBuilder.build());
//        } catch (Exception e) {
//            Logger.d("e" + e);
//        }
//    }
//
//    public void doDownload(final Context context) {
//        createNotification(context);
//        stopDownload(context);
//        if ((null == DownloadBean.getInstance().getDownload_chapters() ||
//                DownloadBean.getInstance().getDownload_chapters().size() <= 0) &&
//                (null == currentChapter || null == currentChapter.getPages())) {
//            //没有章节了
//            //下载完成
//            reset(context);
//            EventBus.getDefault().post(new DownLoadEvent(EventBusEvent.DOWNLOAD_FINISH_EVENT));
//            return;
//        }
//        if (DownloadBean.getInstance().isOne_shot()) {
//            oneShotDownloadList = DownloadBean.getInstance().getDownload_chapters();
//            oneShotListTotalSize = oneShotDownloadList.size();
//            doOneShotDownload(context);
//        } else {
//            //|| currentChapter.getPages().size() <= 0删掉了这个条件 因为这个条件会导致 下载中恰好在一话刚开始时停止时，下次会忽略这话下载的问题
//            if (null == currentChapter || null == currentChapter.getPages()) {
//                //当前章节空了的时候 点前章节赋值为新的章节 移除空章节
//                currentChapter = DownloadBean.getInstance().getDownload_chapters().get(0);
//                saveCurrentChapter(context);
//                ArrayList<DownloadChapterBean> temp = DownloadBean.getInstance().getDownload_chapters();
//                temp.remove(0);
//                DownloadBean.getInstance().setDownload_chapters(context, temp);
//
//                MangaBean tempMangaBean = DownloadBean.getInstance().getCurrentManga();
//                //mangabean也得remove
//                tempMangaBean.getChapters().remove(0);
//                DownloadBean.getInstance().setMangaBean(context, tempMangaBean);
//                EventBus.getDefault().post(new DownLoadEvent(EventBusEvent.DOWNLOAD_CHAPTER_START_EVENT));
//            }
//
//            final String mangaName = DownloadBean.getInstance().initMangaFileName();
//            if (null == spider) {
//                initSpider();
//            }
//            if (null != currentChapter.getPages() && currentChapter.getPages().size() > 0) {
//                //说明上次那一话还没下载完  继续上次下载
//                Intent intent = new Intent(context, DownloadIntentService.class);
//                for (int i = 0; i < currentChapter.getPages().size(); i++) {//循环启动任务
//                    DownloadPageBean item = currentChapter.getPages().get(i);
//                    intent.putExtra(DownloadIntentService.DOWNLOAD_URL, item.getPage_url());
//                    intent.putExtra(DownloadIntentService.MANGA_FOLDER_NAME, mangaName);
//                    intent.putExtra(DownloadIntentService.CHILD_FOLDER_NAME,
//                            currentChapter.getChapter_child_folder_name());
//                    intent.putExtra(DownloadIntentService.PAGE_NAME, item.getPage_file_name());
//                    context.startService(intent);
//                }
//            } else {
//                spider.getMangaChapterPics(context, currentChapter.getChapter_url(), new JsoupCallBack<ArrayList<String>>() {
//                    @Override
//                    public void loadSucceed(ArrayList<String> result) {
//                        Intent intent = new Intent(context, DownloadIntentService.class);
//                        currentChapter.setChapter_size(result.size());
//                        ArrayList<DownloadPageBean> pages = new ArrayList<>();
//                        for (int i = 0; i < result.size(); i++) {//循环启动任务
//                            DownloadPageBean item = new DownloadPageBean();
//                            item.setPage_file_name(mangaName + "_" +
//                                    currentChapter.getChapter_title()
//                                    + "_" + i + ".png");
//                            item.setPage_url(result.get(i));
//                            pages.add(item);
//                            intent.putExtra(DownloadIntentService.DOWNLOAD_URL, item.getPage_url());
//                            intent.putExtra(DownloadIntentService.MANGA_FOLDER_NAME, mangaName);
//                            intent.putExtra(DownloadIntentService.CHILD_FOLDER_NAME,
//                                    currentChapter.getChapter_child_folder_name());
//                            intent.putExtra(DownloadIntentService.PAGE_NAME, item.getPage_file_name());
//                            context.startService(intent);
//                        }
//                        currentChapter.setPages(pages);
//                    }
//
//                    @Override
//                    public void loadFailed(String error) {
//                    }
//                });
//            }
//        }
//    }
//
//    public void doOneShotDownload(final Context context) {
//        String mangaName = DownloadBean.getInstance().initMangaFileName();
//
//        Intent intent = new Intent(context, DownloadIntentService.class);
//        intent.putExtra(DownloadIntentService.DOWNLOAD_URL, oneShotDownloadList.get(0).getImg_url());
//        intent.putExtra(DownloadIntentService.MANGA_FOLDER_NAME, mangaName);
//        intent.putExtra(DownloadIntentService.CHILD_FOLDER_NAME, "one shot");
//        intent.putExtra(DownloadIntentService.PAGE_NAME, oneShotDownloadList.get(0).getChapter_title());
//        context.startService(intent);
//    }
//
//    private void initSpider() {
//        try {
//            spider = (SpiderBase) Class.forName
//                    ("com.truthower.suhang.mangareader.spider." + DownloadBean.getInstance().getWebSite() + "Spider").newInstance();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (java.lang.InstantiationException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void downloadOneShotPageDone(Context context, String url) {
//        EventBus.getDefault().post(new DownLoadEvent(EventBusEvent.DOWNLOAD_CHAPTER_START_EVENT));
//        if (oneShotDownloadList.size() == 1 && oneShotDownloadList.get(0).getImg_url().equals(url)) {
//            //下载完成
//            reset(context);
//            return;
//        }
//        for (int i = 0; i < oneShotDownloadList.size(); i++) {
//            if (url.equals(oneShotDownloadList.get(i).getImg_url())) {
//                oneShotDownloadList.remove(i);
//                doOneShotDownload(context);
//                return;
//            }
//        }
//    }
//
//    public void downloadPageDone(Context context, String url) {
//        try {
//            updateNotification();
//            if (DownloadBean.getInstance().isOne_shot()) {
//                downloadOneShotPageDone(context, url);
//            } else {
//                EventBus.getDefault().post(new DownLoadEvent(EventBusEvent.DOWNLOAD_PAGE_FINISH_EVENT));
//
//                if (currentChapter.getPages().size() == 1 && currentChapter.getPages().get(0).getPage_url().equals(url)) {
//                    //下载完一个章节
//                    currentChapter = null;
//                    doDownload(context);
//                    return;
//                }
//                for (int i = 0; i < currentChapter.getPages().size(); i++) {
//                    if (url.equals(currentChapter.getPages().get(i).getPage_url())) {
//                        currentChapter.getPages().remove(i);
//                        saveCurrentChapter(context);
//                        return;
//                    }
//                }
//            }
//        } catch (Exception e) {
//
//        }
//    }
//
//    public int getOneShotListTotalSize() {
//        return oneShotListTotalSize;
//    }
//
//    public int getOneShotLeftSize() {
//        if (null == oneShotDownloadList) {
//            return 0;
//        } else {
//            return oneShotDownloadList.size();
//        }
//    }
//
//    public void stopDownload(Context context) {
//        Intent stopIntent = new Intent(context, DownloadIntentService.class);
//        context.stopService(stopIntent);
//    }
//
//    public void reset(Context context) {
//        stopDownload(context);
//        if (null != notificationManager) {
//            notificationManager.cancelAll();
//        }
//        DownloadBean.getInstance().clean(context);
//        oneShotDownloadList = null;
//        currentChapter = null;
//        ShareObjUtil.deleteFile(context, ShareKeys.CURRENT_CHAPTER_KEY);
//    }
//
//    //这个最好只有在application类里调用一次(即刚进入应用时调用一次),其他情况直接用单例就好,调用这个效率太低了
//    //因为我一直在不断的给currentChapter赋值和置空并以他是否为空做进一步判断 而这个方法又会给currentChapter赋值 所以这个东西只允许App调用一次
//    public DownloadChapterBean getCurrentChapter(Context context) {
//        if (null != currentChapter) {
//            return currentChapter;
//        }
//        currentChapter = (DownloadChapterBean) ShareObjUtil.getObject(context, ShareKeys.CURRENT_CHAPTER_KEY);
//        return currentChapter;
//    }
//
//    public DownloadChapterBean getCurrentChapter() {
//        return currentChapter;
//    }
//
//    public void saveCurrentChapter(Context context) {
//        ShareObjUtil.saveObject(context, currentChapter, ShareKeys.CURRENT_CHAPTER_KEY);
//    }
//}
