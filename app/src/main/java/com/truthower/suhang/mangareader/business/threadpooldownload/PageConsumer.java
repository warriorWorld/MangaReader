//package com.truthower.suhang.mangareader.business.threadpooldownload;
//
//import com.acorn.downloadsimulator.bean.Page;
//import com.truthower.suhang.mangareader.business.PageStorage;
//
//import java.util.Random;
//
///**
// * Created by acorn on 2020/4/11.
// */
//public class PageConsumer implements Runnable {
//    private final PageStorage mStorage;
//
//    public PageConsumer(com.truthower.suhang.mangareader.business.threadpooldownload.PageStorage storage) {
//        mStorage = storage;
//    }
//
//    @Override
//    public void run() {
//        try {
//            while (true) {
//                LogUtil.i("Consumer take");
//                Page page = mStorage.take();
//                saveBitmap(downloadBitmap(page));
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Page downloadBitmap(Page page) throws InterruptedException {
//        Thread.sleep(new Random().nextInt(1500) + 100);
//        page.setBitmap("我是图片," + page.getPageName());
//        LogUtil.i("Consumer downloadBitmap " + page.getUrl() + " finished");
//        return page;
//    }
//
//    private void saveBitmap(Page page) throws InterruptedException {
//        Thread.sleep(new Random().nextInt(2500) + 200);
//        LogUtil.i("Consumer saveBitmap " + page.getUrl() + " finished");
//    }
//}
