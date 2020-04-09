package com.truthower.suhang.mangareader.bean;/**
 * Created by Administrator on 2016/11/2.
 */

import android.content.Context;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.business.rxdownload.CommonDownloader;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;

import java.util.ArrayList;


/**
 * 作者：苏航 on 2016/11/2 15:52
 * 邮箱：772192594@qq.com
 */
public class RxDownloadBean extends BaseBean {
    private String mangaName;
    private String mangaUrl;
    private String thumbnailUrl;
    private ArrayList<RxDownloadChapterBean> chapters;
    private CommonDownloader downloader;

    public String getMangaName() {
        return mangaName;
    }

    public void setMangaName(String mangaName) {
        this.mangaName = mangaName;
    }

    public String getMangaUrl() {
        return mangaUrl;
    }

    public void setMangaUrl(String mangaUrl) {
        this.mangaUrl = mangaUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public ArrayList<RxDownloadChapterBean> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<RxDownloadChapterBean> chapters) {
        this.chapters = chapters;
    }

    public CommonDownloader getDownloader() {
        return downloader;
    }

    public void setDownloader(CommonDownloader downloader) {
        this.downloader = downloader;
    }
}
