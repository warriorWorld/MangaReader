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
        MangaBean item1 = new MangaBean();
        item1.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/45-read_one_piece_manga_online_free4.jpg");
        item1.setName("海贼王");
        item1.setUrl("https://manganelo.com/manga/read_one_piece_manga_online_free4");
        mangaList.add(item1);
        MangaBean item2 = new MangaBean();
        item2.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/837-read_attack_on_titan_manga_online_free2.jpg");
        item2.setName("进击的巨人");
        item2.setUrl("https://manganelo.com/manga/kxqh9261558062112");
        mangaList.add(item2);
        MangaBean item3 = new MangaBean();
        item3.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1459-ajin.jpg");
        item3.setName("亚人");
        item3.setUrl("https://manganelo.com/manga/ajin");
        mangaList.add(item3);
        MangaBean item81 = new MangaBean();
        item81.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/16363-kimetsu_no_yaiba.jpg");
        item81.setName("鬼灭之刃");
        item81.setUrl("https://manganelo.com/manga/kimetsu_no_yaiba");
        mangaList.add(item81);
        MangaBean item4 = new MangaBean();
        item4.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1083-read_berserk_manga_online.jpg");
        item4.setName("剑风传奇");
        item4.setUrl("https://manganelo.com/manga/ilsi12001567132882");
        mangaList.add(item4);
        MangaBean item5 = new MangaBean();
        item5.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3326-read_dragon_ball_manga_online_for_free2.jpg");
        item5.setName("龙珠");
        item5.setUrl("https://manganelo.com/manga/read_dragon_ball_manga_online_for_free2");
        mangaList.add(item5);
        MangaBean item6 = new MangaBean();
        item6.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3482-read_slam_dunk_manga.jpg");
        item6.setName("灌篮高手");
        item6.setUrl("https://manganelo.com/manga/read_slam_dunk_manga");
        mangaList.add(item6);
        MangaBean item7 = new MangaBean();
        item7.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3128-read_vagabond_manga.jpg");
        item7.setName("浪客行");
        item7.setUrl("https://manganelo.com/manga/read_vagabond_manga");
        mangaList.add(item7);
        MangaBean item8 = new MangaBean();
        item8.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1203-read_naruto_manga_online_free3.jpg");
        item8.setName("火影忍者");
        item8.setUrl("https://manganelo.com/manga/read_naruto_manga_online_free3");
        mangaList.add(item8);
        MangaBean item9 = new MangaBean();
        item9.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/17249-quan_zhi_gao_shou.jpg");
        item9.setName("全职高手");
        item9.setUrl("https://manganelo.com/manga/quan_zhi_gao_shou");
        mangaList.add(item9);
        MangaBean item10 = new MangaBean();
        item10.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/6698-read_death_note_manga_online.jpg");
        item10.setName("死亡笔记");
        item10.setUrl("https://manganelo.com/manga/read_death_note_manga_online");
        mangaList.add(item10);
        MangaBean item11 = new MangaBean();
        item11.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/431-read_detective_conan_manga_online_free.jpg");
        item11.setName("柯南");
        item11.setUrl("https://manganelo.com/manga/read_detective_conan_manga_online_free");
        mangaList.add(item11);
        MangaBean item12 = new MangaBean();
        item12.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/503-feng_shen_ji.jpg");
        item12.setName("武庚纪(封神记)");
        item12.setUrl("https://manganelo.com/manga/feng_shen_ji");
        mangaList.add(item12);
        MangaBean item13 = new MangaBean();
        item13.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/9816-read_gantz_manga.jpg");
        item13.setName("杀戮都市");
        item13.setUrl("https://manganelo.com/manga/read_gantz_manga");
        mangaList.add(item13);
        MangaBean item14 = new MangaBean();
        item14.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/7061-devilman.jpg");
        item14.setName("恶魔人");
        item14.setUrl("https://manganelo.com/manga/devilman");
        mangaList.add(item14);
        MangaBean item15 = new MangaBean();
        item15.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/18383-kinos_journey.jpg");
        item15.setName("奇诺之旅");
        item15.setUrl("https://manganelo.com/manga/kinos_journey");
        mangaList.add(item15);
        MangaBean item16 = new MangaBean();
        item16.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/2032-read_bakuman_manga_online.jpg");
        item16.setName("爆漫王(食梦人)");
        item16.setUrl("https://manganelo.com/manga/read_bakuman_manga_online");
        mangaList.add(item16);
        MangaBean item17 = new MangaBean();
        item17.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/4458-read_inuyasha_manga.jpg");
        item17.setName("犬夜叉");
        item17.setUrl("https://manganelo.com/manga/read_inuyasha_manga");
        mangaList.add(item17);
        MangaBean item18 = new MangaBean();
        item18.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/7258-read_claymore_manga_online_free.jpg");
        item18.setName("大剑");
        item18.setUrl("https://manganelo.com/manga/read_claymore_manga_online_free");
        mangaList.add(item18);
        MangaBean item19 = new MangaBean();
        item19.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/171-read_tenkuu_shinpan_manga.jpg");
        item19.setName("天空侵犯");
        item19.setUrl("https://manganelo.com/manga/read_tenkuu_shinpan_manga");
        mangaList.add(item19);
        MangaBean item20 = new MangaBean();
        item20.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/349-read_green_worldz_manga.jpg");
        item20.setName("Green Worldz");
        item20.setUrl("https://manganelo.com/manga/read_green_worldz_manga");
        mangaList.add(item20);
        MangaBean item21 = new MangaBean();
        item21.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/2693-i_am_a_hero.jpg");
        item21.setName("我是英雄");
        item21.setUrl("https://manganelo.com/manga/i_am_a_hero");
        mangaList.add(item21);
        MangaBean item22 = new MangaBean();
        item22.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/8542-ookami_to_koushinryou.jpg");
        item22.setName("狼与香辛料");
        item22.setUrl("https://manganelo.com/manga/tifr98811554781969");
        mangaList.add(item22);
        MangaBean item23 = new MangaBean();
        item23.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/11563-kokou_no_hito.jpg");
        item23.setName("孤高之人");
        item23.setUrl("https://manganelo.com/manga/kokou_no_hito");
        mangaList.add(item23);
        MangaBean item24 = new MangaBean();
        item24.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3794-read_fullmetal_alchemist_manga.jpg");
        item24.setName("钢之炼金术师");
        item24.setUrl("https://manganelo.com/manga/read_fullmetal_alchemist_manga");
        mangaList.add(item24);
        MangaBean item25 = new MangaBean();
        item25.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/8550-initial_d.jpg");
        item25.setName("头文字D");
        item25.setUrl("https://manganelo.com/manga/cvcn98891556768005");
        mangaList.add(item25);
        MangaBean item26 = new MangaBean();
        item26.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/2295-weirdo.jpg");
        item26.setName("一人之下");
        item26.setUrl("https://manganelo.com/manga/weirdo");
        mangaList.add(item26);
        MangaBean item27 = new MangaBean();
        item27.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/418-inu_yashiki.jpg");
        item27.setName("犬屋敷");
        item27.setUrl("https://manganelo.com/manga/inu_yashiki");
        mangaList.add(item27);
        MangaBean item28 = new MangaBean();
        item28.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/19762-kuroko_no_basket.jpg");
        item28.setName("魔法篮球");
        item28.setUrl("https://manganelo.com/manga/kuroko_no_basket");
        mangaList.add(item28);
        MangaBean item29 = new MangaBean();
        item29.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/17489-blades_of_the_guardians.jpg");
        item29.setName("镖人");
        item29.setUrl("https://manganelo.com/manga/blades_of_the_guardians");
        mangaList.add(item29);
        MangaBean item30 = new MangaBean();
        item30.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/9005-phantom_blood.jpg");
        item30.setName("JOJO1");
        item30.setUrl("https://manganelo.com/manga/phantom_blood");
        mangaList.add(item30);
        MangaBean item31 = new MangaBean();
        item31.setWebThumbnailUrl("https://s4.mangareader.net/cover/jojos-bizarre-adventure-part-2-battle-tendency/jojos-bizarre-adventure-part-2-battle-tendency-l0.jpg");
        item31.setName("JOJO2");
        item31.setUrl("https://www.mangareader.net/jojos-bizarre-adventure-part-2-battle-tendency");
        mangaList.add(item31);
        MangaBean item32 = new MangaBean();
        item32.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/18892-jojos_bizarre_adventure.jpg");
        item32.setName("JOJO3");
        item32.setUrl("https://manganelo.com/manga/jojos_bizarre_adventure");
        mangaList.add(item32);
        MangaBean item33 = new MangaBean();
        item33.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/17580-diamond_wa_kudakenai.jpg");
        item33.setName("JOJO4");
        item33.setUrl("https://manganelo.com/manga/diamond_wa_kudakenai");
        mangaList.add(item33);
        MangaBean item34 = new MangaBean();
        item34.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/92-vento_aureo.jpg");
        item34.setName("JOJO5");
        item34.setUrl("https://manganelo.com/manga/vento_aureo");
        mangaList.add(item34);
        MangaBean item35 = new MangaBean();
        item35.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/21930-bi918289.jpg");
        item35.setName("JOJO6");
        item35.setUrl("https://manganelo.com/manga/bi918289");
        mangaList.add(item35);
        MangaBean item36 = new MangaBean();
        item36.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/21779-af918141.jpg");
        item36.setName("JOJO7");
        item36.setUrl("https://manganelo.com/manga/af918141");
        mangaList.add(item36);
        MangaBean item37 = new MangaBean();
        item37.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/21552-yx917940.jpg");
        item37.setName("JOJO8");
        item37.setUrl("https://manganelo.com/manga/yx917940");
        mangaList.add(item37);
        MangaBean item38 = new MangaBean();
        item38.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/7864-akira.jpg");
        item38.setName("Akira");
        item38.setUrl("https://manganelo.com/manga/akira");
        mangaList.add(item38);
        MangaBean item39 = new MangaBean();
        item39.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/16959-goblin_slayer.jpg");
        item39.setName("哥布林杀手");
        item39.setUrl("https://manganelo.com/manga/hgj2047065412");
        mangaList.add(item39);
        MangaBean item40 = new MangaBean();
        item40.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1079-the_ravages_of_time.jpg");
        item40.setName("火凤燎原");
        item40.setUrl("https://manganelo.com/manga/the_ravages_of_time");
        mangaList.add(item40);
        MangaBean item41 = new MangaBean();
        item41.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/11452-pluto.jpg");
        item41.setName("Pluto");
        item41.setUrl("https://manganelo.com/manga/pluto");
        mangaList.add(item41);
        MangaBean item42 = new MangaBean();
        item42.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/422-read_one_punch_man_manga_online_free3.jpg");
        item42.setName("一拳超人");
        item42.setUrl("https://manganelo.com/manga/read_one_punch_man_manga_online_free3");
        mangaList.add(item42);
        MangaBean item43 = new MangaBean();
        item43.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1792-nichijou.jpg");
        item43.setName("日常");
        item43.setUrl("https://manganelo.com/manga/nichijou");
        mangaList.add(item43);
        MangaBean item44 = new MangaBean();
        item44.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1604-kono_subarashii_sekai_ni_shukufuku_o.jpg");
        item44.setName("为美好世界献上祝福");
        item44.setUrl("https://manganelo.com/manga/kono_subarashii_sekai_ni_shukufuku_o");
        mangaList.add(item44);
        MangaBean item45 = new MangaBean();
        item45.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/19980-gigant.jpg");
        item45.setName("GIGANT");
        item45.setUrl("https://manganelo.com/manga/gigant");
        mangaList.add(item45);
        MangaBean item46 = new MangaBean();
        item46.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/15418-kiseijuu.jpg");
        item46.setName("寄生兽");
        item46.setUrl("https://manganelo.com/manga/kiseijuu");
        mangaList.add(item46);
        MangaBean item47 = new MangaBean();
        item47.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/10421-battle_angel_alita_last_order.jpg");
        item47.setName("战斗天使艾丽塔");
        item47.setUrl("https://manganelo.com/manga/battle_angel_alita_last_order");
        mangaList.add(item47);
        MangaBean item48 = new MangaBean();
        item48.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/6388-hellsing.jpg");
        item48.setName("皇家国教骑士团");
        item48.setUrl("https://manganelo.com/manga/jzde76471556853820");
        mangaList.add(item48);
        MangaBean item49 = new MangaBean();
        item49.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/15407-yokohama_kaidashi_kikou.jpg");
        item49.setName("横滨购物记行");
        item49.setUrl("https://manganelo.com/manga/yokohama_kaidashi_kikou");
        mangaList.add(item49);
        MangaBean item50 = new MangaBean();
        item50.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/6789-team_medical_dragon.jpg");
        item50.setName("医龙");
        item50.setUrl("https://manganelo.com/manga/team_medical_dragon");
        mangaList.add(item50);
        MangaBean item51 = new MangaBean();
        item51.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/99-mob_psycho_100.jpg");
        item51.setName("灵能百分百");
        item51.setUrl("https://manganelo.com/manga/urvx11340213");
        mangaList.add(item51);
        MangaBean item52 = new MangaBean();
        item52.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1390-kakegurui.jpg");
        item52.setName("狂赌之渊");
        item52.setUrl("https://manganelo.com/manga/kakegurui");
        mangaList.add(item52);
        MangaBean item53 = new MangaBean();
        item53.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/17259-yakusoku_no_neverland.jpg");
        item53.setName("约定的梦幻岛");
        item53.setUrl("https://manganelo.com/manga/yakusoku_no_neverland");
        mangaList.add(item53);
        MangaBean item54 = new MangaBean();
        item54.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/4373-koe_no_katachi.jpg");
        item54.setName("声之形");
        item54.setUrl("https://manganelo.com/manga/ueb5218786");
        mangaList.add(item54);
        MangaBean item55 = new MangaBean();
        item55.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/17829-to_you_the_immortal.jpg");
        item55.setName("致不灭的你");
        item55.setUrl("https://manganelo.com/manga/to_you_the_immortal");
        mangaList.add(item55);
        MangaBean item56 = new MangaBean();
        item56.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1221-made_in_abyss.jpg");
        item56.setName("来自深渊");
        item56.setUrl("https://manganelo.com/manga/yovbxa13526492");
        mangaList.add(item56);
        MangaBean item57 = new MangaBean();
        item57.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/18678-journey_to_the_west.jpg");
        item57.setName("西行记");
        item57.setUrl("https://manganelo.com/manga/journey_to_the_west");
        mangaList.add(item57);
        MangaBean item58 = new MangaBean();
        item58.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3480-read_hunter_x_hunter_manga_online_free2.jpg");
        item58.setName("全职猎人");
        item58.setUrl("https://manganelo.com/manga/read_hunter_x_hunter_manga_online_free2");
        mangaList.add(item58);
        MangaBean item59 = new MangaBean();
        item59.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/8927-tetsuwan_girl.jpg");
        item59.setName("铁腕女投手");
        item59.setUrl("https://manganelo.com/manga/tetsuwan_girl");
        mangaList.add(item59);
        MangaBean item60 = new MangaBean();
        item60.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/407-gekkan_shoujo_nozakikun.jpg");
        item60.setName("月刊少女野崎君");
        item60.setUrl("https://manganelo.com/manga/gekkan_shoujo_nozakikun");
        mangaList.add(item60);
        MangaBean item61 = new MangaBean();
        item61.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/155-saiki_kusuo_no_sainan.jpg");
        item61.setName("齐木楠雄的灾难");
        item61.setUrl("https://manganelo.com/manga/saiki_kusuo_no_sainan");
        mangaList.add(item61);
        MangaBean item62 = new MangaBean();
        item62.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3948-cowboy_bebop.jpg");
        item62.setName("宇宙牛仔");
        item62.setUrl("https://manganelo.com/manga/cowboy_bebop");
        mangaList.add(item62);
        MangaBean item63 = new MangaBean();
        item63.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/16301-koukaku_kidoutai.jpg");
        item63.setName("攻壳机动队");
        item63.setUrl("https://manganelo.com/manga/koukaku_kidoutai");
        mangaList.add(item63);
        MangaBean item64 = new MangaBean();
        item64.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/3554-read_nanatsu_no_taizai_manga_online_free.jpg");
        item64.setName("七大罪");
        item64.setUrl("https://manganelo.com/manga/read_nanatsu_no_taizai_manga_online_free");
        mangaList.add(item64);
        MangaBean item65 = new MangaBean();
        item65.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/315-read_world_trigger_manga_online.jpg");
        item65.setName("世界触发者");
        item65.setUrl("https://manganelo.com/manga/read_world_trigger_manga_online");
        mangaList.add(item65);
        MangaBean item66 = new MangaBean();
        item66.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/8471-zetman.jpg");
        item66.setName("超魔人");
        item66.setUrl("https://manganelo.com/manga/zetman");
        mangaList.add(item66);
        MangaBean item67 = new MangaBean();
        item67.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/11620-jackals.jpg");
        item67.setName("Jackals");
        item67.setUrl("https://manganelo.com/manga/jackals");
        mangaList.add(item67);
        MangaBean item68 = new MangaBean();
        item68.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1164-read_akame_ga_kill_manga.jpg");
        item68.setName("斬·赤紅之瞳!");
        item68.setUrl("https://manganelo.com/manga/read_akame_ga_kill_manga");
        mangaList.add(item68);
        MangaBean item69 = new MangaBean();
        item69.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1530-kaiji.jpg");
        item69.setName("赌博默示录");
        item69.setUrl("https://manganelo.com/manga/kaiji");
        mangaList.add(item69);
        MangaBean item70 = new MangaBean();
        item70.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/17783-its_not_my_fault_that_im_not_popular.jpg");
        item70.setName("我不受欢迎这件事,怎么想都不是我的错.");
        item70.setUrl("https://manganelo.com/manga/its_not_my_fault_that_im_not_popular");
        mangaList.add(item70);
        MangaBean item71 = new MangaBean();
        item71.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/8640-jiraishin.jpg");
        item71.setName("地雷震");
        item71.setUrl("https://manganelo.com/manga/jiraishin");
        mangaList.add(item71);
        MangaBean item72 = new MangaBean();
        item72.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/259-rainbow.jpg");
        item72.setName("二舍六房的七人");
        item72.setUrl("https://manganelo.com/manga/rainbow");
        mangaList.add(item72);
        MangaBean item73 = new MangaBean();
        item73.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/7538-read_black_lagoon.jpg");
        item73.setName("黑礁");
        item73.setUrl("https://manganelo.com/manga/read_black_lagoon");
        mangaList.add(item73);
        MangaBean item74 = new MangaBean();
        item74.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/8009-20th_century_boys.jpg");
        item74.setName("20世纪少年");
        item74.setUrl("https://manganelo.com/manga/20th_century_boys");
        mangaList.add(item74);
        MangaBean item75 = new MangaBean();
        item75.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/1036-spirit_circle.jpg");
        item75.setName("魂环");
        item75.setUrl("https://manganelo.com/manga/spirit_circle");
        mangaList.add(item75);
        MangaBean item76 = new MangaBean();
        item76.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/5737-wakusei_no_samidare.jpg");
        item76.setName("The Lucifer and Biscuit Hammer");
        item76.setUrl("https://manganelo.com/manga/wakusei_no_samidare");
        mangaList.add(item76);
        MangaBean item77 = new MangaBean();
        item77.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/558-read_ansatsu_kyoushitsu_manga_online.jpg");
        item77.setName("暗杀教室");
        item77.setUrl("https://manganelo.com/manga/read_ansatsu_kyoushitsu_manga_online");
        mangaList.add(item77);
        MangaBean item78 = new MangaBean();
        item78.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/944-drifters.jpg");
        item78.setName("漂泊者");
        item78.setUrl("https://manganelo.com/manga/drifters");
        mangaList.add(item78);
        MangaBean item79 = new MangaBean();
        item79.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/455-read_sun_ken_rock_manga.jpg");
        item79.setName("最强不良传说");
        item79.setUrl("https://manganelo.com/manga/read_sun_ken_rock_manga");
        mangaList.add(item79);
        MangaBean item80 = new MangaBean();
        item80.setWebThumbnailUrl("https://avt.mkklcdnv3.com/avatar_225/17471-origin.jpg");
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
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }

    @Override
    public void onRefresh() {
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
    }
}
