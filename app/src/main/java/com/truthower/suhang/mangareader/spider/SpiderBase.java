package com.truthower.suhang.mangareader.spider;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/18.
 */

public abstract class SpiderBase {
    public abstract ArrayList<MangaBean> getMangaList(String type, String page);

    public abstract MangaBean getMangaDetail(String mangaName);

    public abstract boolean isOneShot();

    public abstract String[] getMangaTypes();

    public abstract ArrayList<ChapterBean> getMangaChapterPics(String mangaName, String chapter, int picCount);
}
