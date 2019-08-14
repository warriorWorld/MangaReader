package com.truthower.suhang.mangareader.business.collect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaRecyclerListAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;
import com.truthower.suhang.mangareader.service.BaseObserver;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.PermissionUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.ListDialog;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/7/29.
 */

public class CollectedActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    private View emptyView;
    private ArrayList<MangaBean> collectedMangaList = new ArrayList<>();
    private int collectType;
    private OnlineMangaRecyclerListAdapter adapter;
    private RecyclerView mangaRcv;
    private SwipeToLoadLayout swipeToLoadLayout;
    private TextView totalCollectTv;
    private SpiderBase spider;
    private String[] selectOptions = {"获取最后更新日期", "修复缩略图"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        collectType = intent.getIntExtra("collectType", Configure.COLLECT_TYPE_COLLECT);
        initUI();
        initSpider();
        doGetData();
    }

    private void initUI() {
        totalCollectTv = (TextView) findViewById(R.id.total_collect_tv);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        mangaRcv = (RecyclerView) findViewById(R.id.swipe_target);
        mangaRcv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mangaRcv.setFocusableInTouchMode(false);
        mangaRcv.setFocusable(false);
        mangaRcv.setHasFixedSize(true);
        TopBar topBar = (TopBar) findViewById(R.id.gradient_bar);
        topBar.setVisibility(View.GONE);

        emptyView = findViewById(R.id.empty_view);

        switch (collectType) {
            case Configure.COLLECT_TYPE_COLLECT:
                baseTopBar.setTitle("我的收藏");
                break;
            case Configure.COLLECT_TYPE_WAIT_FOR_UPDATE:
                baseTopBar.setTitle("正在追更");
                break;
            case Configure.COLLECT_TYPE_FINISHED:
                baseTopBar.setTitle("我看完的");
                break;
        }
        baseTopBar.setRightBackground(R.drawable.more);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {
                showOptionsDialog();
            }

            @Override
            public void onTitleClick() {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.collect_manga_list;
    }

    private void doGetData() {
//        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
//            this.finish();
//            return;
//        }
//        SingleLoadBarUtil.getInstance().showLoadBar(CollectedActivity.this);
//        AVQuery<AVObject> query = new AVQuery<>("Collected");
//        query.whereEqualTo("owner", LoginBean.getInstance().getUserName());
//        query.limit(999);
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                SingleLoadBarUtil.getInstance().dismissLoadBar();
//                if (LeanCloundUtil.handleLeanResult(CollectedActivity.this, e)) {
//                    collectedMangaList = new ArrayList<MangaBean>();
//                    if (null != list && list.size() > 0) {
//                        MangaBean item;
//                        for (int i = 0; i < list.size(); i++) {
//                            item = new MangaBean();
//                            item.setName(list.get(i).getString("mangaName"));
//                            item.setWebThumbnailUrl(list.get(i).getString("webThumbnailUrl"));
//                            item.setUrl(list.get(i).getString("mangaUrl"));
//
//                            switch (collectType) {
//                                case Configure.COLLECT_TYPE_COLLECT:
//                                    if (!list.get(i).getBoolean("finished")) {
//                                        collectedMangaList.add(item);
//                                    }
//                                    break;
//                                case Configure.COLLECT_TYPE_WAIT_FOR_UPDATE:
//                                    if (list.get(i).getBoolean("top")) {
//                                        collectedMangaList.add(item);
//                                    }
//                                    break;
//                                case Configure.COLLECT_TYPE_FINISHED:
//                                    if (list.get(i).getBoolean("finished")) {
//                                        collectedMangaList.add(item);
//                                    }
//                                    break;
//                            }
//                        }
//                    }
//                    initListView();
//                }
//            }
//        });
    }

    private void initSpider() {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.truthower.suhang.mangareader.spider." + BaseParameterUtil.getInstance().getCurrentWebSite(this) + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            baseToast.showToast(e + "");
            e.printStackTrace();
        }
    }

    private void showOptionsDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        getLastUpdateList();
                        break;
                    case 1:
                        repairThumbil();
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(selectOptions);
    }

    private void getLastUpdateList() {
        Observable.fromIterable(collectedMangaList)
                .flatMap(new Function<MangaBean, ObservableSource<MangaBean>>() {
                    @Override
                    public ObservableSource<MangaBean> apply(final MangaBean bean) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<MangaBean>() {//创建新的支流
                            @Override
                            public void subscribe(final ObservableEmitter<MangaBean> e) throws Exception {
                                getMangaDetail(bean.getUrl(), new JsoupCallBack<MangaBean>() {
                                    @Override
                                    public void loadSucceed(MangaBean result) {
                                        result.setUrl(bean.getUrl());
                                        result.setName(bean.getName());
                                        result.setWebThumbnailUrl(bean.getWebThumbnailUrl());
                                        e.onNext(result);//这个onnext和onComplete并不是最后的那个onnext和onComplete而是其中一个分支，最终这些分支经过flatMap汇聚
                                        e.onComplete();
                                    }

                                    @Override
                                    public void loadFailed(String error) {
                                        e.onNext(bean);
                                        e.onComplete();
                                    }
                                });
                            }
                        });
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<MangaBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<MangaBean> value) {
                        collectedMangaList = (ArrayList<MangaBean>) value;
                        initListView();
                        baseToast.showToast("获取完成");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private void repairThumbil() {
        Observable.fromIterable(collectedMangaList)
                .flatMap(new Function<MangaBean, ObservableSource<MangaBean>>() {
                    @Override
                    public ObservableSource<MangaBean> apply(final MangaBean bean) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<MangaBean>() {
                            @Override
                            public void subscribe(final ObservableEmitter<MangaBean> e) throws Exception {
                                ImageLoader.getInstance().loadImage(bean.getWebThumbnailUrl(), new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String s, View view) {

                                    }

                                    @Override
                                    public void onLoadingFailed(String s, View view, FailReason reason) {
                                        bean.setThumbnailLoadFail(true);
                                        e.onNext(bean);
                                        e.onComplete();
                                    }

                                    @Override
                                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                        bean.setThumbnailLoadFail(false);
                                        e.onNext(bean);
                                        e.onComplete();
                                    }

                                    @Override
                                    public void onLoadingCancelled(String s, View view) {
                                        bean.setThumbnailLoadFail(true);
                                        e.onNext(bean);
                                        e.onComplete();
                                    }
                                });
                            }
                        });
                    }
                })
                .filter(new Predicate<MangaBean>() {
                    @Override
                    public boolean test(MangaBean bean) throws Exception {
                        return bean.isThumbnailLoadFail();
                    }
                })
                .flatMap(new Function<MangaBean, ObservableSource<MangaBean>>() {
                    @Override
                    public ObservableSource<MangaBean> apply(final MangaBean bean) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<MangaBean>() {//创建新的支流
                            @Override
                            public void subscribe(final ObservableEmitter<MangaBean> e) throws Exception {
                                getMangaDetail(bean.getUrl(), new JsoupCallBack<MangaBean>() {
                                    @Override
                                    public void loadSucceed(MangaBean result) {
                                        result.setMangaDetailLoadSuccess(true);
                                        e.onNext(result);//这个onnext和onComplete并不是最后的那个onnext和onComplete而是其中一个分支，最终这些分支经过flatMap汇聚
                                        e.onComplete();
                                    }

                                    @Override
                                    public void loadFailed(String error) {
                                        MangaBean res = new MangaBean();
                                        res.setMangaDetailLoadSuccess(false);
                                        e.onNext(res);
                                        e.onComplete();
                                    }
                                });
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<MangaBean>(CollectedActivity.this) {

                    @Override
                    public void onNext(MangaBean value) {
//                        baseToast.showToast(value.getName());
                        if (value.isMangaDetailLoadSuccess())
                            modifyThumbilUrl(value);
                        else
                            onError(new RuntimeException("not success"));
                        Logger.d("RXJAVA onNext" + value.getName());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Logger.d("RXJAVA onError" + e);
                        doGetData();
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        Logger.d("RXJAVA   onComplete");
                        doGetData();
                    }
                });
    }

    //因为我不知道当期收藏的漫画是哪个网站的 所以就一个个试
    private int trySpiderPosition = 0;

    private void getMangaDetail(final String url, final JsoupCallBack<MangaBean> resultListener) {
        spider.getMangaDetail(url, new JsoupCallBack<MangaBean>() {
            @Override
            public void loadSucceed(final MangaBean result) {
                resultListener.loadSucceed(result);
            }

            @Override
            public void loadFailed(String error) {
                if (error.equals(Configure.WRONG_WEBSITE_EXCEPTION)) {
                    try {
                        if (PermissionUtil.isMaster(CollectedActivity.this)) {
                            BaseParameterUtil.getInstance().saveCurrentWebSite(CollectedActivity.this, Configure.masterWebsList[trySpiderPosition]);
                        } else {
                            BaseParameterUtil.getInstance().saveCurrentWebSite(CollectedActivity.this, Configure.websList[trySpiderPosition]);
                        }
                        initSpider();
                        getMangaDetail(url, resultListener);
                        trySpiderPosition++;
                    } catch (IndexOutOfBoundsException e) {
                        resultListener.loadFailed("IndexOutOfBoundsException");
                    }
                } else {
                    resultListener.loadFailed("orther failed");
                }
            }
        });
    }

    private void modifyThumbilUrl(final MangaBean mangaBean) {
//        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
//            return;
//        }
//        AVQuery<AVObject> query1 = new AVQuery<>("Collected");
//        query1.whereEqualTo("mangaUrl", mangaBean.getUrl());
//
//        AVQuery<AVObject> query2 = new AVQuery<>("Collected");
//        query2.whereEqualTo("owner", LoginBean.getInstance().getUserName());
//        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if (LeanCloundUtil.handleLeanResult(CollectedActivity.this, e)) {
//                    if (null != list && list.size() > 0) {
//                        //已存在的保存
//                        AVObject object = AVObject.createWithoutData("Collected", list.get(0).getObjectId());
//                        object.put("webThumbnailUrl", mangaBean.getWebThumbnailUrl());
//                        object.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if (LeanCloundUtil.handleLeanResult(CollectedActivity.this, e)) {
//                                    baseToast.showToast(mangaBean.getName() + "修复缩略图成功!");
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
    }

    private void initListView() {
        try {
            if (null == collectedMangaList || collectedMangaList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new OnlineMangaRecyclerListAdapter(this, collectedMangaList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(CollectedActivity.this, WebMangaDetailsActivity.class);
                        intent.putExtra("mangaUrl", collectedMangaList.get(position).getUrl());
                        startActivity(intent);
                    }
                });
                mangaRcv.setAdapter(adapter);
                ColorDrawable dividerDrawable = new ColorDrawable(0x00000000) {
                    @Override
                    public int getIntrinsicHeight() {
                        return DisplayUtil.dip2px(CollectedActivity.this, 8);
                    }

                    @Override
                    public int getIntrinsicWidth() {
                        return DisplayUtil.dip2px(CollectedActivity.this, 8);
                    }
                };
                RecyclerGridDecoration itemDecoration = new RecyclerGridDecoration(this,
                        dividerDrawable, true);
                mangaRcv.addItemDecoration(itemDecoration);
            } else {
                adapter.setList(collectedMangaList);
                adapter.notifyDataSetChanged();
            }
            totalCollectTv.setText(collectedMangaList.size() + "");
        } catch (Exception e) {
        }
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }


    @Override
    public void onLoadMore() {
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onRefresh() {
        doGetData();
    }
}