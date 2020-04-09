package com.truthower.suhang.mangareader.business.rxdownload;

import android.content.Context;

import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;

public class DownloadCaretaker {
    private final static String KEY = "download_caretaker";

    public static void saveDownloadMemoto(Context context, RxDownloadBean rxDownloadBean) {
        ShareObjUtil.saveObject(context, rxDownloadBean, KEY);
    }

    public static RxDownloadBean getDownloadMemoto(Context context) {
        return (RxDownloadBean) ShareObjUtil.getObject(context, KEY);
    }

    public static void clean(Context context) {
        ShareObjUtil.deleteFile(context, KEY);
    }
}
