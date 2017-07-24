package com.truthower.suhang.mangareader.config;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.truthower.suhang.mangareader.R;

/**
 * Created by Administrator on 2017/7/19.
 */

public class Configure {
    public static String versionName = "";
    public static String versionCode = "";
    public static String[] websList = {"MangaReader"};
    public static String DST_FOLDER_NAME = "aSpider";
    public static String currentMangaName = "";
    public static String currentWebSite = websList[0];
    public static String YOUDAO = "http://fanyi.youdao.com/openapi.do?keyfrom=mangaeasywa" +
            "tch&key=986400551&type=data&doctype=json&version=1.1&q=";
    public static DisplayImageOptions normalImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .showImageOnLoading(R.drawable.empty_list)
            .showImageOnFail(R.drawable.empty_list)
            .build();
}
