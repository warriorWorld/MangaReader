package com.truthower.suhang.mangareader.bean;/**
 * Created by Administrator on 2016/11/2.
 */

import android.content.Context;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;

import java.util.ArrayList;


/**
 * 作者：苏航 on 2016/11/2 15:52
 * 邮箱：772192594@qq.com
 */
public class DownloadBean extends BaseBean {
    private MangaBean currentManga;
    private ArrayList<DownloadChapterBean> download_chapters;
    private String web_site;
    private boolean one_shot;

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
    public DownloadBean getDownloadInfo(Context context) {
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

    public MangaBean getCurrentManga() {
        return currentManga;
    }

    public void setMangaBean(Context context, MangaBean mangaBean) {
        this.currentManga = mangaBean;
        saveDownloadInfo(context, instance);
    }

    public String getWebSite() {
        return web_site;
    }

    public void setWebSite(Context context, String webSite) {
        this.web_site = webSite;
        saveDownloadInfo(context, instance);
    }

    public boolean isOne_shot() {
        return one_shot;
    }

    public void setOne_shot(Context context, boolean one_shot) {
        this.one_shot = one_shot;
        saveDownloadInfo(context, instance);
    }

    public ArrayList<DownloadChapterBean> getDownload_chapters() {
        return download_chapters;
    }

    public void initDownloadChapters() {
        this.download_chapters = getDownloadChapters();
    }

    public void setDownload_chapters(Context context, ArrayList<DownloadChapterBean> down_chapters) {
        this.download_chapters = down_chapters;
        saveDownloadInfo(context, instance);
    }

    private ArrayList<DownloadChapterBean> getDownloadChapters() {
        try {
            ArrayList<DownloadChapterBean> list = new ArrayList<>();
            if (one_shot && null != currentManga.getChapters() && currentManga.getChapters().size() > 0
                    && !TextUtils.isEmpty(currentManga.getChapters().get(0).getImgUrl())) {
                String mangaName = initMangaFileName();
                for (int i = 0; i <= currentManga.getChapters().size(); i++) {
                    DownloadChapterBean item = new DownloadChapterBean();
                    item.setImg_url(currentManga.getChapters().get(i).getImgUrl());
                    item.setChapter_size(currentManga.getChapters().size());
                    item.setChapter_title(mangaName + "_" + 1
                            + "_" + i + ".png");
                    list.add(item);
                }
            } else {
                for (int i = 0; i < currentManga.getChapters().size(); i++) {
                    DownloadChapterBean item = new DownloadChapterBean();
                    item.setChapter_url(currentManga.getChapters().get(i).getChapterUrl());
                    item.setChapter_child_folder_name(FileSpider.getInstance().getChildFolderName(
                            Integer.valueOf(currentManga.getChapters().get(i).getChapterPosition()), 3));
                    item.setChapter_size(currentManga.getChapters().size());
                    item.setChapter_title(currentManga.getChapters().get(i).getChapterPosition());
                    list.add(item);
                }
            }
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    public String initMangaFileName() {
        String mangaFileName = currentManga.getName();
        mangaFileName = getWordAgain(mangaFileName);
        if (mangaFileName.length() > 32) {
            mangaFileName = mangaFileName.substring(0, 32);
        }
        return mangaFileName;
    }

    private String getWordAgain(String s) {
        String str = s.replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&;*（）——+|{}【】\"‘；：”“’。，、？|]", "");
        //留着空格
//        str = str.replaceAll("\n", "");
//        str = str.replaceAll("\r", "");
//        str = str.replaceAll("\\s", "");
        str = str.replaceAll("_", "");
        return str;
    }
}
