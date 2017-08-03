package com.truthower.suhang.mangareader.test;

import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Test {
    private String[] spiderables = {"http://topwebcomics.com/", "http://mangakakalot.com/"};
    private static org.jsoup.nodes.Document doc;

    public static void main(String[] args) {

        new Thread() {
            // 在java里并不一定需要开启线程 去请求网络,这里随意
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("http://mangakakalot.com/manga/quan_zhi_gao_shou")
                            .timeout(10000).get();
                    Elements mangaDetailElements = doc.select("div.manga-info-top");
                    Elements mangaPicDetailElements = doc.select("div.manga-info-pic");
                    Elements mangaTextDetailElements = doc.select("ul.manga-info-text li");
                    MangaBean item = new MangaBean();
                    item.setWebThumbnailUrl(mangaPicDetailElements.first().getElementsByTag("img").last().attr("src"));
                    item.setName(mangaPicDetailElements.first().getElementsByTag("img").last().attr("alt"));
                    for (int i=0;i<mangaTextDetailElements.size();i++){
                        System.out.println(mangaTextDetailElements.text());
                    }
                    System.out.println();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
