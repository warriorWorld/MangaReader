package com.truthower.suhang.mangareader.eventbus;

/**
 * Created by Administrator on 2017/7/25.
 */

public class DownLoadEvent extends EventBusEvent {
    private String downloadExplain;
    private int currentDownloadEpisode;
    private int currentDownloadPage;
    private int downloadFolderSize;
    private int downloadEndEpisode;
    private String downloadMangaName;

    public DownLoadEvent(int eventType) {
        super(eventType);
    }

    public String getDownloadExplain() {
        return downloadExplain;
    }

    public void setDownloadExplain(String downloadExplain) {
        this.downloadExplain = downloadExplain;
    }

    public int getCurrentDownloadEpisode() {
        return currentDownloadEpisode;
    }

    public void setCurrentDownloadEpisode(int currentDownloadEpisode) {
        this.currentDownloadEpisode = currentDownloadEpisode;
    }

    public int getCurrentDownloadPage() {
        return currentDownloadPage;
    }

    public void setCurrentDownloadPage(int currentDownloadPage) {
        this.currentDownloadPage = currentDownloadPage;
    }

    public int getDownloadFolderSize() {
        return downloadFolderSize;
    }

    public void setDownloadFolderSize(int downloadFolderSize) {
        this.downloadFolderSize = downloadFolderSize;
    }

    public int getDownloadEndEpisode() {
        return downloadEndEpisode;
    }

    public void setDownloadEndEpisode(int downloadEndEpisode) {
        this.downloadEndEpisode = downloadEndEpisode;
    }

    public String getDownloadMangaName() {
        return downloadMangaName;
    }

    public void setDownloadMangaName(String downloadMangaName) {
        this.downloadMangaName = downloadMangaName;
    }
}
