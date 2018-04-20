package com.truthower.suhang.mangareader.bean;

import java.util.Date;

/**
 * Created by Administrator on 2016-06-16.
 */
public class StatisticsBean extends BaseBean {
    private String manga_name;
    private int query_word_c;
    private int read_page;
    private Date create_at;
    private String dateStart;
    private String dateEnd;

    private float query_word_r;//查词率
    private int qurey_word_c_total;
    private int read_page_total;
    private float query_word_r_total;


    public float getQuery_word_r() {
        return query_word_r;
    }

    public void setQuery_word_r(float query_word_r) {
        this.query_word_r = query_word_r;
    }


    public int getQuery_word_c() {
        return query_word_c;
    }

    public void setQuery_word_c(int query_word_c) {
        this.query_word_c = query_word_c;
    }

    public Date getCreate_at() {
        return create_at;
    }

    public void setCreate_at(Date create_at) {
        this.create_at = create_at;
    }

    public int getRead_page() {
        return read_page;
    }

    public void setRead_page(int read_page) {
        this.read_page = read_page;
    }

    public String getManga_name() {
        return manga_name;
    }

    public void setManga_name(String manga_name) {
        this.manga_name = manga_name;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public int getQurey_word_c_total() {
        return qurey_word_c_total;
    }

    public void setQurey_word_c_total(int qurey_word_c_total) {
        this.qurey_word_c_total = qurey_word_c_total;
    }

    public int getRead_page_total() {
        return read_page_total;
    }

    public void setRead_page_total(int read_page_total) {
        this.read_page_total = read_page_total;
    }

    public float getQuery_word_r_total() {
        return query_word_r_total;
    }

    public void setQuery_word_r_total(float query_word_r_total) {
        this.query_word_r_total = query_word_r_total;
    }
}
