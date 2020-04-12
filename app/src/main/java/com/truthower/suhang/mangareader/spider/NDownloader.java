package com.truthower.suhang.mangareader.spider;

import android.content.Context;

import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.MangaDownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class NDownloader implements MangaDownloader, Serializable {
    //"https://nhentai.net/g/308419/1/"
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
                        pathList.add(url.replace("1.jpg",(i+1)+".jpg"));
                    }
                    jsoupCallBack.loadSucceed((ResultObj) pathList);
                }else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
    }
}
