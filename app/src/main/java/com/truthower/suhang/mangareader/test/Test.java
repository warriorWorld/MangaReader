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
                    doc = Jsoup.connect("https://hitomi.la/galleries/1096838.html")
                            .timeout(10000).get();
                    Element mangaThumbnailElement = doc.select("div.cover a").first().getElementsByTag("img").last();
                    Elements mangaTagElements = doc.select("ul.tags a");

                    MangaBean mangaBean = new MangaBean();

                    mangaBean.setName("Can't found");
                    mangaBean.setWebThumbnailUrl(mangaThumbnailElement.attr("src"));

                    //Tag
                    String[] types = new String[mangaTagElements.size()];
                    String[] typeCodes = new String[mangaTagElements.size()];
                    for (int i = 0; i < mangaTagElements.size(); i++) {
                        types[i] = mangaTagElements.get(i).text();
                        String tagCode = mangaTagElements.get(i).attr("href");
                        tagCode = tagCode.replaceAll("/tag/", "");
                        tagCode = tagCode.replaceAll("-all-1.html", "");
                        typeCodes[i] = tagCode;
                    }
                    //pics
                    Elements mangaPicElements = doc.select("div.thumbnail-container a");
                    Elements mangaInfoElement = doc.getElementsByClass("gallery-info");

                    //.select("div.thumbnail-container a")
                    ArrayList<ChapterBean> chapters = new ArrayList<ChapterBean>();
                    ChapterBean chapterBean;
                    String mangaId = "https://hitomi.la/galleries/1096838.html";
                    mangaId = mangaId.replaceAll("https://hitomi\\.la/galleries/", "");
                    mangaId = mangaId.replaceAll("\\.html", "");
                    System.out.println(mangaId);
                    for (int i = 0; i < mangaPicElements.size(); i++) {
                        chapterBean = new ChapterBean();
                        System.out.println(mangaPicElements.text());
                        if (mangaInfoElement.text().contains("doujinshi")) {
                            chapterBean.setChapterThumbnailUrl("https://atn.hitomi.la/smalltn/" + mangaId + "/" + i + ".jpg.jpg");
                            chapterBean.setImgUrl("https://aa.hitomi.la/galleries/" + mangaId + "/" + i + ".jpg");
                        } else {
                            String position = "";
                            if (i < 10) {
                                position = "00" + i;
                            } else if (i < 100) {
                                position = "0" + i;
                            } else if (i < 1000) {
                                position = "" + i;
                            }
                            chapterBean.setChapterThumbnailUrl("https://btn.hitomi.la/smalltn/" + mangaId + "/" + position + ".jpg.jpg");
                            chapterBean.setImgUrl("https://ba.hitomi.la/galleries/" + mangaId + "/" + position + ".jpg");
                        }
                        System.out.println(chapterBean.getChapterThumbnailUrl()+"\n"+chapterBean.getImgUrl());
                        chapters.add(chapterBean);
                    }

                    System.out.println();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
