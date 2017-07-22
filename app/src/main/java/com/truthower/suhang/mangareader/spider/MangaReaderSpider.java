package com.truthower.suhang.mangareader.spider;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.MangaListBean;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.StringUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * http://www.mangareader.net/
 */
public class MangaReaderSpider extends SpiderBase {
    private String webUrl = "http://www.mangareader.net/";
    private String webUrlNoLastLine = "http://www.mangareader.net";
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
                try {
                    if (TextUtils.isEmpty(type) || type.equals("all")) {
                        doc = Jsoup.connect(webUrl + "popular/" + page)
                                .timeout(10000).get();
                    } else {
                        doc = Jsoup.connect(webUrl + "popular/" + type + "/" + page)
                                .timeout(10000).get();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    jsoupCallBack.loadFailed(e.toString());
                }
                if (null != doc) {
                    Elements test = doc.select("div.mangaresultitem h3 a");
                    Elements test1 = doc.select("div.imgsearchresults");
                    int count = test.size();
                    String title;
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
                            item.setName(title);
                            mangaList.add(item);
                        }
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

                    MangaBean item = new MangaBean();
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
        String[] mangaTypeCodes = {"all", "action", "adventure", "comedy", "demons", "drama", "ecchi", "fantasy", "gender-bender",
                "harem", "historical", "horror", "josei", "magic", "martial-arts", "mature", "mecha", "military", "mystery", "one-shot",
                "psychological", "romance", "school-life", "sci-fi", "seinen", "shoujo", "shoujoai", "shounen", "shounenai", "slice-of-life", "smut", "sports",
                "super-power", "supernatural", "tragedy", "vampire", "yaoi", "yuri"};
        return mangaTypeCodes;
    }

    @Override
    public ArrayList<ChapterBean> getMangaChapterPics(String mangaName, String chapter, int picCount, JsoupCallBack jsoupCallBack) {
        return null;
    }

    @Override
    public int nextPageNeedAddCount() {
        return 30;
    }
}
