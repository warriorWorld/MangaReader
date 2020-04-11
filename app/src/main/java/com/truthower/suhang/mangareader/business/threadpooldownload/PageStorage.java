//package com.truthower.suhang.mangareader.business.threadpooldownload;
//
//import com.acorn.downloadsimulator.bean.Page;
//
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.BlockingQueue;
//
///**
// * Created by acorn on 2020/4/11.
// */
//public class PageStorage {
//    private BlockingQueue<Page> mPages = new ArrayBlockingQueue<>(30);
//
//    public Page take() throws InterruptedException {
//        Page page = mPages.take();
//        LogUtil.i("Storage take:" + page.getUrl() + ",Capacity:" + mPages.remainingCapacity());
//        return page;
//    }
//
//    public void put(Page page) throws InterruptedException {
//        mPages.put(page);
//        LogUtil.i("Storage put:" + page.getUrl() + ",Capacity:" + mPages.remainingCapacity());
//    }
//
//    public int remainingCapacity(){
//        return mPages.remainingCapacity();
//    }
//
//    public boolean isEmpty(){
//        return mPages.isEmpty();
//    }
//}
