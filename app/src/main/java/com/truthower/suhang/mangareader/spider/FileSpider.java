package com.truthower.suhang.mangareader.spider;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.utils.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * http://www.mangareader.net/
 */
public class FileSpider extends SpiderBase {
    private String webUrl = "file://";
    private static final String DST_FOLDER_NAME = "a_spider";

    private FileSpider() {
    }

    private static volatile FileSpider instance = null;

    public static FileSpider getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (FileSpider.class) {
                //双重锁定
                if (instance == null) {
                    instance = new FileSpider();
                }
            }
        }
        return instance;
    }

    @Override
    public <ResultObj> void getMangaList(final String type, final String page, final JsoupCallBack<ResultObj> jsoupCallBack) {
        File parentPath = Environment
                .getExternalStorageDirectory();
        String storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME;
        File f = new File(storagePath);//第一级目录 reptile
        if (!f.exists()) {
            f.mkdirs();
        }
        int firstFileLength = f.toString().length() + 1;
        File[] files = f.listFiles();//第二级目录 具体漫画们
        if (null != files && files.length > 0) {
            ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
            for (int i = 0; i < files.length; i++) {
                Bitmap bp = null;
                MangaBean item = new MangaBean();

                String title = files[i].toString();
                title = title.substring(firstFileLength, title.length());

                item.setName(title);
                item.setUrl(files[i].toString());

                File[] files1 = files[i].listFiles();//第三级目录 某具体漫画内部
                if (files1.length > 0 && !files1[0].isDirectory()) {
                    //如果某漫画文件夹第一次目录就直接是图片文件 则显示第一张图片
                    item.setLocalThumbnailUrl(files1[0].toString());

                } else if (files1.length > 0 && files1[0].isDirectory()) {
                    //二级文件夹
                    File[] files2 = files1[0].listFiles();//第四级目录 某具体漫画内部的内部
                    item.setLocalThumbnailUrl(files2[0].toString());
                } else {
                    //空文件夹
                }
                mangaList.add(item);
            }
            MangaListBean mangaListBean = new MangaListBean();
            mangaListBean.setMangaList(mangaList);
            jsoupCallBack.loadSucceed((ResultObj) mangaListBean);
        } else {
            jsoupCallBack.loadFailed("load local files failed");
        }
    }

    @Override
    public <ResultObj> void getMangaDetail(final String mangaURL, final JsoupCallBack<ResultObj> jsoupCallBack) {
    }


    @Override
    public boolean isOneShot() {
        return false;
    }

    @Override
    public String[] getMangaTypes() {
        String[] mangaTypeCodes = {"all", "action", "adventure", "comedy", "demons", "drama", "ecchi", "fantasy", "gender-bender",
                "harem", "historical", "horror", "josei", "magic", "martial-arts", "mature", "mecha", "military", "mystery", "one-shot",
                "psychological", "romance", "school-life", "sci-fi", "seinen", "shoujo", "shoujoai", "shounen", "shounenai", "slice-of-life", "smut", "sports",
                "super-power", "supernatural", "tragedy", "vampire", "yaoi", "yuri"};
        return mangaTypeCodes;
    }

    @Override
    public ArrayList<ChapterBean> getMangaChapterPics(String mangaName, String chapter, int picCount, JsoupCallBack jsoupCallBack) {
        return null;
    }

    @Override
    public int nextPageNeedAddCount() {
        return 30;
    }

    @Override
    public String getWebUrl() {
        return webUrl;
    }

    public static void deleteFile(String file) {
        File f = new File(file);
        deleteFile(f);
    }

    /**
     * 递归删除文件和文件夹 因为file.delete();只能删除空文件夹或文件 所以需要这么递归循环删除
     *
     * @param file 要删除的根目�?
     */
    public static void deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }
}
