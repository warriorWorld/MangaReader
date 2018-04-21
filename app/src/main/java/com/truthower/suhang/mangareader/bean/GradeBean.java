package com.truthower.suhang.mangareader.bean;

/**
 * Created by Administrator on 2018/4/21.
 */

public class GradeBean extends BaseBean {
    private String mangaName;
    private String mangaUrl;
    private int star;
    private String owner;

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

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
