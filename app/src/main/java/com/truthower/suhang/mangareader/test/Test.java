package com.truthower.suhang.mangareader.test;

import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.utils.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Test {
    private String[] spiderables = {"http://topwebcomics.com/"};
    private static org.jsoup.nodes.Document doc;

    public static void main(String[] args) {

        new Thread() {
            // 在java里并不一定需要开启线程 去请求网络,这里随意
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("https://hitomi.la/index-all-3.html")
                            .timeout(10000).get();
                    Elements mangaListElement = doc.select("div.manga");
                    MangaBean item;
                    ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();

                    for (int i = 0; i < mangaListElement.size(); i++) {
                        item = new MangaBean();
                        item.setWebThumbnailUrl(mangaListElement.get(i).select("div.dj-img1").first().getElementsByTag("img").last().attr("src"));
                        item.setName(mangaListElement.get(i).select("h1 a").text());
                        item.setUrl(mangaListElement.get(i).select("h1 a").attr("href"));

                        mangaList.add(item);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
