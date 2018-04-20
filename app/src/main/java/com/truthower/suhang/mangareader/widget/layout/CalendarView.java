package com.truthower.suhang.mangareader.widget.layout;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;


import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.CalendarAdapter;
import com.truthower.suhang.mangareader.bean.CalendarBean;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.utils.WeekUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarView extends LinearLayout {
    private Context context;
    private Calendar calendar;
    private RecyclerView calendarRcv;
    private CalendarAdapter calendarAdapter;
    private int firstDayOfWeek = 7;
    private int currentMonth;
    private int currentYear;
    private ArrayList<CalendarBean> dateList = new ArrayList<>();
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private boolean hideOtherMonthDay = false;
    private int maxDay;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this);
        calendarRcv = (RecyclerView) findViewById(R.id.calendar_rcv);
        calendarRcv.setLayoutManager(new GridLayoutManager(context, 7) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        calendarRcv.setFocusableInTouchMode(false);
        calendarRcv.setFocusable(false);
        calendarRcv.setHasFixedSize(true);
    }

    private void getData() {
        dateList.clear();

        calendar = Calendar.getInstance(Locale.CHINA); //获取China区Calendar实例，实际是GregorianCalendar的一个实例
        calendar.clear();
        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.MONTH, currentMonth - 1); //初始化日期
        maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  //获得当前日期所在月份有多少天（或者说day的最大值)，用于后面的计算

        calendar.set(Calendar.DAY_OF_MONTH, 1);  //将日期调到当前月份的第一天
        //这个DAY_OF_WEEK代表的是这个星期的第几天 而各个时区的第一天是不一样的 中国时区的第一天是周日 这个脚标是从1开始的..
        int startDayOfWeek = WeekUtil.getTodayByPosition(calendar.get(Calendar.DAY_OF_WEEK) - 1, firstDayOfWeek);
        calendar.set(Calendar.DAY_OF_MONTH, maxDay); //将日期调到当前月份的最后一天
        int endDayOfWeek = WeekUtil.getTodayByPosition(calendar.get(Calendar.DAY_OF_WEEK) - 1, firstDayOfWeek); //获得当前日期所在月份的最后一天是星期几

        /**
         * 计算上一个月在本月日历页出现的那几天.
         * 比如，startDayOfWeek = 3，表示当月第一天是星期二，所以日历向前会空出2天的位置，那么让上月的最后两天显示在星期日和星期一的位置上.
         */
        calendar.add(Calendar.MONTH, -1); //set方法调用后calendar并不会立刻重新计算 add方法就是让他set后直接计算的方式
        int preMonthEndDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int lastMonthDayNum;//在当前月上显示的上个月的天的数量
        int nextMonthDayNum;//在当前月上显示的下个月的天的数量

        lastMonthDayNum = WeekUtil.getWeekDayGapInCalendar(firstDayOfWeek, startDayOfWeek, firstDayOfWeek);
        nextMonthDayNum = WeekUtil.getWeekDayGapInCalendar(WeekUtil.getLastDay(firstDayOfWeek, firstDayOfWeek),
                endDayOfWeek, firstDayOfWeek);

        //添加上月的日期数据
        for (int i = 0; i < lastMonthDayNum; i++) {
            CalendarBean item = new CalendarBean();
            item.setDateState(CalendarAdapter.DateState.OTHER_MONTH);
            item.setDayOfMonth(preMonthEndDayOfMonth - (lastMonthDayNum - i) + 1);
            dateList.add(item);
        }
        //添加本月的日期数据
        for (int i = 0; i < maxDay; i++) {
            CalendarBean item = new CalendarBean();
            item.setDateState(CalendarAdapter.DateState.NORMAL);
            item.setDayOfMonth(i + 1);
            dateList.add(item);
        }
        //添加下月的日期数据
        for (int i = 0; i < nextMonthDayNum; i++) {
            CalendarBean item = new CalendarBean();
            item.setDateState(CalendarAdapter.DateState.OTHER_MONTH);
            item.setDayOfMonth(i + 1);
            dateList.add(item);
        }

        initDateRv();
    }

    private void initDateRv() {
        try {
            if (null == calendarAdapter) {
                calendarAdapter = new CalendarAdapter(context, dateList);
                calendarAdapter.setOnRecycleItemClickListener(onRecycleItemClickListener);
                calendarRcv.setAdapter(calendarAdapter);
            } else {
                calendarAdapter.setDateList(dateList);
                calendarAdapter.notifyDataSetChanged();
            }
            calendarAdapter.setHideOtherMonthDay(hideOtherMonthDay);
        } catch (Exception e) {

        }
    }

    public void setSelected(int[] selectes) {
        for (int i = 0; i < dateList.size(); i++) {
            switch (dateList.get(i).getDateState()) {
                case NORMAL:
                case SELECTED:
                case HALF_SELECTED:
                    for (int j = 0; j < selectes.length; j++) {
                        if (selectes[j] == dateList.get(i).getDayOfMonth()) {
                            dateList.get(i).setDateState(CalendarAdapter.DateState.SELECTED);
                            break;
                        } else {
                            dateList.get(i).setDateState(CalendarAdapter.DateState.NORMAL);
                        }
                    }
                    break;
            }
        }
        initDateRv();
    }

    public int getMaxDay() {
        return maxDay;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public int getCurrentMonth() {
        return currentMonth;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public int getDayOfMonthByPosition(int position) {
        try {
            return dateList.get(position).getDayOfMonth();
        } catch (Exception e) {
            return 0;
        }
    }

    public void setCurrentMonth(int year, int currentMonth) {
        this.currentMonth = currentMonth;
        this.currentYear = year;
        getData();
    }

    public void resetSelectedPosition() {
        calendarAdapter.resetSelectedPosition();
    }

    public void setHideOtherMonthDays(boolean hide) {
        this.hideOtherMonthDay = hide;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }
}
