package com.truthower.suhang.mangareader.bean;

/**
 * Created by Administrator on 2017/7/18.
 */

public class RxDownloadPageBean extends BaseBean {
    private String pageUrl;
    private String pageName;
    private RxDownloadChapterBean chapterBean;

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public RxDownloadChapterBean getChapterBean() {
        return chapterBean;
    }

    public void setChapterBean(RxDownloadChapterBean chapterBean) {
        this.chapterBean = chapterBean;
    }
}
