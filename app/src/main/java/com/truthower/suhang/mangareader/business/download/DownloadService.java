package com.truthower.suhang.mangareader.business.download;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Administrator on 2017/7/25.
 */

public class DownloadService extends Service {
    private SpiderBase spider;
    private MangaBean currentManga;
    private int folderSize = 3;
    private boolean stopDownload = false;
    private int endChapter, currentChapter, startPage;
    private DownLoadEvent downLoadEvent;
    private final int TRY_COUNT_LIMIT = 3;
    private int tryCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        downLoadEvent = new DownLoadEvent(EventBusEvent.DOWNLOAD_EVENT);
        initSpider();
    }

    private void initSpider() {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.truthower.suhang.mangareader.spider." + Configure.currentWebSite + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        stopDownload = false;
        currentManga = (MangaBean) intent.getSerializableExtra("download_MangaBean");
        folderSize = intent.getIntExtra("download_folderSize", 3);
        startPage = intent.getIntExtra("download_startPage", 1);
        currentChapter = intent.getIntExtra("download_currentChapter", 0);
        endChapter = intent.getIntExtra("download_endChapter", 0);

        doGetChaptersPics(currentManga.getChapters().get(currentChapter), startPage);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取某个章节的所有图片 然后递归获取图片,一个章节完成后再递归获取下一个章节
     *
     * @param chapter
     * @param startPage
     */
    private void doGetChaptersPics(final ChapterBean chapter, final int startPage) {
        if (!stopDownload) {
            downLoadEvent.setCurrentDownloadEpisode(Integer.valueOf(chapter.getChapterPosition()));
            downLoadEvent.setCurrentDownloadPage(startPage);
            //真正的结束章节
            downLoadEvent.setDownloadEndEpisode
                    (Integer.valueOf(currentManga.getChapters().get(endChapter).getChapterPosition()));
            downLoadEvent.setDownloadExplain("正在爬取第" + chapter.getChapterPosition() + "话所有图片地址");
            downLoadEvent.setDownloadFolderSize(folderSize);
            downLoadEvent.setDownloadMangaName(currentManga.getName());
            EventBus.getDefault().post(downLoadEvent);

            spider.getMangaChapterPics(this, chapter.getChapterUrl(), new JsoupCallBack<ArrayList<String>>() {
                @Override
                public void loadSucceed(ArrayList<String> result) {
                    downloadImgs(result,
                            Integer.valueOf(chapter.getChapterPosition()), startPage,
                            new JsoupCallBack<Objects>() {
                                @Override
                                public void loadSucceed(Objects result) {
                                    currentChapter++;
                                    if (currentChapter <= endChapter) {
                                        doGetChaptersPics(currentManga.getChapters().get(currentChapter), 1);
                                    } else {
                                        //TODO 下载结束
                                        EventBus.getDefault().post(new EventBusEvent("全部下载完成!",
                                                EventBusEvent.DOWNLOAD_FINISH_EVENT));
                                        stopSelf();
                                    }
                                }

                                @Override
                                public void loadFailed(String error) {

                                }
                            });
                }

                @Override
                public void loadFailed(String error) {
                }
            });
        }
    }

    /**
     * 下载一整个章节的图片
     *
     * @param imgs        url列表
     * @param episode     仅用于命名
     * @param page        起始页
     * @param <ResultObj>
     */
    public <ResultObj> void downloadImgs(final ArrayList<String> imgs,
                                         final int episode, final int page,
                                         final JsoupCallBack<ResultObj> jsoupCallBack) {
        if (!stopDownload) {
            // 将图片下载并保存
            new Thread() {
                public void run() {
                    Bitmap bp = null;
                    if (!TextUtils.isEmpty(imgs.get(page - 1))) {
                        //从网络上获取到图片
                        try {
                            InputStream is = new URL(imgs.get(page - 1)).openStream();
                            bp = BitmapFactory.decodeStream(is);
                        } catch (IOException e) {
                            tryCount++;
                            if (tryCount <= TRY_COUNT_LIMIT) {
                                sendEvent(EventBusEvent.DOWNLOAD_FAIL_EVENT, "第" + episode + "话" +
                                        "第" + page + "页下载失败!正在第" + (tryCount + 1) + "次尝试", episode, page);
                                downloadImgs(imgs, episode, page, jsoupCallBack);
                            } else {
                                tryCount = 0;
                                downloadImgs(imgs, episode, page + 1, jsoupCallBack);
                            }
                        }
                        if (null != bp) {
                            //把图片保存到本地
                            FileSpider.getInstance().saveBitmap(bp, currentManga.getName() + "_" + episode
                                            + "_" + page + ".jpg",
                                    FileSpider.getInstance().getChildFolderName(episode, folderSize), currentManga.getName());

                            if (page + 1 <= imgs.size()) {
                                //下载完成
                                sendEvent(EventBusEvent.DOWNLOAD_EVENT, "第" + episode + "话" +
                                        "第" + page + "页下载完成!", episode, page);
                                downloadImgs(imgs, episode, page + 1, jsoupCallBack);
                            } else {
                                //下载完成
                                sendEvent(EventBusEvent.DOWNLOAD_EVENT, "第" + episode + "话下载完成!", episode, page);
                                jsoupCallBack.loadSucceed((ResultObj) null);
                            }
                        } else {
                            tryCount++;
                            if (tryCount <= TRY_COUNT_LIMIT) {
                                sendEvent(EventBusEvent.DOWNLOAD_FAIL_EVENT, "第" + episode + "话" +
                                        "第" + page + "页下载失败!正在第" + (tryCount + 1) + "次尝试", episode, page);
                                downloadImgs(imgs, episode, page, jsoupCallBack);
                            } else {
                                tryCount = 0;
                                downloadImgs(imgs, episode, page + 1, jsoupCallBack);
                            }
                        }
                    } else {
                        downloadImgs(imgs, episode, page + 1, jsoupCallBack);
                    }
                }
            }.start();
        }
    }

    private void sendEvent(int eventType, String explain, int episode, int page) {
        downLoadEvent = new DownLoadEvent(eventType);
        downLoadEvent.setCurrentDownloadEpisode(episode);
        downLoadEvent.setCurrentDownloadPage(page);
        downLoadEvent.setDownloadExplain(explain);
        downLoadEvent.setDownloadFolderSize(folderSize);
        downLoadEvent.setDownloadMangaName(currentManga.getName());
        downLoadEvent.setDownloadEndEpisode(Integer.valueOf
                (currentManga.getChapters().get(endChapter).getChapterPosition()));

        EventBus.getDefault().post(downLoadEvent);

        saveStatus(downLoadEvent);
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

    @Override
    public void onDestroy() {
        stopDownload = true;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
