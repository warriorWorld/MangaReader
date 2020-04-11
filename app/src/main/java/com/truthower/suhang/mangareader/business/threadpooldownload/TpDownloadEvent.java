package com.truthower.suhang.mangareader.business.threadpooldownload;

import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;

public class TpDownloadEvent extends EventBusEvent {
    private RxDownloadBean mDownloadBean;
    private RxDownloadChapterBean currentChapter;

    public TpDownloadEvent(int event) {
        super(event);
    }

    public TpDownloadEvent(int event, RxDownloadBean downloadBean) {
        super(event);
        this.mDownloadBean = downloadBean;
    }

    public TpDownloadEvent(int event, RxDownloadChapterBean downloadChapterBean) {
        super(event);
        this.currentChapter = downloadChapterBean;
    }

    public RxDownloadBean getDownloadBean() {
        return mDownloadBean;
    }

    public void setDownloadBean(RxDownloadBean downloadBean) {
        mDownloadBean = downloadBean;
    }

    public RxDownloadChapterBean getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(RxDownloadChapterBean currentChapter) {
        this.currentChapter = currentChapter;
    }
}
