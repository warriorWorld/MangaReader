package com.truthower.suhang.mangareader.config;

import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.truthower.suhang.mangareader.R;

import java.io.File;

/**
 * Created by Administrator on 2017/7/19.
 */

public class Configure {
    public final static boolean isTest = false;
    public final static String[] websList = {"MangaReader", "KaKaLot"};
    public final static String[] masterWebsList = {"MangaReader", "NManga", "KaKaLot", "LManga"};
    public final static String DST_FOLDER_NAME = "aSpider";
    final public static String storagePath = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/" + DST_FOLDER_NAME;
    final public static String WRONG_WEBSITE_EXCEPTION = "wrong_website_exception";
    //仅用于显示当前的漫画名称
//    public static String currentMangaName = "";
    public static String currentWebSite = websList[0];
    //获取正在运行的服务 有的手机获取不到 所以换一种方式
    public static boolean isDownloadServiceRunning = false;
    final public static String YOUDAO = "http://fanyi.youdao.com/openapi.do?keyfrom=mangaeasywa" +
            "tch&key=986400551&type=data&doctype=json&version=1.1&q=";
    final public static DisplayImageOptions normalImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .showImageOnLoading(R.drawable.empty_list)
            .showImageOnFail(R.drawable.empty_list)
            .build();
    final public static DisplayImageOptions smallImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .showImageOnLoading(R.drawable.loading)
            .showImageOnFail(R.drawable.load_failed)
            .build();
    // 3DES加密key
    final public static String key = "iq2szojof6x1ckgejwe52urw";
    //数字随便写的  权限request code
    final public static int PERMISSION_CAMERA_REQUST_CODE = 8021;
    final public static int PERMISSION_LOCATION_REQUST_CODE = 8022;
    final public static int PERMISSION_FILE_REQUST_CODE = 8023;
    final public static int PERMISSION_READ_PHONE_STATE_REQUST_CODE = 8024;
}
