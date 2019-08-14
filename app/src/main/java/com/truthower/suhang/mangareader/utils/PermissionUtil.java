package com.truthower.suhang.mangareader.utils;

import android.content.Context;

import com.truthower.suhang.mangareader.config.ShareKeys;

public class PermissionUtil {
    public static boolean isMaster(Context context) {
        return SharedPreferencesUtils.getBooleanSharedPreferencesData(context, ShareKeys.IS_MASTER, false);
    }

    public static boolean isCreator(Context context) {
        return SharedPreferencesUtils.getBooleanSharedPreferencesData(context, ShareKeys.IS_CREATOR, false);
    }
}
