package com.truthower.suhang.mangareader.spider;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;

import java.util.ArrayList;

/**
 * http://www.mangareader.net/
 */
public class MangaReaderSpider extends SpiderBase {

    private MangaReaderSpider() {
    }

    private static volatile MangaReaderSpider instance = null;

    public static MangaReaderSpider getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (MangaReaderSpider.class) {
                //双重锁定
                if (instance == null) {
                    instance = new MangaReaderSpider();
                }
            }
        }
        return instance;
    }

    @Override
    public ArrayList<MangaBean> getMangaList(String type, String page) {
        return null;
    }

    @Override
    public MangaBean getMangaDetail(String mangaName) {
        return null;
    }

    @Override
    public boolean isOneShot() {
        return false;
    }

    @Override
    public String[] getMangaTypes() {
        return new String[0];
    }

    @Override
    public ArrayList<ChapterBean> getMangaChapterPics(String mangaName, String chapter, int picCount) {
        return null;
    }

}
