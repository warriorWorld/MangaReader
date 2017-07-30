package com.truthower.suhang.mangareader.test;

import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.utils.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Test {
    private String[] spiderables = {"http://topwebcomics.com/", "https://nhentai.net/", "https://hitomi.la/"};
    private static org.jsoup.nodes.Document doc;

    public static void main(String[] args) {

        new Thread() {
            // 在java里并不一定需要开启线程 去请求网络,这里随意
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("https://nhentai.net/?page=2")
                            .timeout(10000).get();
                    Elements test1 = doc.select("div.index-container");
                    Elements test2 = test1.get(0).getElementsByClass("gallery");
                    Elements hrefElements = test2.select("a");
                    int count = test2.size();
                    String title;
                    String path;
                    String url = "";
                    MangaBean item;
                    ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
                    mangaList.clear();
                    for (int i = 0; i < count; i++) {
                        Elements titleElements = test2.get(i).getElementsByClass("caption");
                        title = titleElements.get(0).text();
                        path = hrefElements.get(i).getElementsByTag("img").last().attr("src");
                        item = new MangaBean();
                        url = hrefElements.get(i).attr("href");

                        item.setWebThumbnailUrl(path);
                        item.setUrl(url);
                        item.setName(title);
                        mangaList.add(item);
                        System.out.println(path);
                    }

//                    System.out.println(doc.toString() + test2.size());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
