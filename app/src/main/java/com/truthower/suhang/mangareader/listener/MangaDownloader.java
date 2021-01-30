package com.truthower.suhang.mangareader.listener;

import android.content.Context;

import com.truthower.suhang.mangareader.bean.RxDownloadPageBean;

import java.util.List;

public abstract class MangaDownloader {
    public abstract <ResultObj> void getMangaChapterPics
            (final Context context, final String chapterUrl, final JsoupCallBack<ResultObj> jsoupCallBack);

    /**
     *  用于修复一个bug，该bug是由于某些网站里的图片是只爬了第一张后边是直接拼出来的，所以当第一张
     *  图片是比如.jpg格式但是后边有些图片却是.png的情况下会导致下载失败。
     *  所以需要替换后再次重试。
     *  没有此问题的网站不需要重新该方法
     */
    public List<RxDownloadPageBean> getFixedUrls(List<RxDownloadPageBean> list) {
        return null;
    }
}
