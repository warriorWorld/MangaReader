package com.truthower.suhang.mangareader.business.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaRecyclerListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.base.BaseFragmentActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.service.BaseObserver;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class RecommendFragment extends BaseFragment implements
        OnRefreshListener, OnLoadMoreListener {
    private View emptyView;
    private View mainView;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();
    private TopBar topBar;

    private OnlineMangaRecyclerListAdapter adapter;
    private RecyclerView mangaRcv;
    private SwipeToLoadLayout swipeToLoadLayout;
    private SpiderBase spider;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.collect_manga_list, null);
        initUI(mainView);
        initGridView();
        doGetData();
        initSpider();
        return mainView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        try {
            if (!hidden) {
            }
        } catch (Exception e) {
            //这时候有可能fragment还没绑定上activity
        }
    }

    private void initSpider() {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.truthower.suhang.mangareader.spider." + BaseParameterUtil.getInstance().getCurrentWebSite(getActivity()) + "Spider").newInstance();
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

    private void doGetData() {
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        AVQuery<AVObject> query = new AVQuery<>("Recommend");
        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    mangaList = new ArrayList<MangaBean>();
                    if (null != list && list.size() > 0) {
                        MangaBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new MangaBean();
                            item.setName(list.get(i).getString("recommend"));
                            item.setWebThumbnailUrl(list.get(i).getString("thumbnailUrl"));
                            item.setUrl(list.get(i).getString("mangaUrl"));
                            mangaList.add(item);
                        }
                    }
                    initGridView();
                }
            }
        });
    }

    private void initGridView() {
        try {
            if (null == mangaList || mangaList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new OnlineMangaRecyclerListAdapter(getActivity(), mangaList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), WebMangaDetailsActivity.class);
                        intent.putExtra("mangaUrl", mangaList.get(position).getUrl());
                        startActivity(intent);
                    }
                });
                mangaRcv.setAdapter(adapter);
                ColorDrawable dividerDrawable = new ColorDrawable(0x00000000) {
                    @Override
                    public int getIntrinsicHeight() {
                        return DisplayUtil.dip2px(getActivity(), 8);
                    }

                    @Override
                    public int getIntrinsicWidth() {
                        return DisplayUtil.dip2px(getActivity(), 8);
                    }
                };
                RecyclerGridDecoration itemDecoration = new RecyclerGridDecoration(getActivity(),
                        dividerDrawable, true);
                mangaRcv.addItemDecoration(itemDecoration);
            } else {
                adapter.setList(mangaList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }


    private void initUI(View v) {
        swipeToLoadLayout = (SwipeToLoadLayout) v.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        mangaRcv = (RecyclerView) v.findViewById(R.id.swipe_target);
        mangaRcv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mangaRcv.setFocusableInTouchMode(false);
        mangaRcv.setFocusable(false);
        mangaRcv.setHasFixedSize(true);
        emptyView = v.findViewById(R.id.empty_view);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGetData();
            }
        });

        topBar = (TopBar) v.findViewById(R.id.gradient_bar);
        topBar.setTitle("推荐");
        if (LoginBean.getInstance().isCreator()) {
            topBar.setRightText("修复缩略图");
        } else {
            topBar.setRightText("");
        }
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
                if (LoginBean.getInstance().isCreator()) {
                    repairThumbnail();
                }
            }

            @Override
            public void onTitleClick() {
                if (Configure.isTest) {
                }
            }
        });
    }

    private void repairThumbnail() {
        Observable.fromIterable(mangaList)
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
                .subscribe(new BaseObserver<MangaBean>((BaseFragmentActivity) getActivity()) {

                    @Override
                    public void onNext(MangaBean value) {
//                        baseToast.showToast(value.getName());
                        if (value.isMangaDetailLoadSuccess())
                            modifyThumbnailUrl(value);
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
                        if (LoginBean.getInstance().isMaster()) {
                            BaseParameterUtil.getInstance().saveCurrentWebSite(getActivity(), Configure.masterWebsList[trySpiderPosition]);
                        } else {
                            BaseParameterUtil.getInstance().saveCurrentWebSite(getActivity(), Configure.websList[trySpiderPosition]);
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

    private void modifyThumbnailUrl(final MangaBean mangaBean) {
        AVQuery<AVObject> query1 = new AVQuery<>("Recommend");
        query1.whereEqualTo("mangaUrl", mangaBean.getUrl());

        query1.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    if (null != list && list.size() > 0) {
                        //已存在的保存
                        AVObject object = AVObject.createWithoutData("Recommend", list.get(0).getObjectId());
                        object.put("thumbnailUrl", mangaBean.getWebThumbnailUrl());
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                                    baseToast.showToast(mangaBean.getName() + "修复缩略图成功!");
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onLoadMore() {
        doGetData();
    }

    @Override
    public void onRefresh() {
        doGetData();
    }
}
