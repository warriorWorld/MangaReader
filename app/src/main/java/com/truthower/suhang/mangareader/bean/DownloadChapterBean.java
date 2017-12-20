package com.truthower.suhang.mangareader.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/18.
 */

public class DownloadChapterBean extends BaseBean {
    private String chapter_title;
    private String chapter_file_path;
    private int chapter_size;
    private ArrayList<DownloadPageBean> pages;

    public String getChapter_title() {
        return chapter_title;
    }

    public void setChapter_title(String chapter_title) {
        this.chapter_title = chapter_title;
    }

    public ArrayList<DownloadPageBean> getPages() {
        return pages;
    }

    public void setPages(ArrayList<DownloadPageBean> pages) {
        this.pages = pages;
    }

    public String getChapter_file_path() {
        return chapter_file_path;
    }

    public void setChapter_file_path(String chapter_file_path) {
        this.chapter_file_path = chapter_file_path;
    }

    public int getChapter_size() {
        return chapter_size;
    }

    public void setChapter_size(int chapter_size) {
        this.chapter_size = chapter_size;
    }
}
