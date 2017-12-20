package com.truthower.suhang.mangareader.bean;/**
 * Created by Administrator on 2016/11/2.
 */

import android.content.Context;

import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;

import java.util.ArrayList;


/**
 * 作者：苏航 on 2016/11/2 15:52
 * 邮箱：772192594@qq.com
 */
public class DownloadBean extends BaseBean {
    private String manga_title;
    private String thumbnailUrl;
    private ArrayList<DownloadChapterBean> chapters;

    private DownloadBean() {
    }

    private static volatile DownloadBean instance = null;

    public static DownloadBean getInstance() {
        if (instance == null) {
            //线程锁定
            synchronized (DownloadBean.class) {
                //双重锁定
                if (instance == null) {
                    instance = new DownloadBean();
                }
            }
        }
        return instance;
    }

    public void setDownloadInfo(Context context, DownloadBean downloadInfo) {
        instance = downloadInfo;
        saveDownloadInfo(context, downloadInfo);
    }

    //这个最好只有在application类里调用一次(即刚进入应用时调用一次),其他情况直接用单例就好,调用这个效率太低了
    public static DownloadBean getDownloadInfo(Context context) {
        DownloadBean res = (DownloadBean) ShareObjUtil.getObject(context, ShareKeys.DOWNLOAD_KEY);
        if (null != res) {
            return res;
        } else {
            return instance;
        }
    }

    private void saveDownloadInfo(Context context, DownloadBean downloadInfo) {
        ShareObjUtil.saveObject(context, downloadInfo, ShareKeys.DOWNLOAD_KEY);
    }

    public void clean(Context context) {
        instance = null;
        ShareObjUtil.deleteFile(context, ShareKeys.DOWNLOAD_KEY);
    }

    public String getManga_title() {
        return manga_title;
    }

    public void setManga_title(Context context, String manga_title) {
        this.manga_title = manga_title;
        saveDownloadInfo(context, instance);
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(Context context, String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        saveDownloadInfo(context, instance);
    }

    public ArrayList<DownloadChapterBean> getChapters() {
        return chapters;
    }

    public void setChapters(Context context, ArrayList<DownloadChapterBean> chapters) {
        this.chapters = chapters;
        saveDownloadInfo(context, instance);
    }
}
