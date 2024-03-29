package com.truthower.suhang.mangareader.business.threadpooldownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.bean.RxDownloadPageBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.OnResultListener;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PageDownloadRunner implements Runnable {
    private RxDownloadPageBean mPageBean;
    private OnResultListener mOnResultListener;

    public PageDownloadRunner(RxDownloadPageBean pageBean, OnResultListener onResultListener) {
        mPageBean = pageBean;
        mOnResultListener = onResultListener;
    }

    @Override
    public void run() {
        Bitmap bp = ImageLoader.getInstance().loadImageSync(mPageBean.getPageUrl(), Configure.smallImageOptions);
        //把图片保存到本地
        boolean isSuccess;
        if (null != bp) {
            isSuccess = FileSpider.getInstance().saveBitmap(bp, mPageBean.getPageName(), mPageBean.getChapterName(), mPageBean.getMangaName());
        } else {
            isSuccess = false;
        }
        mPageBean.setDownloaded(true);
        Logger.d("one page downloaded; chapter: " + mPageBean.getChapterName() + " page: " + mPageBean.getPageName());
        if (null != mOnResultListener) {
            if (isSuccess) {
                mOnResultListener.onFinish();
            } else {
                mOnResultListener.onFailed();
            }
        }
    }

    private Bitmap downloadUrlBitmap(String urlString, OnResultListener listener) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        Bitmap bitmap = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap = BitmapFactory.decodeStream(in);
            if (null != listener) {
                listener.onFinish();
            }
        } catch (final IOException e) {
            e.printStackTrace();
            if (null != listener) {
                listener.onFailed();
            }
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

    public void setOnResultListener(OnResultListener onResultListener) {
        mOnResultListener = onResultListener;
    }
}
