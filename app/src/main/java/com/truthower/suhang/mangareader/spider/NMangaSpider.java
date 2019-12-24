package com.truthower.suhang.mangareader.spider;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.StringUtil;
import com.truthower.suhang.mangareader.volley.MStringRequest;
import com.truthower.suhang.mangareader.volley.VolleyTool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://www.mangareader.net/
 */
public class NMangaSpider extends SpiderBase {
    private String webUrl = "https://nhentai.net/";
    private String webUrlNoLastLine = "https://nhentai.net";

    @Override
    public <ResultObj> void getMangaList(final String type, final String page, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc=null;
                try {
                    if (TextUtils.isEmpty(type) || type.equals("all")) {
                        doc = Jsoup.connect(webUrl + "?page=" + page)
                                .timeout(10000).get();
                    } else {
                        doc = Jsoup.connect(webUrl + "tag/" + type + "/?page=" + page)
                                .timeout(10000).get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
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
                        item.setUrl(webUrlNoLastLine + url);
                        item.setName(title);
                        mangaList.add(item);
                    }
                    MangaListBean mangaListBean = new MangaListBean();
                    mangaListBean.setMangaList(mangaList);
                    jsoupCallBack.loadSucceed((ResultObj) mangaListBean);
                } else {
                    jsoupCallBack.loadFailed(Configure.WRONG_WEBSITE_EXCEPTION);
                }
            }
        }.start();
    }

    @Override
    public <ResultObj> void getMangaDetail(final String mangaURL, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc=null;
                try {
                    doc = Jsoup.connect(mangaURL)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                try {
                    if (null != doc) {
                        Element test1 = doc.getElementById("info");
                        Element imgElement = doc.getElementById("cover").getElementsByTag("img").last();
                        Elements chaptersElement = doc.getElementsByClass("gallerythumb");
                        MangaBean mangaBean = new MangaBean();
                        mangaBean.setUrl(mangaURL);
                        String tilte = test1.select("h1").text();
                        String thumbnail = imgElement.attr("src");
                        ChapterBean item = new ChapterBean();
                        ArrayList<ChapterBean> chapters = new ArrayList<ChapterBean>();
                        for (int i = 0; i < chaptersElement.size(); i++) {
                            String chapterThumbnail = chaptersElement.get(i).getElementsByTag("img").last().attr("src");
                            String url = chapterThumbnail.replaceAll("t.nhentai.net", "i.nhentai.net");
                            url = url.replaceAll("t.jpg", ".jpg");
                            item = new ChapterBean();
                            item.setImgUrl(url);
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
                        mangaBean.setTypes(types);
                        mangaBean.setChapters(chapters);
                        mangaBean.setName(tilte);
                        mangaBean.setWebThumbnailUrl(thumbnail);
                        jsoupCallBack.loadSucceed((ResultObj) mangaBean);
                    } else {
                        jsoupCallBack.loadFailed("doc load failed");
                    }
                } catch (Exception e) {
                    jsoupCallBack.loadFailed(Configure.WRONG_WEBSITE_EXCEPTION);
                }
            }
        }.start();
    }


    @Override
    public boolean isOneShot() {
        return true;
    }

    @Override
    public String[] getMangaTypes() {
        String[] mangaTypeCodes = {"all", "slave", "shota", "collar", "human-pet", "spanking", "sex-toy", "mind-control", "gender-bender",
                "tomgirl", "femdom", "bondage", "bdsm", "exhibitionism", "shotacon", "nakadashi", "mind-break", "apron", "crossdressing",
                "bodysuit", "humiliation", "torture", "dog-girl", "dog-boy", "dog", "tomboy", "shoujoai", "shounen", "shounenai",
                "slice-of-life", "smut", "sports", "super-power", "supernatural", "tragedy", "vampire", "yaoi", "yuri"};
        return mangaTypeCodes;
    }

    @Override
    public String[] getMangaTypeCodes() {
        return new String[0];
    }

    @Override
    public String[] getAdultTypes() {
        return new String[0];
    }

    @Override
    public <ResultObj> void getMangaChapterPics(final Context context, final String chapterUrl, final JsoupCallBack<ResultObj> jsoupCallBack) {
    }

    @Override
    public <ResultObj> void getSearchResultList(SearchType type, String keyWord, JsoupCallBack<ResultObj> jsoupCallBack) {

    }

    @Override
    public int nextPageNeedAddCount() {
        return 1;
    }

    @Override
    public String getWebUrl() {
        return webUrl;
    }
}
