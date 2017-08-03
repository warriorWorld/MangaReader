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
                    doc = Jsoup.connect("http://mangakakalot.com/chapter/quan_zhi_gao_shou/chapter_38.2")
                            .timeout(10000).get();
                    Elements mangaPicsElements = doc.select("div.vung-doc").first().getElementsByTag("img");
                    for (int i = 0; i < mangaPicsElements.size(); i++) {
                        System.out.println(mangaPicsElements.get(i).attr("src"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
