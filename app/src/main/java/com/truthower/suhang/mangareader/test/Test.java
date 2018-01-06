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
            doc = Jsoup.connect("http://www.mangareader.net/popular")
                    .timeout(10000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null != doc) {
            Elements test = doc.select("div.mangaresultitem h3 a");
            Elements test1 = doc.select("div.imgsearchresults");
            int count = test.size();
            String title, name;
            String path;
            MangaBean item;
            ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
            mangaList.clear();
            for (int i = 0; i < count; i++) {
                title = test.get(i).attr("href");
                if (!TextUtils.isEmpty(title) && !title.equals("null")) {
                    item = new MangaBean();
                    title = StringUtil.cutString(title, 1, title.length());
                    path = test1.get(i).attr("style");
                    path = StringUtil.cutString(path, 22,
                            path.length() - 2);
                    item.setWebThumbnailUrl(path);
                    item.setUrl(title);
                    name = test.get(i).text();
                    item.setName(title);
                    mangaList.add(item);
                    System.out.println(name);
                }
            }
            MangaListBean mangaListBean = new MangaListBean();
            mangaListBean.setMangaList(mangaList);
        }
    }
}
