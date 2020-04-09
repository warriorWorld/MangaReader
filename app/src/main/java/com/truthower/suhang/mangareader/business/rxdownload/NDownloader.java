package com.truthower.suhang.mangareader.business.rxdownload;

import android.content.Context;

import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.MangaDownloader;

import java.io.Serializable;

public class NDownloader implements MangaDownloader , Serializable {
    @Override
    public <ResultObj> void getMangaChapterPics(Context context, String chapterUrl, JsoupCallBack<ResultObj> jsoupCallBack) {

    }
}
