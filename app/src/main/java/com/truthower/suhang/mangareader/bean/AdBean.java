package com.truthower.suhang.mangareader.bean;

import com.avos.avoscloud.AVFile;

/**
 * Created by Administrator on 2016/4/3.
 */
public class AdBean extends BaseBean{
    private String title;
    private String subtitle;
    private String message;
    private AVFile adFile;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AVFile getAdFile() {
        return adFile;
    }

    public void setAdFile(AVFile adFile) {
        this.adFile = adFile;
    }
}
