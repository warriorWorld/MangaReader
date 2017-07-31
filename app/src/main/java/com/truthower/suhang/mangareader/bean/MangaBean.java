package com.truthower.suhang.mangareader.bean;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/3.
 */
public class MangaBean extends BaseBean {
    private String url;
    private String localThumbnailUrl;
    private String name;
    private String webThumbnailUrl;
    private String author;
    private String last_update;
    private String[] types;
    private boolean isCollected;
    private ArrayList<ChapterBean> chapters;
    private String description;//漫画介绍

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public ArrayList<ChapterBean> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<ChapterBean> chapters) {
        this.chapters = chapters;
    }

    public String getLocalThumbnailUrl() {
        if (!TextUtils.isEmpty(localThumbnailUrl) && !localThumbnailUrl.contains("file://")) {
            return "file://" + localThumbnailUrl;
        } else {
            return localThumbnailUrl;
        }
    }

    public void setLocalThumbnailUrl(String localThumbnailUrl) {
        this.localThumbnailUrl = localThumbnailUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebThumbnailUrl() {
        return webThumbnailUrl;
    }

    public void setWebThumbnailUrl(String webThumbnailUrl) {
        this.webThumbnailUrl = webThumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
