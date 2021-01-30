package com.truthower.suhang.mangareader.spider;

import android.content.Context;
import android.util.Log;

import com.truthower.suhang.mangareader.bean.RxDownloadPageBean;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.MangaDownloader;
import com.truthower.suhang.mangareader.utils.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NDownloader extends MangaDownloader implements Serializable {
    @Override
    public <ResultObj> void getMangaChapterPics(Context context, final String chapterUrl, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc = null;
                try {
                    doc = Jsoup.connect(chapterUrl + "1/")
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (null != doc) {
                    Element pageCountE = doc.select("span.num-pages").last();

                    int pageCount = Integer.valueOf(pageCountE.text());
                    ArrayList<String> pathList = new ArrayList<String>();
                    String url = doc.getElementById("image-container").
                            getElementsByTag("img").attr("src");
                    for (int i = 0; i < pageCount; i++) {
                        //这只能解决所有图片都是统一一个格式的情况下的问题
                        if (url.endsWith("1.jpg")) {
                            pathList.add(url.replace("1.jpg", (i + 1) + ".jpg"));
                        } else if (url.endsWith("1.png")) {
                            pathList.add(url.replace("1.png", (i + 1) + ".png"));
                        } else {
                            Log.e("NDownloader", "imgae type not support!!!");
                        }
                    }
                    jsoupCallBack.loadSucceed((ResultObj) pathList);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
    }

    @Override
    public List<RxDownloadPageBean> getFixedUrls(List<RxDownloadPageBean> list) {
        for (RxDownloadPageBean item : list) {
            if (item.getPageUrl().endsWith(".png")) {
                item.setPageUrl(item.getPageUrl().replaceAll(".png", ".jpg"));
            } else if (item.getPageUrl().endsWith(".jpg")) {
                item.setPageUrl(item.getPageUrl().replaceAll(".jpg", ".png"));
            }
        }
        return list;
    }
}
