package com.truthower.suhang.mangareader.utils;/**
 * Created by Administrator on 2017/3/20.
 */

import android.app.Activity;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * 作者：苏航 on 2017/3/20 11:14
 * 邮箱：772192594@qq.com
 */
public class ActivityPoor {
    /**
     * 1.ArrayList是实现了基于动态数组的数据结构，LinkedList基于链表的数据结构。
     * 2.对于随机访问get和set，ArrayList觉得优于LinkedList，因为LinkedList要移动指针。
     * 3.对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据。
     * 这一点要看实际情况的。若只对单条数据插入或删除，ArrayList的速度反而优于LinkedList。
     * 但若是批量随机的插入删除数据，LinkedList的速度大大优于ArrayList. 因为ArrayList每插入一条数据，
     * 要移动插入点及之后的所有数据。  这一点我做了实验。在分别有200000条“记录”的ArrayList和
     * LinkedList的首位插入20000条数据，LinkedList耗时约是ArrayList的20分之1。
     */
    public static List<Activity> activityList = new LinkedList<Activity>();

    public ActivityPoor() {

    }

    /**
     * 添加到Activity容器中
     */
    public static void addActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
    }

    /**
     * 便利所有Activigty并finish
     */
    public static void finishAllActivity() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }

    public static void finishAllActivityButThis(Class<?> cls) {
        for (Activity activity : activityList) {
            if (!activity.getClass().equals(cls)) {
                activity.finish();
            }
        }
    }

    public static String getRunningActivityName(Context context) {
        String contextString = context.toString();
        return contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
    }

    /**
     * 结束指定的Activity
     */
    public static void finishSingleActivity(Activity activity) {
        if (activity != null) {
            if (activityList.contains(activity)) {
                activityList.remove(activity);
            }
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity 在遍历一个列表的时候不能执行删除操作，所有我们先记住要删除的对象，遍历之后才去删除。
     */
    public static void finishSingleActivityByClass(Class<?> cls) {
        Activity tempActivity = null;
        for (Activity activity : activityList) {
            if (activity.getClass().equals(cls)) {
                tempActivity = activity;
            }
        }

        finishSingleActivity(tempActivity);
    }

    public static Class<?> getActivityClassByPosition(int position) {
        if (position < 0) {
            return null;
        }
        if (activityList.size() < position + 1) {
            return null;
        }
        return activityList.get(position).getClass();
    }

    public static Class<?> getLastActivityClass() {
        return getActivityClassByPosition(activityList.size() - 2);
    }
}
