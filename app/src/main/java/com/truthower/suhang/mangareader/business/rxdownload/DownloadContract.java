package com.truthower.suhang.mangareader.business.rxdownload;

import com.truthower.suhang.mangareader.base.BasePresenter;
import com.truthower.suhang.mangareader.base.BaseView;
import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.bean.WordsBookBean;
import com.truthower.suhang.mangareader.business.words.WordsContract;

import java.util.ArrayList;

public interface DownloadContract {
    interface Presenter extends BasePresenter {
        void getDownloadInfo();

        void startDownload();

        void stopDownload();

        void updateDownload();
    }

    interface View extends BaseView<DownloadContract.Presenter> {
        void displayInfo(RxDownloadBean downloadBean);

        void displayStart();

        void displayStop();

        void displayUpdate();
    }
}
