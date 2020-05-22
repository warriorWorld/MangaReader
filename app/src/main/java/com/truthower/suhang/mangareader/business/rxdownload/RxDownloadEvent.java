//package com.truthower.suhang.mangareader.business.rxdownload;
//
//import com.truthower.suhang.mangareader.bean.RxDownloadBean;
//import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
//
//public class RxDownloadEvent extends EventBusEvent {
//    private RxDownloadBean mDownloadBean;
//    private int position;
//
//    public RxDownloadEvent(int event) {
//        super(event);
//    }
//
//    public RxDownloadEvent(int event, RxDownloadBean downloadBean, int position) {
//        super(event);
//        this.mDownloadBean = downloadBean;
//        this.position = position;
//    }
//
//    public RxDownloadBean getDownloadBean() {
//        return mDownloadBean;
//    }
//
//    public void setDownloadBean(RxDownloadBean downloadBean) {
//        mDownloadBean = downloadBean;
//    }
//
//    public int getPosition() {
//        return position;
//    }
//
//    public void setPosition(int position) {
//        this.position = position;
//    }
//}
