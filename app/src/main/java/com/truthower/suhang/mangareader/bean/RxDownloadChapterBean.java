package com.truthower.suhang.mangareader.bean;

import java.util.ArrayList;

public class RxDownloadChapterBean extends BaseBean {
    private String chapterUrl;
    private String chapterName;
    private int pageCount;
    private ArrayList<RxDownloadPageBean> pages;

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public ArrayList<RxDownloadPageBean> getPages() {
        return pages;
    }

    public void setPages(ArrayList<RxDownloadPageBean> pages) {
        this.pages = pages;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
