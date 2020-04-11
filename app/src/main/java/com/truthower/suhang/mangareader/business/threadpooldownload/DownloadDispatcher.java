//package com.truthower.suhang.mangareader.business.threadpooldownload;
//
//import com.acorn.downloadsimulator.bean.Chapter;
//import com.truthower.suhang.mangareader.business.PageConsumer;
//import com.truthower.suhang.mangareader.business.PageProducer;
//import com.truthower.suhang.mangareader.business.PageStorage;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by acorn on 2020/4/11.
// */
//public class DownloadDispatcher {
//    private ExecutorService mExecutorService = Executors.newFixedThreadPool(30);
//    private List<Chapter> mChapters;
//
//    public static void main(String[] args) {
//        DownloadDispatcher dispatcher = new DownloadDispatcher();
//        dispatcher.execute(generateTestChapters());
//    }
//
//    public static List<Chapter> generateTestChapters() {
//        List<Chapter> chapters = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            chapters.add(new Chapter("url/" + i, "chapter:" + i));
//        }
//        return chapters;
//    }
//
//    public void execute(List<Chapter> chapters) {
//        PageStorage storage = new com.truthower.suhang.mangareader.business.threadpooldownload.PageStorage();
//        mExecutorService.execute(new PageProducer(storage, chapters));
//        for (int i = 0; i < 30; i++) {
//            mExecutorService.execute(new PageConsumer(storage));
//        }
//    }
//}
