package com.truthower.suhang.mangareader.bean;

/**
 * Created by Administrator on 2017/7/18.
 */

public class ChapterBean extends BaseBean {
    private String chapterUrl;//章节的地址
    private String chapterPosition;
    private String chapterThumbnailUrl;//one shot only
    private String imgUrl;//有的one shot可以一次性获取缩略图和图片 这就是图片的地址

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public String getChapterPosition() {
        return chapterPosition;
    }

    public void setChapterPosition(String chapterPosition) {
        this.chapterPosition = chapterPosition;
    }

    public String getChapterThumbnailUrl() {
        return chapterThumbnailUrl;
    }

    public void setChapterThumbnailUrl(String chapterThumbnailUrl) {
        this.chapterThumbnailUrl = chapterThumbnailUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
