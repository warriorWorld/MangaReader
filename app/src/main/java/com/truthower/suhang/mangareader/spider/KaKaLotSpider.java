package com.truthower.suhang.mangareader.spider;

import android.content.Context;
import android.text.Html;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://www.mangareader.net/
 */
public class KaKaLotSpider extends SpiderBase {
    private String webUrl = "http://mangakakalot.com/";
    private String webUrlNoLastLine = "http://mangakakalot.com";

    @Override
    public <ResultObj> void getMangaList(final String type, final String page, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (TextUtils.isEmpty(type) || type.equals("all")) {
                        doc = Jsoup.connect(webUrl + "manga_list?type=topview&category=all&alpha=all&page=" + page + "&state=all")
                                .timeout(10000).get();
                    } else {
                        doc = Jsoup.connect(webUrl + "manga_list?type=topview&category=" + type + "&alpha=all&page=" + page + "&state=all")
                                .timeout(10000).get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    Elements mangaListElements = doc.select("div.list-truyen-item-wrap");
                    MangaBean item;
                    ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
                    for (int i = 0; i < mangaListElements.size(); i++) {
                        item = new MangaBean();
                        item.setName(mangaListElements.get(i).getElementsByTag("img").last().attr("alt"));
                        item.setUrl(mangaListElements.get(i).select("a").first().attr("href"));
                        item.setWebThumbnailUrl(mangaListElements.get(i).getElementsByTag("img").last().attr("src"));
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
                try {
                    doc = Jsoup.connect(mangaURL)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                try {
                    if (null != doc) {
                        Elements mangaPicDetailElements = doc.select("div.manga-info-pic");
                        Elements mangaTextDetailElements = doc.select("ul.manga-info-text li");
                        Elements mangaTagDetailElements = null;
                        if (null != mangaTextDetailElements && mangaTextDetailElements.size() > 6) {
                            mangaTagDetailElements = mangaTextDetailElements.get(6).select("a");
                        }
                        MangaBean item = new MangaBean();
                        item.setWebThumbnailUrl(mangaPicDetailElements.first().getElementsByTag("img").last().attr("src"));

                        if (null != mangaTagDetailElements) {
                            item.setName(mangaTextDetailElements.get(0).select("h1").text());
                            String authors = mangaTextDetailElements.get(1).text();
                            authors = authors.replaceAll("Author\\(s\\) : ", "");
                            item.setAuthor(authors);
                            String lastUpadte = mangaTextDetailElements.get(3).text();
                            lastUpadte = lastUpadte.replaceAll("Last updated : ", "");
                            item.setLast_update(lastUpadte);

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
                        }

                        Elements chapterElements = doc.select("div.row a");
                        ArrayList<ChapterBean> chapters = new ArrayList<ChapterBean>();
                        ChapterBean chapterBean;
                        int chapterPosition = 0;
                        for (int i = chapterElements.size() - 1; i >= 0; i--) {
                            //原网站是倒叙排列的
                            chapterBean = new ChapterBean();
                            chapterPosition++;
                            chapterBean.setChapterPosition(chapterPosition + "");
                            chapterBean.setChapterUrl(chapterElements.get(i).attr("href"));
                            chapters.add(chapterBean);
                        }
                        item.setChapters(chapters);
                        jsoupCallBack.loadSucceed((ResultObj) item);
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
        return false;
    }

    @Override
    public String[] getMangaTypes() {
        String[] mangaTypeCodes = {"all", "action", "adult", "adventure", "comedy", "cooking", "doujinshi", "drama", "ecchi", "fantasy", "gender-bender",
                "harem", "historical", "horror", "josei", "manhua", "manhwa", "martial-arts", "mature", "mecha", "medical", "mystery", "one-shot",
                "psychological", "romance", "school-life", "sci-fi", "seinen", "shoujo", "shoujoai", "shounen", "shounenai", "slice-of-life", "smut", "sports",
                "supernatural", "tragedy", "webtoons", "yaoi", "yuri"};
        return mangaTypeCodes;
    }

    @Override
    public String[] getMangaTypeCodes() {
        String[] mangaTypeCodes = {"all", "2", "3", "4", "6", "7", "9", "10", "11", "12", "13",
                "14", "15", "16", "17", "44", "43", "19", "20", "21", "22", "24", "25",
                "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37",
                "38", "39", "40", "41", "42"};
        return mangaTypeCodes;
    }

    @Override
    public <ResultObj> void getMangaChapterPics(final Context context, final String chapterUrl, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(chapterUrl)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    Elements mangaPicsElements = doc.select("div.vung-doc").first().getElementsByTag("img");
                    ArrayList<String> pathList = new ArrayList<String>();
                    for (int i = 0; i < mangaPicsElements.size(); i++) {
                        pathList.add(mangaPicsElements.get(i).attr("src"));
                    }
                    jsoupCallBack.loadSucceed((ResultObj) pathList);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
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
