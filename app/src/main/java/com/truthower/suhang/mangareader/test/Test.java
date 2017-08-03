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
                    Elements mangaTagDetailElements = doc.select("ul.manga-info-text li").get(6).select("a");

                    MangaBean item = new MangaBean();
                    item.setWebThumbnailUrl(mangaPicDetailElements.first().getElementsByTag("img").last().attr("src"));
                    item.setName(mangaPicDetailElements.first().getElementsByTag("img").last().attr("alt"));
                    item.setAuthor(mangaTextDetailElements.get(1).text());
                    item.setLast_update(mangaTextDetailElements.get(3).text());
                    System.out.println(mangaTextDetailElements.get(3).text());
                    //Tag
                    String[] types = new String[mangaTagDetailElements.size()];
                    String[] typeCodes = new String[mangaTagDetailElements.size()];
                    for (int i = 0; i < mangaTagDetailElements.size(); i++) {
                        String typeCode = mangaTagDetailElements.get(i).attr("href");
                        //加个\\转义字符
                        typeCode = typeCode.replaceAll("http://mangakakalot.com/manga_list\\?type=newest&category=", "");
                        typeCode = typeCode.replaceAll("&alpha=all&page=1&state=all", "");
                        typeCodes[i] = typeCode;
                        types[i] = mangaTagDetailElements.get(i).text();
                    }
                    item.setTypes(types);
                    item.setTypeCodes(typeCodes);

                    Elements chapterElements = doc.select("div.row a");
                    ArrayList<ChapterBean> chapters = new ArrayList<ChapterBean>();
                    ChapterBean chapterBean;
                    for (int i = 0; i < chapterElements.size(); i++) {
                        chapterBean = new ChapterBean();
                        chapterBean.setChapterPosition((i + 1) + "");
                        chapterBean.setChapterUrl(chapterElements.get(i).attr("href"));
                        chapters.add(chapterBean);
                    }
                    item.setChapters(chapters);
                    System.out.println();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
