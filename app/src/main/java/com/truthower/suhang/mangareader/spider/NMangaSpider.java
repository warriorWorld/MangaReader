package com.truthower.suhang.mangareader.spider;

import android.content.Context;
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
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * http://www.mangareader.net/
 */
public class NMangaSpider extends SpiderBase {
    private String webUrl = "https://nhentai.net/";
    private String webUrlNoLastLine = "https://nhentai.net";
    private ArrayList<String> pathList = new ArrayList<String>();

    @Override
    public <ResultObj> void getMangaList(final String type, final String page, final JsoupCallBack<ResultObj> jsoupCallBack) {
        new Thread() {
            @Override
            public void run() {
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
                    Element test1 = doc.getElementById("info");
                    Element imgElement = doc.getElementById("cover").getElementsByTag("img").last();
                    Elements chaptersElement = doc.getElementsByClass("gallerythumb");
                    MangaBean mangaBean = new MangaBean();

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
