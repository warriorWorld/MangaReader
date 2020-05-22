package com.truthower.suhang.mangareader.spider;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;

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
public class MangaReaderSpider extends SpiderBase {
    private String webUrl = "http://www.mangareader.net/";
    private String webUrlNoLastLine = "http://www.mangareader.net";
    private ArrayList<String> pathList = new ArrayList<String>();
//    private MangaReaderSpider() {
//    }
//
//    private static volatile MangaReaderSpider instance = null;
//
//    public static MangaReaderSpider getInstance() {
//        if (instance == null) {
//            //线程锁定
//            synchronized (MangaReaderSpider.class) {
//                //双重锁定
//                if (instance == null) {
//                    instance = new MangaReaderSpider();
//                }
//            }
//        }
//        return instance;
//    }

    @Override
    public <ResultObj> void getMangaList(final String type, final String page, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc = null;
                try {
                    if (TextUtils.isEmpty(type) || type.equals("all")) {
                        doc = Jsoup.connect(webUrl + "popular/" + page)
                                .timeout(timeout).get();
                    } else {
                        doc = Jsoup.connect(webUrl + "popular/" + type + "/" + page)
                                .timeout(timeout).get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                    return;
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
                            item.setUrl(webUrl + title);
                            name = test.get(i).text();
                            item.setName(name);
                            mangaList.add(item);
                        }
                    }
                    MangaListBean mangaListBean = new MangaListBean();
                    mangaListBean.setMangaList(mangaList);
                    jsoupCallBack.loadSucceed((ResultObj) mangaListBean);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                    return;
                }
            }
        }.start();
    }

    @Override
    public <ResultObj> void getMangaDetail(final String mangaURL, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc = null;
                try {
                    doc = Jsoup.connect(mangaURL)
                            .timeout(timeout).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                    return;
                }
                try {
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
                        item.setUrl(mangaURL);
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
                        return;
                    }
                } catch (Exception e) {
                    jsoupCallBack.loadFailed(Configure.WRONG_WEBSITE_EXCEPTION);
                    return;
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
        String[] mangaTypeCodes = {"all", "action", "adventure", "comedy", "demons", "drama", "ecchi", "fantasy", "gender-bender",
                "harem", "historical", "horror", "josei", "magic", "martial-arts", "mature", "mecha", "military", "mystery", "one-shot",
                "psychological", "romance", "school-life", "sci-fi", "seinen", "shoujo", "shoujoai", "shounen", "shounenai", "slice-of-life", "smut", "sports",
                "super-power", "supernatural", "tragedy", "vampire", "yaoi", "yuri"};
        return mangaTypeCodes;
    }

    @Override
    public String[] getMangaTypeCodes() {
        return new String[0];
    }

    @Override
    public String[] getAdultTypes() {
        String[] mangaTypeCodes = {"ecchi"};
        return mangaTypeCodes;
    }

    /**
     * 阅读页是一页一页翻得 只能这么爬...
     *
     * @param context
     * @param chapterUrl
     * @param jsoupCallBack
     * @param <ResultObj>
     */
    @Override
    public <ResultObj> void getMangaChapterPics(final Context context, final String chapterUrl, final JsoupCallBack<ResultObj> jsoupCallBack) {
        pathList = new ArrayList<String>();
        getPageSize(chapterUrl, new JsoupCallBack<Integer>() {
            @Override
            public void loadSucceed(Integer result) {
//                initPicPathList(context, chapterUrl, 1, result, jsoupCallBack);
                radicalInitPicPathList(chapterUrl, result, jsoupCallBack);
            }

            @Override
            public void loadFailed(String error) {
                jsoupCallBack.loadFailed(error);
                if (Configure.isTest) {
                    MangaDialog dialog = new MangaDialog(context);
                    dialog.show();
                    dialog.setTitle(error);
                }
            }
        });
    }

    @Override
    public <ResultObj> void getSearchResultList(final SearchType type, final String keyWord, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc = null;
                try {
                    String keyW = keyWord.replaceAll(" ", "+");
                    doc = Jsoup.connect(webUrl + "search/?w=" +
                            keyW +
                            "&rd=0&status=0&order=0&genre=0000000000000000000000000000000000000&p=0")
                            .timeout(timeout).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                    return;
                }
                if (null != doc) {
                    Elements test = null;
                    switch (type) {
                        case BY_MANGA_NAME:
                            test = doc.select("div.manga_name h3 a");
                            break;
                        case BY_MANGA_AUTHOR:
                            test = doc.select("div.authorinfo a");
                            break;
                    }

                    int count = test.size();
                    String title, name;
                    MangaBean item;
                    ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
                    mangaList.clear();
                    for (int i = 0; i < count; i++) {
                        title = test.get(i).attr("href");
                        if (!TextUtils.isEmpty(title) && !title.equals("null")) {
                            item = new MangaBean();
                            title = StringUtil.cutString(title, 1, title.length());
                            item.setUrl(webUrl + title);
                            name = test.get(i).text();
                            item.setName(name);
                            mangaList.add(item);
                        }
                    }
                    MangaListBean mangaListBean = new MangaListBean();
                    mangaListBean.setMangaList(mangaList);
                    jsoupCallBack.loadSucceed((ResultObj) mangaListBean);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                    return;
                }
            }
        }.start();
    }


//    private <ResultObj> void initPicPathList(final Context context, final String chapterUrl, final int page,
//                                             final int pageSize, final JsoupCallBack<ResultObj> jsoupCallBack) {
//        String url = chapterUrl + "/" + page;
//        HashMap<String, String> params = new HashMap<String, String>();
//        MStringRequest request = new MStringRequest(url, params,
//                new Response.Listener<String>() {
//
//                    @Override
//                    public void onResponse(String arg0) {
//                        // 得到包含某正则表达式的字符串
//                        Pattern p;
//                        p = Pattern.compile("https:[^\f\n\r\t]*?(jpg|png|gif|jpeg)");
//                        Matcher m;
//                        m = p.matcher(arg0);
//                        // String xxx;
//                        int cycle = 0;
//                        String urlResult = "", prefetch = "";
//                        while (m.find()) {
//                            // 获取到图片的URL 先获取到的第二个后获取到的第一个
//                            if (cycle == 1) {
//                                urlResult = m.group();
//                            } else if (cycle == 0) {
//                                prefetch = m.group();
//                            }
//                            cycle++;
//                        }
//                        if (page != pageSize) {
//                            pathList.add(urlResult);
//                            pathList.add(prefetch);
//                            Logger.d(urlResult + "\n" + prefetch);
//                        } else {
//                            //到最后一页时 只有一个图片
//                            pathList.add(prefetch);
//                            Logger.d(prefetch);
//                        }
//                        if (page == pageSize || page + 1 == pageSize) {
//                            //已找到所有的图片地址
//                            //TODO 得到结果
//                            jsoupCallBack.loadSucceed((ResultObj) pathList);
//                        } else {
//                            initPicPathList(context, chapterUrl, page + 2, pageSize, jsoupCallBack);
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError arg0) {
//                jsoupCallBack.loadFailed(arg0.toString());
//            }
//        });
//        VolleyTool.getInstance(context).getRequestQueue()
//                .add(request);
//    }

    private <ResultObj> void initPicPathList(final String chapterUrl, final int pageSize, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc = null;
                for (int i = 0; i < pageSize; i++) {
                    try {
                        doc = Jsoup.connect(chapterUrl + "/" + (i + 1))
                                .timeout(timeout).get();
                        Logger.d(chapterUrl + "/" + (i + 1));
                    } catch (IOException e) {
                        e.printStackTrace();
                        jsoupCallBack.loadFailed(e.toString());
                        return;
                    }
                    if (null != doc) {
                        Element mangaPicDetailElement = doc.getElementsByClass("episode-table").first();
                        Element element = mangaPicDetailElement.tagName("img");
                        String url = element.getElementsByTag("img").last().attr("src");
                        pathList.add(url);
                    }
                }
                if (null != pathList && pathList.size() > 0) {
                    jsoupCallBack.loadSucceed((ResultObj) pathList);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                }
            }
        }.start();
    }

    //激进爬虫,通过网站规律获取
    private <ResultObj> void radicalInitPicPathList(final String chapterUrl, final int pageSize, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc = null;
                try {
                    doc = Jsoup.connect(chapterUrl + "/" + 1)
                            .timeout(timeout).get();
                    Logger.d(chapterUrl + "/" + 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                    return;
                }
                Element mangaPicDetailElement = doc.getElementsByClass("episode-table").first();
                Element element = mangaPicDetailElement.tagName("img");
                String url = element.getElementsByTag("img").last().attr("src");
                pathList.add(url);
                long l = getLFromUrl(url);
                //再爬一遍最后一张图
                org.jsoup.nodes.Document doc1 = null;
                try {
                    doc1 = Jsoup.connect(chapterUrl + "/" + pageSize)
                            .timeout(timeout).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                    return;
                }
                Element mangaPicDetailElement1 = doc1.getElementsByClass("episode-table").first();
                Element element1 = mangaPicDetailElement1.tagName("img");
                String url1 = element1.getElementsByTag("img").last().attr("src");
                long l1 = getLFromUrl(url1);
                //再爬一遍倒数第二图
                org.jsoup.nodes.Document doc2 = null;
                try {
                    doc2 = Jsoup.connect(chapterUrl + "/" + (pageSize - 1))
                            .timeout(timeout).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                    return;
                }
                Element mangaPicDetailElement2 = doc2.getElementsByClass("episode-table").first();
                Element element2 = mangaPicDetailElement2.tagName("img");
                String url2 = element2.getElementsByTag("img").last().attr("src");
                long l2 = getLFromUrl(url2);

                boolean solved = false;
                for (int n = 1; n < 10; n++) {
                    if (((l1 - l + n) % pageSize == 0) && ((l2 - l + n) % (pageSize - 1) == 0)) {
                        for (int i = 1; i < pageSize; i++) {
                            pathList.add(url.replaceAll(l + ".jpg", (l + i * n) + ".jpg"));
                            solved = true;
                        }
                        break;
                    }
                }

                if (!solved) {
                    //说明都不适用,适用保守方法
                    initPicPathList(chapterUrl, pageSize, jsoupCallBack);
                    return;
                }

                if (null != pathList && pathList.size() > 0) {
                    jsoupCallBack.loadSucceed((ResultObj) pathList);
                } else {
                    jsoupCallBack.loadFailed("doc load failed");
                    return;
                }
            }
        }.start();
    }

    private long getLFromUrl(String url) {
        String[] ss = url.split("-");
        String s = ss[ss.length - 1];
        s = s.replaceAll(".jpg", "");
        return Long.valueOf(s);
    }

    private <ResultObj> void getPageSize(final String url, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
                org.jsoup.nodes.Document doc = null;
                try {
                    doc = Jsoup.connect(url + "/" + 1)
                            .timeout(timeout).get();
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                    return;
                }
                if (null != doc) {
                    Element page = doc.getElementById("selectpage");
                    Element lastPage = page.select("select option").last();

                    jsoupCallBack.loadSucceed((ResultObj) Integer.valueOf(lastPage.text()));
                } else {
                    jsoupCallBack.loadFailed("getPageSize doc load failed");
                    return;
                }
            }
        }.start();

    }

    @Override
    public int nextPageNeedAddCount() {
        return 30;
    }

    @Override
    public String getWebUrl() {
        return webUrl;
    }
}
