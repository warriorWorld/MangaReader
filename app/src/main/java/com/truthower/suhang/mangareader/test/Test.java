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
    private String[] spiderables = {"http://topwebcomics.com/", "https://nhentai.net/", "https://hitomi.la/"};
    private static org.jsoup.nodes.Document doc;

    public static void main(String[] args) {

        new Thread() {
            // 在java里并不一定需要开启线程 去请求网络,这里随意
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect("http://www.mangareader.net/one-piece")
                            .timeout(10000).get();
                    Element readmangasumElement = doc.getElementById("readmangasum");
                    Element descriptionElement = readmangasumElement.select("p").first();

                    System.out.println(descriptionElement.text());

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
