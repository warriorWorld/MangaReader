package com.truthower.suhang.mangareader.bean;

import java.util.Date;

/**
 * Created by Administrator on 2018/4/21.
 */

public class CommentBean extends LeanBaseBean {
    private String mangaName;
    private String mangaUrl;
    private int oo_number;
    private int xx_number;
    private String reply_user;
    private String comment_content;
    private String owner;
    private Date create_at;
    private boolean hot;

    public int getOo_number() {
        return oo_number;
    }

    public void setOo_number(int oo_number) {
        this.oo_number = oo_number;
    }

    public int getXx_number() {
        return xx_number;
    }

    public void setXx_number(int xx_number) {
        this.xx_number = xx_number;
    }

    public String getReply_user() {
        return reply_user;
    }

    public void setReply_user(String reply_user) {
        this.reply_user = reply_user;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public boolean isHot() {
        return hot;
    }

    public void setHot(boolean hot) {
        this.hot = hot;
    }
}
