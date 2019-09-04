package com.truthower.suhang.mangareader.test;

import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.MatchStringUtil;
import com.truthower.suhang.mangareader.utils.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Test {
    private String[] spiderables = {"http://topwebcomics.com/", "http://mangakakalot.com/"};
    private static org.jsoup.nodes.Document doc;
    private static int num = 0;
    private static int addTime = 0;
    private static int totalAddTime = 0;

    public static void main(String[] args) {
//        try {
//            doc = Jsoup.connect("https://www.mangareader.net/one-piece/950")
//                    .timeout(10000).get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (null != doc) {
////            Element mangaPicDetailElement = doc.getElementsByClass("episode-table").first();
//            Element element = doc.select("div.imgholder").first();
//            String url = element.getElementsByTag("img").last().attr("src");
//            System.out.println(url);
//        }
        totalAddTime = 100;
        addTo();
    }

    private static void addTo() {
        if (addTime < totalAddTime) {
            num = addTime + addTime + 1;
            addTime++;
            addTo();
        }
    }
}
