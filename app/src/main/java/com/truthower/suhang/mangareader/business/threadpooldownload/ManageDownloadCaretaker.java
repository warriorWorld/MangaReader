package com.truthower.suhang.mangareader.business.threadpooldownload;

import android.content.Context;

import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;

public class ManageDownloadCaretaker {
    private final static String KEY = "manage_download_caretaker";

    public static void saveContentMemoto(Context context, String content) {
        ShareObjUtil.saveObject(context, content, KEY);
    }

    public static String getContentMemoto(Context context) {
        return (String) ShareObjUtil.getObject(context, KEY);
    }

    public static void clean(Context context) {
        ShareObjUtil.deleteFile(context, KEY);
    }
}
