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
import com.truthower.suhang.mangareader.volley.MStringRequest;
import com.truthower.suhang.mangareader.volley.VolleyTool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://www.mangareader.net/
 */
public class LMangaSpider extends SpiderBase {
    private String webUrl = "https://hitomi.la/";
    private String webUrlNoLastLine = "https://hitomi.la";
    private ArrayList<String> pathList = new ArrayList<String>();

    @Override
    public <ResultObj> void getMangaList(final String type, final String page, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc=null;
                try {
                    if (TextUtils.isEmpty(type) || type.equals("all")) {
                        doc = Jsoup.connect(webUrl + "index-all-" + page + ".html")
                                .timeout(10000).get();
                    } else {
                        doc = Jsoup.connect(webUrl + "tag/" + type + "-all-" + page + ".html")
                                .timeout(10000).get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    Elements mangaListElement = doc.select("div.manga");
                    Elements djListElement = doc.select("div.dj");
                    MangaBean item;
                    ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
                    for (int i = 0; i < mangaListElement.size(); i++) {
                        item = new MangaBean();
                        item.setWebThumbnailUrl("https:" + mangaListElement.get(i).select("div.dj-img1").first().getElementsByTag("img").last().attr("src"));
                        item.setName(mangaListElement.get(i).select("h1 a").text());
                        item.setUrl(webUrlNoLastLine + mangaListElement.get(i).select("h1 a").attr("href"));

                        mangaList.add(item);
                    }
                    for (int i = 0; i < djListElement.size(); i++) {
                        item = new MangaBean();
                        item.setWebThumbnailUrl("https:" + djListElement.get(i).select("div.dj-img1").first().getElementsByTag("img").last().attr("src"));
                        item.setName(djListElement.get(i).select("h1 a").text());
                        item.setUrl(webUrlNoLastLine + djListElement.get(i).select("h1 a").attr("href"));

                        mangaList.add(item);
                    }
                    MangaListBean mangaListBean = new MangaListBean();
                    mangaListBean.setMangaList(mangaList);
                    jsoupCallBack.loadSucceed((ResultObj) mangaListBean);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
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
                        Element mangaThumbnailElement = doc.select("div.cover a").first().getElementsByTag("img").last();
                        Elements mangaTagElements = doc.select("ul.tags a");

                        MangaBean mangaBean = new MangaBean();
                        mangaBean.setUrl(mangaURL);
                        mangaBean.setName("Can't found");
                        mangaBean.setWebThumbnailUrl("https:" + mangaThumbnailElement.attr("src"));

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
                        mangaBean.setTypes(types);
                        mangaBean.setTypeCodes(typeCodes);
                        //pics
                        Elements mangaPicElements = doc.select("div.thumbnail-container a");
                        Elements mangaInfoElement = doc.getElementsByClass("gallery-info");

                        //.select("div.thumbnail-container a")
                        ArrayList<ChapterBean> chapters = new ArrayList<ChapterBean>();
                        ChapterBean chapterBean;
                        String mangaId = mangaURL;
                        mangaId = mangaId.replaceAll("https://hitomi\\.la/galleries/", "");
                        mangaId = mangaId.replaceAll("\\.html", "");
                        System.out.println(mangaId);
                        for (int i = 0; i < mangaPicElements.size(); i++) {
                            chapterBean = new ChapterBean();
                            System.out.println(mangaPicElements.text());
                            if (mangaInfoElement.text().contains("doujinshi")) {
                                chapterBean.setChapterThumbnailUrl("https://atn.hitomi.la/smalltn/" + mangaId + "/" + (i + 1) + ".jpg.jpg");
                                chapterBean.setImgUrl("https://aa.hitomi.la/galleries/" + mangaId + "/" + (i + 1) + ".jpg");
                            } else {
                                String position = "";
                                if (mangaPicElements.size() < 10) {
                                    position = "00" + (i + 1);
                                } else if (mangaPicElements.size() < 100) {
                                    position = "0" + (i + 1);
                                } else if (mangaPicElements.size() < 1000) {
                                    position = "" + (i + 1);
                                }
                                chapterBean.setChapterThumbnailUrl("https://btn.hitomi.la/smalltn/" + mangaId + "/" + position + ".jpg.jpg");
                                chapterBean.setImgUrl("https://ba.hitomi.la/galleries/" + mangaId + "/" + position + ".jpg");
                            }
                            chapters.add(chapterBean);
                        }
                        mangaBean.setChapters(chapters);
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
