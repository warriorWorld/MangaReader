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
                    doc = Jsoup.connect("https://nhentai.net/g/202443/")
                            .timeout(10000).get();
                    Element test1 = doc.getElementById("info");
                    Element imgElement = doc.getElementById("cover").getElementsByTag("img").last();
                    Elements chaptersElement = doc.getElementsByClass("gallerythumb");

                    String tilte = test1.select("h1").text();
                    String thumbnail = imgElement.attr("src");
                    ChapterBean item = new ChapterBean();
                    ArrayList<ChapterBean> chapters = new ArrayList<ChapterBean>();
                    for (int i = 0; i < chaptersElement.size(); i++) {
                        String chapterThumbnail = chaptersElement.get(i).getElementsByTag("img").last().attr("src");
                        String url = chaptersElement.get(i).attr("href");
                        item = new ChapterBean();
                        item.setChapterUrl(url);
                        item.setChapterThumbnailUrl(chapterThumbnail);
                        chapters.add(item);
                    }
                    Elements tagsElements = test1.getElementsByClass("tags");
                    Elements tagElements = tagsElements.select("a");
                    ArrayList<String> typesList = new ArrayList<String>();

//                    ArrayList<String> artistsList = new ArrayList<String>();
                    for (int i = 0; i < tagElements.size(); i++) {
                        //漫画类型
                        String tag = tagElements.get(i).attr("href");
                        if (tag.contains("tag")) {
                            String[] split = tag.split("tag");
                            tag = split[split.length - 1];
                            tag = tag.replaceAll("/", "");
                            typesList.add(tag);
                        }
//                        else if (tag.contains("artist")) {
//                            String[] split = tag.split("artist");
//                            tag = split[split.length - 1];
//                            tag = tag.replaceAll("/", "");
//                            artistsList.add(tag);
//                        }
                    }
                    String[] types = new String[typesList.size()];
//                    String[] artists = new String[artistsList.size()];
                    for (int i = 0; i < typesList.size(); i++) {
                        types[i] = typesList.get(i);
                    }
//                    for (int i = 0; i < artistsList.size(); i++) {
//                        artists[i] = artistsList.get(i);
//                        System.out.println(artistsList.get(i));
//                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
