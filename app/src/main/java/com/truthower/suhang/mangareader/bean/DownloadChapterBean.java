package com.truthower.suhang.mangareader.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/18.
 */

public class DownloadChapterBean extends BaseBean {
    private String chapter_title;//normal:第几话;one shot:文件名
    private String chapter_child_folder_name;//子文件夹名
    private int chapter_size;//normal:本话页数
    private String chapter_url;//normal
    private String img_url;//one shot
    private ArrayList<DownloadPageBean> pages;//normal

    public String getChapter_title() {
        return chapter_title;
    }

    public void setChapter_title(String chapter_title) {
        this.chapter_title = chapter_title;
    }

    public String getChapter_child_folder_name() {
        return chapter_child_folder_name;
    }

    public void setChapter_child_folder_name(String chapter_child_folder_name) {
        this.chapter_child_folder_name = chapter_child_folder_name;
    }

    public int getChapter_size() {
        return chapter_size;
    }

    public void setChapter_size(int chapter_size) {
        this.chapter_size = chapter_size;
    }

    public String getChapter_url() {
        return chapter_url;
    }

    public void setChapter_url(String chapter_url) {
        this.chapter_url = chapter_url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public ArrayList<DownloadPageBean> getPages() {
        return pages;
    }

    public void setPages(ArrayList<DownloadPageBean> pages) {
        this.pages = pages;
    }
}
