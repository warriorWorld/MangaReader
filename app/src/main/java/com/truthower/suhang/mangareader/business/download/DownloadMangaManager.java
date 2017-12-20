
package com.truthower.suhang.mangareader.business.download;

import android.content.Context;
import android.content.Intent;

import com.truthower.suhang.mangareader.bean.DownloadBean;
import com.truthower.suhang.mangareader.bean.DownloadChapterBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;

/**
 * userInfo管理类
 * <p>
 *
 * @author Administrator
 */
public class DownloadMangaManager {
    private DownloadChapterBean currentChapter;

    private DownloadMangaManager() {
    }

    private static volatile DownloadMangaManager instance = null;

    public static DownloadMangaManager getInstance() {
        if (instance == null) {
            synchronized (DownloadMangaManager.class) {
                if (instance == null) {
                    instance = new DownloadMangaManager();
                }
            }
        }
        return instance;
    }

    public void doDownload(final Context context) {
        stopDownload(context);
        if (null == currentChapter || currentChapter.getPages().size() <= 0) {
            currentChapter = DownloadBean.getInstance().getChapters().get(0);
            saveCurrentChapter(context);
            DownloadBean.getInstance().getChapters().remove(0);
        }
        Intent intent = new Intent(context, DownloadIntentService.class);
        for (int i = 0; i < currentChapter.getPages().size(); i++) {//循环启动任务
            intent.putExtra(DownloadIntentService.DOWNLOAD_URL, currentChapter.getPages().get(i));
            intent.putExtra(DownloadIntentService.MANGA_FOLDER_NAME, DownloadBean.getInstance().getManga_title());
            intent.putExtra(DownloadIntentService.CHILD_FOLDER_NAME, currentChapter.getChapter_file_path());
            intent.putExtra(DownloadIntentService.PAGE_NAME, currentChapter.getPages().get(i).getPage_file_name());
            context.startService(intent);
        }
    }

    public void downloadPageDone(Context context, String url) {
        if (currentChapter.getPages().size() == 1 && currentChapter.getPages().get(0).equals(url)) {
            currentChapter = null;
            doDownload(context);
            return;
        }
        for (int i = 0; i < currentChapter.getPages().size(); i++) {
            if (url.equals(currentChapter.getPages().get(i).getPage_url())) {
                currentChapter.getPages().remove(i);
                saveCurrentChapter(context);
                return;
            }
        }
    }

    private void stopDownload(Context context) {
        Intent stopIntent = new Intent(context, DownloadIntentService.class);
        context.stopService(stopIntent);
    }

    public void reset(Context context) {
        DownloadBean.getInstance().clean(context);
        currentChapter = null;
        ShareObjUtil.deleteFile(context, ShareKeys.CURRENT_CHAPTER_KEY);
    }

    //这个最好只有在application类里调用一次(即刚进入应用时调用一次),其他情况直接用单例就好,调用这个效率太低了
    public DownloadChapterBean getCurrentChapter(Context context) {
        if (null!=currentChapter){
            return currentChapter;
        }
        currentChapter = (DownloadChapterBean) ShareObjUtil.getObject(context, ShareKeys.CURRENT_CHAPTER_KEY);
        return currentChapter;
    }

    public void saveCurrentChapter(Context context) {
        ShareObjUtil.saveObject(context, currentChapter, ShareKeys.CURRENT_CHAPTER_KEY);
    }
}
