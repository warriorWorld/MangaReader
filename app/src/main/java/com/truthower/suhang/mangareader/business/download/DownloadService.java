package com.truthower.suhang.mangareader.business.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.spider.SpiderBase;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Administrator on 2017/7/25.
 */

public class DownloadService extends Service {
    private SpiderBase spider;
    private MangaBean currentManga;
    private int folderSize = 3;
    private boolean isRunning = false;
    private int endChapter, currentChapter, startPage;
    private DownLoadEvent downLoadEvent;

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
        if (!isRunning) {
            currentManga = (MangaBean) intent.getSerializableExtra("download_MangaBean");
            folderSize = intent.getIntExtra("download_folderSize", 3);
            startPage = intent.getIntExtra("download_startPage", 1);
            currentChapter = intent.getIntExtra("download_currentChapter", 0);
            endChapter = intent.getIntExtra("download_endChapter", 0);

            doGetChaptersPics(currentManga.getChapters().get(currentChapter), startPage);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 获取某个章节的所有图片 然后递归获取图片,一个章节完成后再递归获取下一个章节
     *
     * @param chapter
     * @param startPage
     */
    private void doGetChaptersPics(final ChapterBean chapter, final int startPage) {
        isRunning = true;
        downLoadEvent.setCurrentDownloadEpisode(Integer.valueOf(chapter.getChapterPosition()));
        downLoadEvent.setCurrentDownloadPage(startPage);
        downLoadEvent.setDownloadEndEpisode(endChapter);
        downLoadEvent.setDownloadExplain("正在爬取第" + chapter.getChapterPosition() + "话所有图片地址");
        downLoadEvent.setDownloadFolderSize(folderSize);
        downLoadEvent.setDownloadMangaName(currentManga.getName());
        EventBus.getDefault().post(downLoadEvent);

        spider.getMangaChapterPics(this, chapter.getChapterUrl(), new JsoupCallBack<ArrayList<String>>() {
            @Override
            public void loadSucceed(ArrayList<String> result) {
                FileSpider.getInstance().downloadImgs(currentManga.getName(), result,
                        Integer.valueOf(chapter.getChapterPosition()), endChapter, startPage, folderSize,
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
                                    isRunning = false;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
