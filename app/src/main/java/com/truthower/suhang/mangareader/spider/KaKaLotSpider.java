package com.truthower.suhang.mangareader.spider;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
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
    private ArrayList<String> pathList = new ArrayList<String>();

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
                if (null != doc) {
                    Elements mangaPicDetailElements = doc.select("div.manga-info-pic");
                    Elements mangaTextDetailElements = doc.select("ul.manga-info-text li");
                    Elements mangaTagDetailElements = doc.select("ul.manga-info-text li").get(6).select("a");

                    MangaBean item = new MangaBean();
                    item.setWebThumbnailUrl(mangaPicDetailElements.first().getElementsByTag("img").last().attr("src"));
                    item.setName(mangaPicDetailElements.first().getElementsByTag("img").last().attr("alt"));
                    item.setAuthor(mangaTextDetailElements.get(1).text());
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
                    jsoupCallBack.loadSucceed((ResultObj) item);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
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
        pathList = new ArrayList<String>();
        getPageSize(chapterUrl, new JsoupCallBack<Integer>() {
            @Override
            public void loadSucceed(Integer result) {
                initPicPathList(context, chapterUrl, 1, result, jsoupCallBack);
            }

            @Override
            public void loadFailed(String error) {

            }
        });
    }


    private <ResultObj> void initPicPathList(final Context context, final String chapterUrl, final int page,
                                             final int pageSize, final JsoupCallBack<ResultObj> jsoupCallBack) {
        String url = chapterUrl + "/" + page;
        HashMap<String, String> params = new HashMap<String, String>();
        MStringRequest request = new MStringRequest(url, params,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String arg0) {
                        // 得到包含某正则表达式的字符串
                        Pattern p;
                        p = Pattern.compile("http:[^\f\n\r\t]*?(jpg|png|gif|jpeg)");
                        Matcher m;
                        m = p.matcher(arg0);
                        // String xxx;
                        int cycle = 0;
                        String urlResult = "", prefetch = "";
                        while (m.find()) {
                            // 获取到图片的URL 先获取到的第二个后获取到的第一个
                            if (cycle == 1) {
                                urlResult = m.group();
                            } else if (cycle == 0) {
                                prefetch = m.group();
                            }
                            cycle++;
                        }
                        if (page != pageSize) {
                            pathList.add(urlResult);
                            pathList.add(prefetch);
                            Logger.d(urlResult + "\n" + prefetch);
                        } else {
                            //到最后一页时 只有一个图片
                            pathList.add(prefetch);
                            Logger.d(prefetch);
                        }
                        if (page == pageSize || page + 1 == pageSize) {
                            //已找到所有的图片地址
                            //TODO 得到结果
                            jsoupCallBack.loadSucceed((ResultObj) pathList);
                        } else {
                            initPicPathList(context, chapterUrl, page + 2, pageSize, jsoupCallBack);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                jsoupCallBack.loadFailed(arg0.toString());
            }
        });
        VolleyTool.getInstance(context).getRequestQueue()
                .add(request);
    }

    private <ResultObj> void getPageSize(final String url, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(url + "/" + 1)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    Element page = doc.getElementById("selectpage");
                    Element lastPage = page.select("select option").last();

                    jsoupCallBack.loadSucceed((ResultObj) Integer.valueOf(lastPage.text()));
                } else {
                    jsoupCallBack.loadFailed("getPageSize doc load failed");
                }
            }
        }.start();

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
