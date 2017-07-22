package com.truthower.suhang.mangareader.spider;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/18.
 */

public abstract class SpiderBase {
    protected org.jsoup.nodes.Document doc;

    public abstract <ResultObj> void getMangaList(String type, String page, final JsoupCallBack<ResultObj> jsoupCallBack);

    public abstract MangaBean getMangaDetail(String mangaURL, final JsoupCallBack jsoupCallBack);

    public abstract boolean isOneShot();

    public abstract String[] getMangaTypes();

    public abstract ArrayList<ChapterBean> getMangaChapterPics(String mangaName, String chapter, int picCount, final JsoupCallBack jsoupCallBack);

    //很多网页的下一页并不是在网址后+1 而是+n
    public abstract int nextPageNeedAddCount();
}
