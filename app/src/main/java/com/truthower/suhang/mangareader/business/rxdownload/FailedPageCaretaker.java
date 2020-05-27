package com.truthower.suhang.mangareader.business.rxdownload;

import android.content.Context;

import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.bean.RxDownloadPageBean;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FailedPageCaretaker {
    private final static String KEY = "failed_page";

    public static void saveFailedPages(Context context, CopyOnWriteArrayList<RxDownloadPageBean> pages) {
        ShareObjUtil.saveObject(context, pages, KEY);
    }

    public static CopyOnWriteArrayList<RxDownloadPageBean> getFailedPages(Context context) {
        return (CopyOnWriteArrayList<RxDownloadPageBean>) ShareObjUtil.getObject(context, KEY);
    }

    public static void clean(Context context) {
        ShareObjUtil.deleteFile(context, KEY);
    }
}
