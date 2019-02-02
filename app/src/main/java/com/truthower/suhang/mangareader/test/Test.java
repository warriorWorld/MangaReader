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

    public static void main(String[] args) {
        try {
            doc = Jsoup.connect("http://jandan.net/duan")
                    .timeout(10000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null != doc) {
            Element mangaListElements = doc.body().getElementById("wrapper").getElementById("body")
                    .getElementById("content").getElementById("comments");
            Elements eles=mangaListElements.getElementsByClass("commentlist").last().
                    getElementsByTag("li");
//            System.out.println(mangaListElements.text());
            for (Element ele : eles) {
                System.out.println(ele.select("div.text").text());
            }
//            System.out.println(mangaListElements.text());
        }
    }
}
