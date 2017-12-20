package com.truthower.suhang.mangareader.business.download;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.DownloadChapterBean;
import com.truthower.suhang.mangareader.bean.DownloadPageBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.spider.FileSpider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/18.
 */

public class DownloadIntentService extends IntentService {
    public static final String DOWNLOAD_URL = "download_url";
    public static final String MANGA_FOLDER_NAME = "manga_folder_name";
    public static final String CHILD_FOLDER_NAME = "child_folder_name";
    public static final String PAGE_NAME = "page_name";

    public DownloadIntentService(){
        super("DownloadIntentService");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownloadIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String img_url = intent.getStringExtra(DOWNLOAD_URL);
        String mangaFolderName = intent.getStringExtra(MANGA_FOLDER_NAME);
        String childFolderName = intent.getStringExtra(CHILD_FOLDER_NAME);
        String page_name = intent.getStringExtra(PAGE_NAME);
        Bitmap bp = downloadUrlBitmap(img_url);
        //把图片保存到本地
        FileSpider.getInstance().saveBitmap(bp, page_name, childFolderName, mangaFolderName);
        DownloadMangaManager.getInstance().downloadPageDone(this, img_url);
    }

    private Bitmap downloadUrlBitmap(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        Bitmap bitmap = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
