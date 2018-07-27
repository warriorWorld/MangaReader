package com.truthower.suhang.mangareader.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 本地存储工具
 *
 * @author Yunlongx Luo
 */
public class SharedPreferencesUtils {

    /**
     * SharedPreferences
     */
    private static SharedPreferences mSharedPreferences;


    public static void setSharedPreferencesData(Context mContext, String mKey,
                                                String mValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "manga_reader", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putString(mKey, mValue).commit();
    }

    public static void setSharedPreferencesData(Context mContext, String mKey, int mValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "manga_reader", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putInt(mKey, mValue).commit();
    }

    public static void setSharedPreferencesData(Context mContext, String mKey, boolean mValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "manga_reader", Context.MODE_PRIVATE);
        }
        mSharedPreferences.edit().putBoolean(mKey, mValue).commit();
    }

    public static String getSharedPreferencesData(Context mContext, String mKey) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "manga_reader", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getString(mKey, "");
    }

    public static Integer getIntSharedPreferencesData(Context mContext, String mKey) {
        return getIntSharedPreferencesData(mContext, mKey, 0);
    }

    public static Integer getIntSharedPreferencesData(Context mContext, String mKey, int defaultValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "manga_reader", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getInt(mKey, defaultValue);
    }

    public static boolean getBooleanSharedPreferencesData(Context mContext, String mKey, boolean defaultValue) {
        if (null == mSharedPreferences) {
            mSharedPreferences = mContext.getSharedPreferences(
                    "manga_reader", Context.MODE_PRIVATE);
        }
        return mSharedPreferences.getBoolean(mKey, defaultValue);
    }
}
