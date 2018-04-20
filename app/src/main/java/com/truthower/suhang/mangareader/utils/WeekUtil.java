package com.truthower.suhang.mangareader.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/12/29.
 */

public class WeekUtil {
    public final static int MAX_WEEK_DAY = 7;//一周7天

    public static HashMap<Integer, Integer> getDaysOfWeek(int firstOfWeek) {
        HashMap<Integer, Integer> daysOfWeek = new HashMap<Integer, Integer>();
        for (int i = 0; i < MAX_WEEK_DAY; i++) {
            if (firstOfWeek + i > MAX_WEEK_DAY) {
                daysOfWeek.put(firstOfWeek + i - MAX_WEEK_DAY, i);
            } else {
                daysOfWeek.put(firstOfWeek + i, i);
            }
        }
        return daysOfWeek;
    }

    public static int getTodayByPosition(int position, int firstOfWeek) {
        HashMap<Integer, Integer> daysOfWeek = getDaysOfWeek(firstOfWeek);
        int result = 0;
        for (int i = 1; i < MAX_WEEK_DAY + 1; i++) {
            if (daysOfWeek.get(i) == position) {
                result = i;
                break;
            }
        }
        return result;
    }

    public static int getLastDay(int today, int firstOfWeek) {
        HashMap<Integer, Integer> daysOfWeek = getDaysOfWeek(firstOfWeek);
        if (today == firstOfWeek) {
            //说明今天是本周第一天 所以前一天一定是本周最后一天
            return daysOfWeek.get(daysOfWeek.size() - 1);
        } else {
            return daysOfWeek.get(today - 1);
        }
    }

    public static int getNextDay(int today, int firstOfWeek) {
        HashMap<Integer, Integer> daysOfWeek = getDaysOfWeek(firstOfWeek);
        try {
            return daysOfWeek.get(today + 1);
        } catch (IndexOutOfBoundsException e) {
            //说明今天是本周最后一天
            return daysOfWeek.get(0);
        }
    }

    /**
     * 获取一周内两天在日历上的间隔天数
     *
     * @param start
     * @param end
     * @return
     */
    public static int getWeekDayGapInCalendar(int start, int end, int firstOfWeek) {
        HashMap<Integer, Integer> daysOfWeek = getDaysOfWeek(firstOfWeek);

        return Math.abs(daysOfWeek.get(start) - daysOfWeek.get(end));
    }

    public static Date getDateWithDateString(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getDateStringWithDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = "";
        dateString = dateFormat.format(date);
        return dateString;
    }

    public static String getDayStringWithDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        String dateString = "";
        dateString = dateFormat.format(date);
        return dateString;
    }
}
