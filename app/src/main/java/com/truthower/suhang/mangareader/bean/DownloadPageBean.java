package com.truthower.suhang.mangareader.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/18.
 */

public class DownloadPageBean extends BaseBean {
    private String page_url;
    private String page_file_name;

    public String getPage_url() {
        return page_url;
    }

    public void setPage_url(String page_url) {
        this.page_url = page_url;
    }

    public String getPage_file_name() {
        return page_file_name;
    }

    public void setPage_file_name(String page_file_name) {
        this.page_file_name = page_file_name;
    }
}
