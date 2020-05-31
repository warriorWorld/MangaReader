package com.truthower.suhang.mangareader.business.main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaRecyclerListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.onlinedetail.OnlineDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.PermissionUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
        MangaBean item1 = new MangaBean();
        item1.setWebThumbnailUrl("http://img4.imgtn.bdimg.com/it/u=2262005623,647407035&fm=26&gp=0.jpg");
        item1.setName("海贼王");
        item1.setUrl("https://manganelo.com/manga/read_one_piece_manga_online_free4");
        mangaList.add(item1);
        MangaBean item2 = new MangaBean();
        item2.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2583954982,2531790065&fm=15&gp=0.jpg");
        item2.setName("进击的巨人");
        item2.setUrl("https://manganelo.com/manga/kxqh9261558062112");
        mangaList.add(item2);
        MangaBean item3 = new MangaBean();
        item3.setWebThumbnailUrl("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2081382405,2055551310&fm=15&gp=0.jpg");
        item3.setName("亚人");
        item3.setUrl("https://manganelo.com/manga/ajin");
        mangaList.add(item3);
        MangaBean item81 = new MangaBean();
        item81.setWebThumbnailUrl("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2226986818,4191666315&fm=15&gp=0.jpg");
        item81.setName("鬼灭之刃");
        item81.setUrl("https://manganelo.com/manga/kimetsu_no_yaiba");
        mangaList.add(item81);
        MangaBean item4 = new MangaBean();
        item4.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4086181937,3100646167&fm=26&gp=0.jpg");
        item4.setName("剑风传奇");
        item4.setUrl("https://manganelo.com/manga/ilsi12001567132882");
        mangaList.add(item4);
        MangaBean item5 = new MangaBean();
        item5.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=351869670,2000132629&fm=26&gp=0.jpg");
        item5.setName("龙珠");
        item5.setUrl("https://manganelo.com/manga/read_dragon_ball_manga_online_for_free2");
        mangaList.add(item5);
        MangaBean item6 = new MangaBean();
        item6.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=4086315305,705072348&fm=26&gp=0.jpg");
        item6.setName("灌篮高手");
        item6.setUrl("https://manganelo.com/manga/read_slam_dunk_manga");
        mangaList.add(item6);
        MangaBean item7 = new MangaBean();
        item7.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588935300592&di=8cad0dc6c5eb8524eeeff04c9ca69bb6&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201809%2F08%2F20180908182401_gyqxn.png");
        item7.setName("浪客行");
        item7.setUrl("https://manganelo.com/manga/read_vagabond_manga");
        mangaList.add(item7);
        MangaBean item8 = new MangaBean();
        item8.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588935334408&di=6ed517b0caf75999791d6b0f7410d071&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fmobile%2F2019-02-18%2F5c6a6478c282f.jpg");
        item8.setName("火影忍者");
        item8.setUrl("https://manganelo.com/manga/read_naruto_manga_online_free3");
        mangaList.add(item8);
        MangaBean item9 = new MangaBean();
        item9.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1936942412,4271582878&fm=15&gp=0.jpg");
        item9.setName("全职高手");
        item9.setUrl("https://manganelo.com/manga/quan_zhi_gao_shou");
        mangaList.add(item9);
        MangaBean item10 = new MangaBean();
        item10.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2920150396,3714167875&fm=15&gp=0.jpg");
        item10.setName("死亡笔记");
        item10.setUrl("https://manganelo.com/manga/read_death_note_manga_online");
        mangaList.add(item10);
        MangaBean item11 = new MangaBean();
        item11.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2002025952,4043616348&fm=26&gp=0.jpg");
        item11.setName("柯南");
        item11.setUrl("https://manganelo.com/manga/read_detective_conan_manga_online_free");
        mangaList.add(item11);
        MangaBean item12 = new MangaBean();
        item12.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588935423957&di=4872888eed029393e9cc0065c009048d&imgtype=0&src=http%3A%2F%2Fimg3.doubanio.com%2Fview%2Fphoto%2Fl%2Fpublic%2Fp2503372860.jpg");
        item12.setName("武庚纪(封神记)");
        item12.setUrl("https://manganelo.com/manga/feng_shen_ji");
        mangaList.add(item12);
        MangaBean item13 = new MangaBean();
        item13.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3130076247,2560243500&fm=15&gp=0.jpg");
        item13.setName("杀戮都市");
        item13.setUrl("https://manganelo.com/manga/read_gantz_manga");
        mangaList.add(item13);
        MangaBean item14 = new MangaBean();
        item14.setWebThumbnailUrl("https://bkimg.cdn.bcebos.com/pic/6d81800a19d8bc3e4d749523898ba61ea9d345df?x-bce-process=image/resize,m_lfit,w_268,limit_1/format,f_jpg");
        item14.setName("恶魔人");
        item14.setUrl("https://manganelo.com/manga/devilman");
        mangaList.add(item14);
        MangaBean item15 = new MangaBean();
        item15.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1933530102,1391487007&fm=26&gp=0.jpg");
        item15.setName("奇诺之旅");
        item15.setUrl("https://manganelo.com/manga/kinos_journey");
        mangaList.add(item15);
        MangaBean item16 = new MangaBean();
        item16.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=468285406,3476241238&fm=26&gp=0.jpg");
        item16.setName("爆漫王(食梦人)");
        item16.setUrl("https://manganelo.com/manga/read_bakuman_manga_online");
        mangaList.add(item16);
        MangaBean item17 = new MangaBean();
        item17.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1942311875,364965099&fm=26&gp=0.jpg");
        item17.setName("犬夜叉");
        item17.setUrl("https://manganelo.com/manga/read_inuyasha_manga");
        mangaList.add(item17);
        MangaBean item18 = new MangaBean();
        item18.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3915460743,4290605783&fm=15&gp=0.jpg");
        item18.setName("大剑");
        item18.setUrl("https://manganelo.com/manga/read_claymore_manga_online_free");
        mangaList.add(item18);
        MangaBean item19 = new MangaBean();
        item19.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2863792081,3493600549&fm=26&gp=0.jpg");
        item19.setName("天空侵犯");
        item19.setUrl("https://manganelo.com/manga/read_tenkuu_shinpan_manga");
        mangaList.add(item19);
        MangaBean item20 = new MangaBean();
        item20.setWebThumbnailUrl("https://bkimg.cdn.bcebos.com/pic/d31b0ef41bd5ad6ecc6def9286cb39dbb6fd3c35?x-bce-process=image/watermark,g_7,image_d2F0ZXIvYmFpa2U4MA==,xp_5,yp_5");
        item20.setName("Green Worldz");
        item20.setUrl("https://manganelo.com/manga/read_green_worldz_manga");
        mangaList.add(item20);
        MangaBean item21 = new MangaBean();
        item21.setWebThumbnailUrl("http://mhfm4tel.cdndm5.com/8/7663/7663_h.jpg");
        item21.setName("我是英雄");
        item21.setUrl("https://manganelo.com/manga/i_am_a_hero");
        mangaList.add(item21);
        MangaBean item22 = new MangaBean();
        item22.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1557146869,1649297224&fm=26&gp=0.jpg");
        item22.setName("狼与香辛料");
        item22.setUrl("https://manganelo.com/manga/tifr98811554781969");
        mangaList.add(item22);
        MangaBean item23 = new MangaBean();
        item23.setWebThumbnailUrl("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1212929551,4074952812&fm=26&gp=0.jpg");
        item23.setName("孤高之人");
        item23.setUrl("https://manganelo.com/manga/kokou_no_hito");
        mangaList.add(item23);
        MangaBean item24 = new MangaBean();
        item24.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=446647188,1986425161&fm=26&gp=0.jpg");
        item24.setName("钢之炼金术师");
        item24.setUrl("https://manganelo.com/manga/read_fullmetal_alchemist_manga");
        mangaList.add(item24);
        MangaBean item25 = new MangaBean();
        item25.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588935817517&di=5faf308f4c56ff6026c9f4d66ef585c5&imgtype=0&src=http%3A%2F%2F03.imgmini.eastday.com%2Fmobile%2F20171219%2F20171219024558_c1a2c87d356090cd8975049378475fc3_5.png");
        item25.setName("头文字D");
        item25.setUrl("https://manganelo.com/manga/cvcn98891556768005");
        mangaList.add(item25);
        MangaBean item26 = new MangaBean();
        item26.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3339412564,3086912406&fm=15&gp=0.jpg");
        item26.setName("一人之下");
        item26.setUrl("https://manganelo.com/manga/weirdo");
        mangaList.add(item26);
        MangaBean item27 = new MangaBean();
        item27.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588935863136&di=39cd63631e1e92e38d72b49bb15fd406&imgtype=0&src=http%3A%2F%2Fwx3.sinaimg.cn%2Flarge%2F740ca5e5gy1ffujz5kmgyj20nm0xbafv.jpg");
        item27.setName("犬屋敷");
        item27.setUrl("https://manganelo.com/manga/inu_yashiki");
        mangaList.add(item27);
        MangaBean item28 = new MangaBean();
        item28.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1937061453,1850939440&fm=26&gp=0.jpg");
        item28.setName("魔法篮球");
        item28.setUrl("https://manganelo.com/manga/kuroko_no_basket");
        mangaList.add(item28);
        MangaBean item29 = new MangaBean();
        item29.setWebThumbnailUrl("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=2675451831,2447364466&fm=26&gp=0.jpg");
        item29.setName("镖人");
        item29.setUrl("https://manganelo.com/manga/blades_of_the_guardians");
        mangaList.add(item29);
        MangaBean item30 = new MangaBean();
        item30.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=400201165,1829301786&fm=26&gp=0.jpg");
        item30.setName("JOJO1");
        item30.setUrl("https://manganelo.com/manga/phantom_blood");
        mangaList.add(item30);
        MangaBean item31 = new MangaBean();
        item31.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=400201165,1829301786&fm=26&gp=0.jpg");
        item31.setName("JOJO2");
        item31.setUrl("https://www.mangareader.net/jojos-bizarre-adventure-part-2-battle-tendency");
        mangaList.add(item31);
        MangaBean item32 = new MangaBean();
        item32.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=400201165,1829301786&fm=26&gp=0.jpg");
        item32.setName("JOJO3");
        item32.setUrl("https://manganelo.com/manga/jojos_bizarre_adventure");
        mangaList.add(item32);
        MangaBean item33 = new MangaBean();
        item33.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=400201165,1829301786&fm=26&gp=0.jpg");
        item33.setName("JOJO4");
        item33.setUrl("https://manganelo.com/manga/diamond_wa_kudakenai");
        mangaList.add(item33);
        MangaBean item34 = new MangaBean();
        item34.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=400201165,1829301786&fm=26&gp=0.jpg");
        item34.setName("JOJO5");
        item34.setUrl("https://manganelo.com/manga/vento_aureo");
        mangaList.add(item34);
        MangaBean item35 = new MangaBean();
        item35.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=400201165,1829301786&fm=26&gp=0.jpg");
        item35.setName("JOJO6");
        item35.setUrl("https://manganelo.com/manga/bi918289");
        mangaList.add(item35);
        MangaBean item36 = new MangaBean();
        item36.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=400201165,1829301786&fm=26&gp=0.jpg");
        item36.setName("JOJO7");
        item36.setUrl("https://manganelo.com/manga/af918141");
        mangaList.add(item36);
        MangaBean item37 = new MangaBean();
        item37.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=400201165,1829301786&fm=26&gp=0.jpg");
        item37.setName("JOJO8");
        item37.setUrl("https://manganelo.com/manga/yx917940");
        mangaList.add(item37);
        MangaBean item38 = new MangaBean();
        item38.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588936076550&di=d4304ac17d657a92d05aaa566bee4713&imgtype=0&src=http%3A%2F%2F07imgmini.eastday.com%2Fmobile%2F20190706%2F20190706174326_95d1fb680923631eecd59f2b424fea9b_1.jpeg");
        item38.setName("Akira");
        item38.setUrl("https://manganelo.com/manga/akira");
        mangaList.add(item38);
        MangaBean item39 = new MangaBean();
        item39.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=801053967,3352561400&fm=26&gp=0.jpg");
        item39.setName("哥布林杀手");
        item39.setUrl("https://manganelo.com/manga/hgj2047065412");
        mangaList.add(item39);
        MangaBean item40 = new MangaBean();
        item40.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=270275802,977542596&fm=26&gp=0.jpg");
        item40.setName("火凤燎原");
        item40.setUrl("https://manganelo.com/manga/the_ravages_of_time");
        mangaList.add(item40);
        MangaBean item41 = new MangaBean();
        item41.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588936126077&di=cb7ad05056d7f7983a180123762acebd&imgtype=0&src=http%3A%2F%2Fimg9.doubanio.com%2Fview%2Fsubject%2Fl%2Fpublic%2Fs3171174.jpg");
        item41.setName("Pluto");
        item41.setUrl("https://manganelo.com/manga/pluto");
        mangaList.add(item41);
        MangaBean item42 = new MangaBean();
        item42.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1015280758,3844836180&fm=15&gp=0.jpg");
        item42.setName("一拳超人");
        item42.setUrl("https://manganelo.com/manga/read_one_punch_man_manga_online_free3");
        mangaList.add(item42);
        MangaBean item43 = new MangaBean();
        item43.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588936218380&di=428936f9bf4cd9d5a6be76d5edd12383&imgtype=0&src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farticle%2F8e77c74cc1500bf3dbbf4fb4f492a4a9c936b13b.jpg");
        item43.setName("日常");
        item43.setUrl("https://manganelo.com/manga/nichijou");
        mangaList.add(item43);
        MangaBean item44 = new MangaBean();
        item44.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588936238697&di=ac484ff081f5f420e2a5674512a1afc8&imgtype=0&src=http%3A%2F%2F00.minipic.eastday.com%2F20170206%2F20170206014706_bd2def4bb74b3f5899e36d388d2c5981_4.jpeg");
        item44.setName("为美好世界献上祝福");
        item44.setUrl("https://manganelo.com/manga/kono_subarashii_sekai_ni_shukufuku_o");
        mangaList.add(item44);
        MangaBean item45 = new MangaBean();
        item45.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1418893704,95461326&fm=26&gp=0.jpg");
        item45.setName("GIGANT");
        item45.setUrl("https://manganelo.com/manga/gigant");
        mangaList.add(item45);
        MangaBean item46 = new MangaBean();
        item46.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588936292336&di=3422aa203160514d95e3d4e8a492f95f&imgtype=0&src=http%3A%2F%2Fimgsrc.baidu.com%2Fforum%2Fw%3D580%2Fsign%3D29180d651ad8bc3ec60806c2b28aa6c8%2F7e7ca0d3fd1f4134d293bd21261f95cad0c85eaf.jpg");
        item46.setName("寄生兽");
        item46.setUrl("https://manganelo.com/manga/kiseijuu");
        mangaList.add(item46);
        MangaBean item47 = new MangaBean();
        item47.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588936312232&di=6a658c2ab05cdd8acb8b341e640eb86a&imgtype=0&src=http%3A%2F%2Fd-paper.i4.cn%2Fmax%2F2019%2F02%2F20%2F15%2F1550646950663_998162.jpg");
        item47.setName("战斗天使艾丽塔");
        item47.setUrl("https://manganelo.com/manga/battle_angel_alita_last_order");
        mangaList.add(item47);
        MangaBean item48 = new MangaBean();
        item48.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3487408384,1780262425&fm=26&gp=0.jpg");
        item48.setName("皇家国教骑士团");
        item48.setUrl("https://manganelo.com/manga/jzde76471556853820");
        mangaList.add(item48);
        MangaBean item49 = new MangaBean();
        item49.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2319285129,873424491&fm=26&gp=0.jpg");
        item49.setName("横滨购物记行");
        item49.setUrl("https://manganelo.com/manga/yokohama_kaidashi_kikou");
        mangaList.add(item49);
        MangaBean item50 = new MangaBean();
        item50.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=1324509594,1346996934&fm=26&gp=0.jpg");
        item50.setName("医龙");
        item50.setUrl("https://manganelo.com/manga/team_medical_dragon");
        mangaList.add(item50);
        MangaBean item51 = new MangaBean();
        item51.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1606008730,617806086&fm=26&gp=0.jpg");
        item51.setName("灵能百分百");
        item51.setUrl("https://manganelo.com/manga/urvx11340213");
        mangaList.add(item51);
        MangaBean item52 = new MangaBean();
        item52.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1603376028,3160142060&fm=26&gp=0.jpg");
        item52.setName("狂赌之渊");
        item52.setUrl("https://manganelo.com/manga/kakegurui");
        mangaList.add(item52);
        MangaBean item53 = new MangaBean();
        item53.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1396053136,2337459199&fm=26&gp=0.jpg");
        item53.setName("约定的梦幻岛");
        item53.setUrl("https://manganelo.com/manga/yakusoku_no_neverland");
        mangaList.add(item53);
        MangaBean item54 = new MangaBean();
        item54.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3422572099,2804975441&fm=26&gp=0.jpg");
        item54.setName("声之形");
        item54.setUrl("https://manganelo.com/manga/ueb5218786");
        mangaList.add(item54);
        MangaBean item55 = new MangaBean();
        item55.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2854023161,2606517751&fm=26&gp=0.jpg");
        item55.setName("致不灭的你");
        item55.setUrl("https://manganelo.com/manga/to_you_the_immortal");
        mangaList.add(item55);
        MangaBean item56 = new MangaBean();
        item56.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1233691816,456702705&fm=26&gp=0.jpg");
        item56.setName("来自深渊");
        item56.setUrl("https://manganelo.com/manga/yovbxa13526492");
        mangaList.add(item56);
        MangaBean item57 = new MangaBean();
        item57.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2375853316,1657182897&fm=26&gp=0.jpg");
        item57.setName("西行记");
        item57.setUrl("https://manganelo.com/manga/journey_to_the_west");
        mangaList.add(item57);
        MangaBean item58 = new MangaBean();
        item58.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3832516470,1763729500&fm=15&gp=0.jpg");
        item58.setName("全职猎人");
        item58.setUrl("https://manganelo.com/manga/read_hunter_x_hunter_manga_online_free2");
        mangaList.add(item58);
        MangaBean item59 = new MangaBean();
        item59.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2408533325,1728296867&fm=26&gp=0.jpg");
        item59.setName("铁腕女投手");
        item59.setUrl("https://manganelo.com/manga/tetsuwan_girl");
        mangaList.add(item59);
        MangaBean item60 = new MangaBean();
        item60.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1918585003,646730037&fm=26&gp=0.jpg");
        item60.setName("月刊少女野崎君");
        item60.setUrl("https://manganelo.com/manga/gekkan_shoujo_nozakikun");
        mangaList.add(item60);
        MangaBean item61 = new MangaBean();
        item61.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=569466545,3210954072&fm=26&gp=0.jpg");
        item61.setName("齐木楠雄的灾难");
        item61.setUrl("https://manganelo.com/manga/saiki_kusuo_no_sainan");
        mangaList.add(item61);
        MangaBean item62 = new MangaBean();
        item62.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1244247376,3018966307&fm=26&gp=0.jpg");
        item62.setName("宇宙牛仔");
        item62.setUrl("https://manganelo.com/manga/cowboy_bebop");
        mangaList.add(item62);
        MangaBean item63 = new MangaBean();
        item63.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2746810111,2764469141&fm=26&gp=0.jpg");
        item63.setName("攻壳机动队");
        item63.setUrl("https://manganelo.com/manga/koukaku_kidoutai");
        mangaList.add(item63);
        MangaBean item64 = new MangaBean();
        item64.setWebThumbnailUrl("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3122948923,3249775644&fm=26&gp=0.jpg");
        item64.setName("七大罪");
        item64.setUrl("https://manganelo.com/manga/read_nanatsu_no_taizai_manga_online_free");
        mangaList.add(item64);
        MangaBean item65 = new MangaBean();
        item65.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1258531227,437352821&fm=26&gp=0.jpg");
        item65.setName("世界触发者");
        item65.setUrl("https://manganelo.com/manga/read_world_trigger_manga_online");
        mangaList.add(item65);
        MangaBean item66 = new MangaBean();
        item66.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588936795013&di=fe97ced6f8dfb47138ffd10bb3a1b0ef&imgtype=0&src=http%3A%2F%2Fa.hiphotos.baidu.com%2Fbaike%2Fw%253D268%2Fsign%3D1507a86c9058d109c4e3aeb4e958ccd0%2Fcc11728b4710b91200af3decc1fdfc03924522b2.jpg");
        item66.setName("超魔人");
        item66.setUrl("https://manganelo.com/manga/zetman");
        mangaList.add(item66);
        MangaBean item67 = new MangaBean();
        item67.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2370845168,1183901258&fm=26&gp=0.jpg");
        item67.setName("Jackals");
        item67.setUrl("https://manganelo.com/manga/jackals");
        mangaList.add(item67);
        MangaBean item68 = new MangaBean();
        item68.setWebThumbnailUrl("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=378319780,1310589443&fm=15&gp=0.jpg");
        item68.setName("斬·赤紅之瞳!");
        item68.setUrl("https://manganelo.com/manga/read_akame_ga_kill_manga");
        mangaList.add(item68);
        MangaBean item69 = new MangaBean();
        item69.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588936912777&di=d4fa07a5a3ec90593a4df1eb1419370a&imgtype=0&src=http%3A%2F%2Fimg3.doubanio.com%2Fview%2Fthing_review%2Fl%2Fpublic%2Fp1644995.jpg");
        item69.setName("赌博默示录");
        item69.setUrl("https://manganelo.com/manga/kaiji");
        mangaList.add(item69);
        MangaBean item70 = new MangaBean();
        item70.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588936994129&di=754610d42701ff93013592b71814a8cb&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201808%2F27%2F20180827195446_wgqhq.thumb.700_0.jpg");
        item70.setName("我不受欢迎这件事,怎么想都不是我的错.");
        item70.setUrl("https://manganelo.com/manga/its_not_my_fault_that_im_not_popular");
        mangaList.add(item70);
        MangaBean item71 = new MangaBean();
        item71.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3127651426,533995719&fm=26&gp=0.jpg");
        item71.setName("地雷震");
        item71.setUrl("https://manganelo.com/manga/jiraishin");
        mangaList.add(item71);
        MangaBean item72 = new MangaBean();
        item72.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1842566619,647875915&fm=26&gp=0.jpg");
        item72.setName("二舍六房的七人");
        item72.setUrl("https://manganelo.com/manga/rainbow");
        mangaList.add(item72);
        MangaBean item73 = new MangaBean();
        item73.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588937053237&di=e37366e1c12d231425db5ed56367592b&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201202%2F09%2F20120209212226_fNzB2.jpg");
        item73.setName("黑礁");
        item73.setUrl("https://manganelo.com/manga/read_black_lagoon");
        mangaList.add(item73);
        MangaBean item74 = new MangaBean();
        item74.setWebThumbnailUrl("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=4086171016,4239911824&fm=26&gp=0.jpg");
        item74.setName("20世纪少年");
        item74.setUrl("https://manganelo.com/manga/20th_century_boys");
        mangaList.add(item74);
        MangaBean item75 = new MangaBean();
        item75.setWebThumbnailUrl("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3766127228,2162622052&fm=26&gp=0.jpg");
        item75.setName("魂环");
        item75.setUrl("https://manganelo.com/manga/spirit_circle");
        mangaList.add(item75);
        MangaBean item76 = new MangaBean();
        item76.setWebThumbnailUrl("https://cdn.myanimelist.net/images/manga/3/56421.jpg");
        item76.setName("The Lucifer and Biscuit Hammer");
        item76.setUrl("https://manganelo.com/manga/wakusei_no_samidare");
        mangaList.add(item76);
        MangaBean item77 = new MangaBean();
        item77.setWebThumbnailUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1588937220812&di=ce321b66a408aa81c1b1b8f7bf32826a&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201507%2F09%2F20150709195925_MLHkF.jpeg");
        item77.setName("暗杀教室");
        item77.setUrl("https://manganelo.com/manga/read_ansatsu_kyoushitsu_manga_online");
        mangaList.add(item77);
        MangaBean item78 = new MangaBean();
        item78.setWebThumbnailUrl("https://mhpics.caisangji.com/upload2/1753/2018/03-20/20180320183151_0953teyxpykq_small.jpg");
        item78.setName("漂泊者");
        item78.setUrl("https://manganelo.com/manga/drifters");
        mangaList.add(item78);
        MangaBean item79 = new MangaBean();
        item79.setWebThumbnailUrl("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1139679291,1054708034&fm=26&gp=0.jpg");
        item79.setName("最强不良传说");
        item79.setUrl("https://manganelo.com/manga/read_sun_ken_rock_manga");
        mangaList.add(item79);
        MangaBean item80 = new MangaBean();
        item80.setWebThumbnailUrl("http://mhfm2tel.cdndm5.com/41/40547/20180128163808_360x480_40.jpg");
        item80.setName("Origin");
        item80.setUrl("https://manganelo.com/manga/origin");
        mangaList.add(item80);

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
                        Intent intent = new Intent(getActivity(), OnlineDetailsActivity.class);
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
            e.printStackTrace();
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
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRightClick() {
            }

            @Override
            public void onTitleClick() {
                if (Configure.isTest) {
                }
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
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onRefresh() {
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }
}
