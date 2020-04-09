package com.truthower.suhang.mangareader.business.rxdownload;

import android.content.Context;

import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.MangaDownloader;
import com.truthower.suhang.mangareader.spider.SpiderBase;

import java.io.Serializable;

public class CommonDownloader implements MangaDownloader , Serializable {
    private SpiderBase spider;

    public CommonDownloader(SpiderBase spider) {
        this.spider = spider;
    }

    @Override
    public <ResultObj> void getMangaChapterPics(Context context, String chapterUrl, JsoupCallBack<ResultObj> jsoupCallBack) {
        spider.getMangaChapterPics(context, chapterUrl, jsoupCallBack);
    }

    public void setSpider(SpiderBase spider) {
        this.spider = spider;
    }
}
