package com.truthower.suhang.mangareader.listener;

import android.content.Context;

public interface MangaDownloader {
    <ResultObj> void getMangaChapterPics
            (final Context context, final String chapterUrl, final JsoupCallBack<ResultObj> jsoupCallBack);

}
