package com.truthower.suhang.mangareader.spider;

import android.content.Context;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/18.
 */

public abstract class SpiderBase {
    protected org.jsoup.nodes.Document doc;

    public enum SearchType {
        BY_MANGA_NAME,
        BY_MANGA_AUTHOR,
        BY_COMMENT,
        BY_GRADE
    }

    public abstract <ResultObj> void getMangaList(String type, String page, final JsoupCallBack<ResultObj> jsoupCallBack);

    public abstract <ResultObj> void getMangaDetail(final String mangaURL, final JsoupCallBack<ResultObj> jsoupCallBack);

    public abstract boolean isOneShot();

    public abstract String[] getMangaTypes();

    public abstract String[] getMangaTypeCodes();

    public abstract String[] getAdultTypes();

    public abstract <ResultObj> void getMangaChapterPics
            (final Context context, final String chapterUrl, final JsoupCallBack<ResultObj> jsoupCallBack);

    public abstract <ResultObj> void getSearchResultList(SearchType type, String keyWord, final JsoupCallBack<ResultObj> jsoupCallBack);

    //很多网页的下一页并不是在网址后+1 而是+n
    public abstract int nextPageNeedAddCount();

    public abstract String getWebUrl();
}
