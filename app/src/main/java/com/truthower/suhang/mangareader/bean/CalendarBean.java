package com.truthower.suhang.mangareader.bean;


import com.truthower.suhang.mangareader.adapter.CalendarAdapter;

/**
 * Created by Administrator on 2017/12/29.
 */

public class CalendarBean extends BaseBean{
    private CalendarAdapter.DateState dateState;
    private int dayOfMonth;//几号
    private int dayOfWeek;//星期几

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public CalendarAdapter.DateState getDateState() {
        return dateState;
    }

    public void setDateState(CalendarAdapter.DateState dateState) {
        this.dateState = dateState;
    }
}
