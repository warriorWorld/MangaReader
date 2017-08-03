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
                        System.out.println();
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
                    Element masthead = doc.select("h2.aname").first();
                    Element masthead3 = doc.select("td.propertytitle").get(4)
                            .lastElementSibling();
                    Elements mastheads1 = doc.select("span.genretags");
//                    Element masthead4 = doc.select("div.chico_manga").last()
//                            .lastElementSibling();
                    Elements mastheads2 = doc.select("div.chico_manga");

                    Element content = doc.getElementById("listing");
                    Element dates = content.getElementsByTag("td").last();


                    Element imgElement = doc.getElementById("mangaimg");
                    Element imgElement1 = imgElement.getElementsByTag("img").first();

                    Element readmangasumElement = doc.getElementById("readmangasum");
                    Element descriptionElement = readmangasumElement.select("p").first();

                    MangaBean item = new MangaBean();
                    item.setDescription(Html.fromHtml(descriptionElement.text()).toString());
                    item.setWebThumbnailUrl(imgElement1.attr("src"));
                    item.setName(masthead.text());
                    item.setAuthor(masthead3.text());
                    String[] types = new String[mastheads1.size()];
                    for (int i = 0; i < mastheads1.size(); i++) {
                        //漫画类型
                        types[i] = mastheads1.get(i).text();
                    }
                    item.setTypes(types);
                    item.setLast_update(dates.text());

                    String chapter;
                    String path;
                    ArrayList<ChapterBean> chapters = new ArrayList<ChapterBean>();
                    ChapterBean chapterBean;
                    for (int i = 0; i < mastheads2.size(); i++) {
                        //章节
                        if (mastheads2.size() <= 6) {
                            //跟底下那段一模一样 只不过当总章节小于6时需要特殊处理下
                            chapterBean = new ChapterBean();
                            chapter = mastheads2.get(i).lastElementSibling().text();
                            String[] s = chapter.split(" ");
                            chapter = s[s.length - 1];
                            chapterBean.setChapterPosition(chapter);
                            path = mastheads2.get(i).lastElementSibling().attr("href");
                            chapterBean.setChapterUrl(webUrlNoLastLine + path);
                            chapters.add(chapterBean);
                        } else {
                            if (i > 5) {
                                //前6个是最近更新的6个
                                chapterBean = new ChapterBean();
                                chapter = mastheads2.get(i).lastElementSibling().text();
                                String[] s = chapter.split(" ");
                                chapter = s[s.length - 1];
                                chapterBean.setChapterPosition(chapter);
                                path = mastheads2.get(i).lastElementSibling().attr("href");
                                chapterBean.setChapterUrl(webUrlNoLastLine + path);
                                chapters.add(chapterBean);
                            }
                        }
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
