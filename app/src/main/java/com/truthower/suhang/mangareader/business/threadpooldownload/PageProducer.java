//package com.truthower.suhang.mangareader.business.threadpooldownload;
//
//import com.acorn.downloadsimulator.bean.Chapter;
//import com.acorn.downloadsimulator.bean.Page;
//import com.truthower.suhang.mangareader.business.PageStorage;
//
//import java.util.List;
//
///**
// * Created by acorn on 2020/4/11.
// */
//public class PageProducer implements Runnable {
//    private final List<Chapter> mChapters;
//    private final PageStorage mStorage;
//
//    public PageProducer(PageStorage storage, List<Chapter> chapters) {
//        mChapters = chapters;
//        mStorage = storage;
//    }
//
//    @Override
//    public void run() {
//        for (final Chapter chapter : mChapters) {
//            ChapterResover.resoveChapter(chapter.getUrl(), new ChapterResover.OnChapterResoveListener() {
//                @Override
//                public void onChapterResoved(List<String> urls) {
//                    try {
//                        int length = urls.size();
//                        for (int i = 0; i < length; i++) {
//                            String url = urls.get(i);
//                            LogUtil.i("Producer put " + url);
//                            mStorage.put(new Page(url, chapter.getChapterName() + i, chapter.getChapterName()));
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        LogUtil.e("Producer error:" + e.getMessage());
//                    }
//                }
//            });
//        }
//    }
//}
