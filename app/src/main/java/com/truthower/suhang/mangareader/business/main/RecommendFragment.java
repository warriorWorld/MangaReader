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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaRecyclerListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.base.BaseFragmentActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.service.BaseObserver;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.PermissionUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;

import java.util.ArrayList;

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
        MangaBean item=new MangaBean();
        item.setWebThumbnailUrl("http://ww3.sinaimg.cn/mw600/006XNEY7gy1fup9fnneypj30j60i9mzy.jpg");
        item.setName("qq群782685214");
        item.setUrl("https://manganelo.com/manga/ookami_to_koushinryou");
        mangaList.add(item);
        MangaBean item1=new MangaBean();
        item1.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/45-read_one_piece_manga_online_free4.jpg");
        item1.setName("海贼王");
        item1.setUrl("https://manganelo.com/manga/read_one_piece_manga_online_free4");
        mangaList.add(item1);
        MangaBean item2=new MangaBean();
        item2.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/837-read_attack_on_titan_manga_online_free2.jpg");
        item2.setName("进击的巨人");
        item2.setUrl("https://manganelo.com/manga/read_attack_on_titan_manga_online_free2");
        mangaList.add(item2);
        MangaBean item3=new MangaBean();
        item3.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1459-ajin.jpg");
        item3.setName("亚人");
        item3.setUrl("https://manganelo.com/manga/ajin");
        mangaList.add(item3);
        MangaBean item4=new MangaBean();
        item4.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1083-read_berserk_manga_online.jpg");
        item4.setName("剑风传奇");
        item4.setUrl("https://manganelo.com/manga/read_berserk_manga_online");
        mangaList.add(item4);
        MangaBean item5=new MangaBean();
        item5.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3326-read_dragon_ball_manga_online_for_free2.jpg");
        item5.setName("龙珠");
        item5.setUrl("https://manganelo.com/manga/read_dragon_ball_manga_online_for_free2");
        mangaList.add(item5);
        MangaBean item6=new MangaBean();
        item6.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3482-read_slam_dunk_manga.jpg");
        item6.setName("灌篮高手");
        item6.setUrl("https://manganelo.com/manga/read_slam_dunk_manga");
        mangaList.add(item6);
        MangaBean item7=new MangaBean();
        item7.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3128-read_vagabond_manga.jpg");
        item7.setName("浪客行");
        item7.setUrl("https://manganelo.com/manga/read_vagabond_manga");
        mangaList.add(item7);
        MangaBean item8=new MangaBean();
        item8.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1203-read_naruto_manga_online_free3.jpg");
        item8.setName("火影忍者");
        item8.setUrl("https://manganelo.com/manga/read_naruto_manga_online_free3");
        mangaList.add(item8);
        MangaBean item9=new MangaBean();
        item9.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/17249-quan_zhi_gao_shou.jpg");
        item9.setName("全职高手");
        item9.setUrl("https://manganelo.com/manga/quan_zhi_gao_shou");
        mangaList.add(item9);
        MangaBean item10=new MangaBean();
        item10.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/6698-read_death_note_manga_online.jpg");
        item10.setName("死亡笔记");
        item10.setUrl("https://manganelo.com/manga/read_death_note_manga_online");
        mangaList.add(item10);
        MangaBean item11=new MangaBean();
        item11.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/431-read_detective_conan_manga_online_free.jpg");
        item11.setName("柯南");
        item11.setUrl("https://manganelo.com/manga/read_detective_conan_manga_online_free");
        mangaList.add(item11);
        MangaBean item12=new MangaBean();
        item12.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/503-feng_shen_ji.jpg");
        item12.setName("武庚纪(封神记)");
        item12.setUrl("https://manganelo.com/manga/feng_shen_ji");
        mangaList.add(item12);
        MangaBean item13=new MangaBean();
        item13.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/9816-read_gantz_manga.jpg");
        item13.setName("杀戮都市");
        item13.setUrl("https://manganelo.com/manga/read_gantz_manga");
        mangaList.add(item13);
        MangaBean item14=new MangaBean();
        item14.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/7061-devilman.jpg");
        item14.setName("恶魔人");
        item14.setUrl("https://manganelo.com/manga/devilman");
        mangaList.add(item14);
        MangaBean item15=new MangaBean();
        item15.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/18383-kinos_journey.jpg");
        item15.setName("奇诺之旅");
        item15.setUrl("https://manganelo.com/manga/kinos_journey");
        mangaList.add(item15);
        MangaBean item16=new MangaBean();
        item16.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/2032-read_bakuman_manga_online.jpg");
        item16.setName("爆漫王(食梦人)");
        item16.setUrl("https://manganelo.com/manga/read_bakuman_manga_online");
        mangaList.add(item16);
        MangaBean item17=new MangaBean();
        item17.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/4458-read_inuyasha_manga.jpg");
        item17.setName("犬夜叉");
        item17.setUrl("https://manganelo.com/manga/read_inuyasha_manga");
        mangaList.add(item17);
        MangaBean item18=new MangaBean();
        item18.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/7258-read_claymore_manga_online_free.jpg");
        item18.setName("大剑");
        item18.setUrl("https://manganelo.com/manga/read_claymore_manga_online_free");
        mangaList.add(item18);
        MangaBean item19=new MangaBean();
        item19.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/171-read_tenkuu_shinpan_manga.jpg");
        item19.setName("天空侵犯");
        item19.setUrl("https://manganelo.com/manga/read_tenkuu_shinpan_manga");
        mangaList.add(item19);
        MangaBean item20=new MangaBean();
        item20.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/349-read_green_worldz_manga.jpg");
        item20.setName("Green Worldz");
        item20.setUrl("https://manganelo.com/manga/read_green_worldz_manga");
        mangaList.add(item20);
        MangaBean item21=new MangaBean();
        item21.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/2693-i_am_a_hero.jpg");
        item21.setName("我是英雄");
        item21.setUrl("https://manganelo.com/manga/i_am_a_hero");
        mangaList.add(item21);
        MangaBean item22=new MangaBean();
        item22.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/8542-ookami_to_koushinryou.jpg");
        item22.setName("狼与香辛料");
        item22.setUrl("https://manganelo.com/manga/ookami_to_koushinryou");
        mangaList.add(item22);
        MangaBean item23=new MangaBean();
        item23.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/11563-kokou_no_hito.jpg");
        item23.setName("孤高之人");
        item23.setUrl("https://manganelo.com/manga/kokou_no_hito");
        mangaList.add(item23);
        MangaBean item24=new MangaBean();
        item24.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3794-read_fullmetal_alchemist_manga.jpg");
        item24.setName("钢之炼金术师");
        item24.setUrl("https://manganelo.com/manga/read_fullmetal_alchemist_manga");
        mangaList.add(item24);
        MangaBean item25=new MangaBean();
        item25.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/8550-initial_d.jpg");
        item25.setName("头文字D");
        item25.setUrl("https://manganelo.com/manga/initial_d");
        mangaList.add(item25);
        MangaBean item26=new MangaBean();
        item26.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/2295-weirdo.jpg");
        item26.setName("一人之下");
        item26.setUrl("https://manganelo.com/manga/weirdo");
        mangaList.add(item26);
        MangaBean item27=new MangaBean();
        item27.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/418-inu_yashiki.jpg");
        item27.setName("犬屋敷");
        item27.setUrl("https://manganelo.com/manga/inu_yashiki");
        mangaList.add(item27);
        MangaBean item28=new MangaBean();
        item28.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/19762-kuroko_no_basket.jpg");
        item28.setName("魔法篮球");
        item28.setUrl("https://manganelo.com/manga/kuroko_no_basket");
        mangaList.add(item28);
        MangaBean item29=new MangaBean();
        item29.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/17489-blades_of_the_guardians.jpg");
        item29.setName("镖人");
        item29.setUrl("https://manganelo.com/manga/blades_of_the_guardians");
        mangaList.add(item29);
        MangaBean item30=new MangaBean();
        item30.setWebThumbnailUrl("");
        item30.setName("");
        item30.setUrl("");
        mangaList.add(item30);

        initGridView();
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
        if (PermissionUtil.isCreator(getActivity())) {
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
                if (PermissionUtil.isCreator(getActivity())) {
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
                        if (PermissionUtil.isMaster(getActivity())) {
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
//        AVQuery<AVObject> query1 = new AVQuery<>("Recommend");
//        query1.whereEqualTo("mangaUrl", mangaBean.getUrl());
//
//        query1.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
//                    if (null != list && list.size() > 0) {
//                        //已存在的保存
//                        AVObject object = AVObject.createWithoutData("Recommend", list.get(0).getObjectId());
//                        object.put("thumbnailUrl", mangaBean.getWebThumbnailUrl());
//                        object.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(AVException e) {
//                                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
//                                    baseToast.showToast(mangaBean.getName() + "修复缩略图成功!");
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
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
