//package com.truthower.suhang.mangareader.spider;
//
//import com.truthower.suhang.mangareader.bean.ChapterBean;
//import com.truthower.suhang.mangareader.bean.MangaBean;
//import com.truthower.suhang.mangareader.config.Configure;
//import com.truthower.suhang.mangareader.listener.JsoupCallBack;
//import com.truthower.suhang.mangareader.utils.Logger;
//
//import org.jsoup.Jsoup;
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
///**
// * 这个网站的详情有两种样式 所以再加一个爬虫文件
// */
//public class KaKaLot1Spider extends KaKaLotSpider{
//    @Override
//    public <ResultObj> void getMangaDetail(final String mangaURL, final JsoupCallBack<ResultObj> jsoupCallBack) {
//        new Thread() {
//            @Override
//            public void run() {
//                org.jsoup.nodes.Document doc=null;
//                try {
//                    doc = Jsoup.connect(mangaURL)
//                            .timeout(10000).get();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    jsoupCallBack.loadFailed(e.toString());
//                }
//                try {
//                    if (null != doc) {
//                        Elements mangaPicDetailElements = doc.select("div.manga-info-pic");
//                        Elements mangaTextDetailElements = doc.select("ul.manga-info-text li");
//                        Elements mangaTagDetailElements = null;
//                        if (null != mangaTextDetailElements && mangaTextDetailElements.size() > 6) {
//                            mangaTagDetailElements = mangaTextDetailElements.get(6).select("a");
//                        }
//                        MangaBean item = new MangaBean();
//                        item.setUrl(mangaURL);
//                        item.setWebThumbnailUrl(mangaPicDetailElements.first().getElementsByTag("img").last().attr("src"));
//
//                        if (null != mangaTagDetailElements) {
//                            item.setName(mangaTextDetailElements.get(0).select("h1").text());
//                            String authors = mangaTextDetailElements.get(1).text();
//                            authors = authors.replaceAll("Author\\(s\\) : ", "");
//                            authors = authors.substring(0, authors.length() - 1);//这个网站的作者最后一位总是有个逗号
//                            item.setAuthor(authors);
//                            String lastUpadte = mangaTextDetailElements.get(3).text();
//                            lastUpadte = lastUpadte.replaceAll("Last updated : ", "");
//                            item.setLast_update(lastUpadte);
//
//                            //Tag	                            //Tag
//                            String[] types = new String[mangaTagDetailElements.size()];
//                            String[] typeCodes = new String[mangaTagDetailElements.size()];
//                            for (int i = 0; i < mangaTagDetailElements.size(); i++) {
//                                String typeCode = mangaTagDetailElements.get(i).attr("href");
//                                //加个\\转义字符
//                                typeCode = typeCode.replaceAll("https://mangakakalot.com/manga_list\\?type=newest&category=", "");
//                                typeCode = typeCode.replaceAll("&alpha=all&page=1&state=all", "");
//                                typeCode=typeCode.replaceAll("https://manganelo.com","");
//                                typeCodes[i] = typeCode;
//                                types[i] = mangaTagDetailElements.get(i).text();
//                            }
//                            item.setTypes(types);
//                            item.setTypeCodes(typeCodes);
//                            Logger.d("inside:  "+item.getName()+"   "+item.getLast_update()+"   "+item.getWebThumbnailUrl()+"   "+item.getAuthor()+"   "+item.getUrl());
//                        }else {
//                            Logger.d("mangaTagDetailElements=null");
//                        }
//
//                        Elements chapterElements = doc.select("ul.row-content-chapter li");
//                        ArrayList<ChapterBean> chapters = new ArrayList<ChapterBean>();
//                        ChapterBean chapterBean;
//                        int chapterPosition = 0;
//                        for (int i = chapterElements.size() - 1; i >= 0; i--) {
//                            //原网站是倒叙排列的
//                            chapterBean = new ChapterBean();
//                            chapterPosition++;
//                            chapterBean.setChapterPosition(chapterPosition + "");
//                            chapterBean.setChapterUrl(chapterElements.get(i).attr("href"));
//                            chapters.add(chapterBean);
//                        }
//                        item.setChapters(chapters);
//                        jsoupCallBack.loadSucceed((ResultObj) item);
//                    } else {
//                        jsoupCallBack.loadFailed("doc load failed");
//                    }
//                } catch (Exception e) {
//                    jsoupCallBack.loadFailed(Configure.WRONG_WEBSITE_EXCEPTION);
//                }
//            }
//        }.start();
//    }
//}
