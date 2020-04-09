package com.truthower.suhang.mangareader.business.rxdownload;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.bean.RxDownloadPageBean;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.MangaDownloader;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.widget.toast.EasyToast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class DownloadService extends Service {
    private RxDownloadBean downloadBean;
    private Disposable mDisposable;
    private EasyToast mEasyToast;
    private MangaDownloader mDownloader;

    @Override
    public void onCreate() {
        super.onCreate();
        mEasyToast = new EasyToast(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadBean = DownloadCaretaker.getDownloadMemoto(this);
        final ArrayList<RxDownloadChapterBean> chapters = downloadBean.getChapters();
        mDownloader = downloadBean.getDownloader();
        Observable.fromIterable(chapters)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                //map是应用用同步的转换,这里显然只能用flatmap或concatmap 由于需要尽量按顺序下载 这里使用concatmap
                .concatMap(new Function<RxDownloadChapterBean, ObservableSource<ArrayList<RxDownloadPageBean>>>() {
                    @Override
                    public ObservableSource<ArrayList<RxDownloadPageBean>> apply(final RxDownloadChapterBean bean) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<ArrayList<RxDownloadPageBean>>() {
                            @Override
                            public void subscribe(final ObservableEmitter<ArrayList<RxDownloadPageBean>> e) throws Exception {
                                mDownloader.getMangaChapterPics(DownloadService.this, bean.getChapterUrl(), new JsoupCallBack<ArrayList<String>>() {
                                    @Override
                                    public void loadSucceed(ArrayList<String> result) {
                                        ArrayList<RxDownloadPageBean> pages = new ArrayList<>();
                                        for (int i = 0; i < result.size(); i++) {
                                            RxDownloadPageBean item = new RxDownloadPageBean();
                                            item.setPageUrl(result.get(i));
                                            item.setPageName(downloadBean.getMangaName() + "_" + bean.getChapterName() + "_" + i + ".png");
                                            item.setChapterBean(bean);
                                            pages.add(item);
                                        }
                                        bean.setPages(pages);
                                        e.onNext(pages);
                                    }

                                    @Override
                                    public void loadFailed(String error) {
                                        e.onError(new Exception());
                                    }
                                });
                            }
                        });
                    }
                })
                .flatMap(new Function<ArrayList<RxDownloadPageBean>, ObservableSource<RxDownloadPageBean>>() {
                    @Override
                    public ObservableSource<RxDownloadPageBean> apply(ArrayList<RxDownloadPageBean> beans) throws Exception {
                        return Observable.fromIterable(beans);
                    }
                })
                .subscribe(new Observer<RxDownloadPageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(RxDownloadPageBean value) {
                        Bitmap bp = downloadUrlBitmap(value.getPageUrl());
                        //把图片保存到本地
                        FileSpider.getInstance().saveBitmap(bp, value.getPageName(), value.getChapterBean().getChapterName(), downloadBean.getMangaName());
                        value.getChapterBean().getPages().remove(value);
                        if (value.getChapterBean().getPages().size() <= 0) {
                            chapters.remove(value.getChapterBean());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mEasyToast.showToast("全部下载完成!");
                    }
                });

        return super.onStartCommand(intent, flags, startId);
    }

    private Bitmap downloadUrlBitmap(String urlString) {
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        Bitmap bitmap = null;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            bitmap = BitmapFactory.decodeStream(in);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
        DownloadCaretaker.saveDownloadMemoto(this, downloadBean);
    }
}
