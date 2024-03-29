package com.truthower.suhang.mangareader.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RxDownloadChapterBean extends BaseBean {
    private static final long serialVersionUID = 8131338942807515398L;
    private String chapterUrl;
    private String chapterName;
    private volatile int downloadedCount;
    private int pageCount;
    private CopyOnWriteArrayList<RxDownloadPageBean> pages;

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

    public CopyOnWriteArrayList<RxDownloadPageBean> getPages() {
        return pages;
    }

    public void setPages(CopyOnWriteArrayList<RxDownloadPageBean> pages) {
        this.pages = pages;
    }

    public int getPageCount() {
        return pageCount;
    }

    public synchronized void addDownloadedCount() {
        downloadedCount++;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getDownloadCount() {
        if (null == pages || pages.size() == 0) {
            return 0;
        }
        int count = 0;
        for (RxDownloadPageBean item : pages) {
            if (item.isDownloaded()) {
                count++;
            }
        }
        return count;
    }

    public int getDownloadedCount() {
        return downloadedCount;
    }

    public boolean isDownloaded() {
        return downloadedCount == pageCount;
    }
//    public boolean isDownloaded() {
//        if (null == pages || pages.size() == 0) {
//            return false;
//        }
//        for (RxDownloadPageBean item : pages) {
//            if (!item.isDownloaded()) {
//                return false;
//            }
//        }
//        return true;
//    }
}
