package com.truthower.suhang.mangareader.business.detail;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OneShotDetailsAdapter;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailAdapter;
import com.truthower.suhang.mangareader.base.TTSActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.business.main.MainActivity;
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity;
import com.truthower.suhang.mangareader.business.rxdownload.CommonDownloader;
import com.truthower.suhang.mangareader.business.rxdownload.DownloadCaretaker;
import com.truthower.suhang.mangareader.business.rxdownload.FailedPageCaretaker;
import com.truthower.suhang.mangareader.business.search.SearchActivity;
import com.truthower.suhang.mangareader.business.threadpooldownload.TpDownloadActivity;
import com.truthower.suhang.mangareader.business.threadpooldownload.TpDownloadService;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.db.DbAdapter;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.eventbus.JumpEvent;
import com.truthower.suhang.mangareader.eventbus.TagClickEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnResultListener;
import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.ActivityPoor;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.PermissionUtil;
import com.truthower.suhang.mangareader.utils.ServiceUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.UltimateTextSizeUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.GestureDialog;
import com.truthower.suhang.mangareader.widget.dialog.ListDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.gesture.GestureLockViewGroup;
import com.truthower.suhang.mangareader.widget.popupwindow.EasyPopupWindow;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.SingleSelectorDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class WebMangaDetailsActivity extends TTSActivity implements AdapterView.OnItemClickListener, View.OnClickListener,
        EasyPermissions.PermissionCallbacks, View.OnLongClickListener {
    private SpiderBase spider;
    private GridView mangaGV;
    private boolean chooseing = false;//判断是否在选择状态
    private boolean firstChoose = true;
    private int downloadStartPoint = 0;
    private MangaBean currentManga;
    private OnlineMangaDetailAdapter adapter;
    private OneShotDetailsAdapter oneShotAdapter;
    private ImageView thumbnailIV;
    private TextView mangaNameTv, mangaAuthorTv, mangaTypeTv, lastUpdateTv;
    private String[] optionsList = {"下载全部", "区间下载"};
    private ProgressDialog loadBar;
    private String mangaUrl;
    private SingleSelectorDialog tagSelector, authorSelector;
    //one shot 直接获取到了所有图片的地址
    private ArrayList<String> oneShotPathList = new ArrayList<String>();
    //因为我不知道当期收藏的漫画是哪个网站的 所以就一个个试
    private int trySpiderPosition = 0;
    private String currentMangaName;
    private String[] authorOptions;
    private View collectV;
    private boolean isCollected = false;
    private DbAdapter db;//数据库
    private SwipeRefreshLayout bgSrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbAdapter(this);
        Intent intent = getIntent();
        mangaUrl = intent.getStringExtra("mangaUrl");
        if (TextUtils.isEmpty(mangaUrl)) {
            this.finish();
        }
        initSpider();

        initUI();
        initProgressBar();
        initWebManga(mangaUrl);
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TUTORIAL, true)) {
            MangaDialog dialog = new MangaDialog(this);
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("1,点击五角星可收藏" +
                    "\n2,点击左上角漫画封面可显示漫画介绍" +
                    "\n3,点击右上角图标可以下载漫画" +
                    "\n4,点击漫画作者可以按漫画作者搜索漫画" +
                    "\n5,点击漫画类型可以按漫画类型搜索漫画" +
                    "\n6,点击下方按钮给漫画评论和评分了");
        }
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.LAST_READ_MANGA_URL,
                mangaUrl);
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

    private void initProgressBar() {
        loadBar = new ProgressDialog(WebMangaDetailsActivity.this);
        loadBar.setCancelable(true);
        loadBar.setMessage("加载中...");
    }

    private void initWebManga(final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (WebMangaDetailsActivity.this.isFinishing()) {
                    return;
                }
                loadBar.show();
                spider.getMangaDetail(url, new JsoupCallBack<MangaBean>() {
                    @Override
                    public void loadSucceed(final MangaBean result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadBar.dismiss();
                                currentManga = result;
                                refreshUI();
//                                    toggleDownload();
                                showDescription();
                                if (spider.isOneShot() && null != result.getChapters() && result.getChapters().size() > 0
                                        && !TextUtils.isEmpty(result.getChapters().get(0).getImgUrl())) {
                                    for (int i = 0; i < result.getChapters().size(); i++) {
                                        oneShotPathList.add(result.getChapters().get(i).getImgUrl());
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void loadFailed(String error) {
                        loadBar.dismiss();
                        if (error.equals(Configure.WRONG_WEBSITE_EXCEPTION)) {
                            try {
                                if (PermissionUtil.isMaster(WebMangaDetailsActivity.this)) {
                                    BaseParameterUtil.getInstance().saveCurrentWebSite(WebMangaDetailsActivity.this, Configure.masterWebsList[trySpiderPosition]);
                                } else {
                                    BaseParameterUtil.getInstance().saveCurrentWebSite(WebMangaDetailsActivity.this, Configure.websList[trySpiderPosition]);
                                }
                                initSpider();
                                initWebManga(url);
                                trySpiderPosition++;
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    private void refreshUI() {
        if (null == currentManga) {
            return;
        }
        baseTopBar.setTitle(currentManga.getName());
        ImageLoader.getInstance().displayImage(currentManga.getWebThumbnailUrl(), thumbnailIV, Configure.smallImageOptions);
        if (spider.isOneShot()) {
            mangaNameTv.setVisibility(View.GONE);
        } else {
            mangaNameTv.setVisibility(View.VISIBLE);
            mangaNameTv.setText("漫画名称:" + currentManga.getName());
        }
        if (!TextUtils.isEmpty(currentManga.getAuthor())) {
            mangaAuthorTv.setVisibility(View.VISIBLE);
            mangaAuthorTv.setText(UltimateTextSizeUtil.getEmphasizedSpannableString
                    ("作者:" + currentManga.getAuthor(), currentManga.getAuthor(), 0,
                            getResources().getColor(R.color.manga_reader), 0));
            authorOptions = currentManga.getAuthor().split(",");
        } else {
            mangaAuthorTv.setVisibility(View.GONE);
        }
        String mangaTags = "";
        for (int i = 0; i < currentManga.getTypes().length; i++) {
            //漫画类型
            mangaTags = mangaTags + " " + currentManga.getTypes()[i];
        }
        mangaTypeTv.setText(UltimateTextSizeUtil.getEmphasizedSpannableString
                ("类型:" + mangaTags, mangaTags, 0,
                        getResources().getColor(R.color.manga_reader), 0));
        if (!TextUtils.isEmpty(currentManga.getLast_update())) {
            lastUpdateTv.setVisibility(View.VISIBLE);
            lastUpdateTv.setText("最后更新:" + currentManga.getLast_update());
        } else {
            lastUpdateTv.setVisibility(View.GONE);
        }

        if (spider.isOneShot()) {
            initOneShotGridView();
        } else {
            initGridView();
            adapter.setLastReadPosition(SharedPreferencesUtils.getIntSharedPreferencesData
                    (WebMangaDetailsActivity.this,
                            ShareKeys.ONLINE_MANGA_READ_CHAPTER_POSITION + currentManga.getName()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doGetIsCollected();
    }

    private void doGetIsCollected() {
        isCollected = db.queryCollectExist(mangaUrl);
        toggleCollect();
    }

    private void doCollect() {
        db.insertCollect(currentManga.getName(), mangaUrl, currentManga.getWebThumbnailUrl());
        baseToast.showToast("收藏成功");
        doGetIsCollected();
    }

    private void deleteCollected() {
        db.deleteCollect(mangaUrl);
        baseToast.showToast("取消收藏");
        isCollected = false;
        toggleCollect();
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new OnlineMangaDetailAdapter(this, currentManga.getChapters());
            mangaGV.setAdapter(adapter);
            mangaGV.setColumnWidth(50);
            if (Configure.isPad) {
                mangaGV.setNumColumns(8);
            } else {
                mangaGV.setNumColumns(5);
            }
            mangaGV.setVerticalSpacing(10);
            mangaGV.setHorizontalSpacing(3);
            mangaGV.setOnItemClickListener(this);
        } else {
            adapter.setChapters(currentManga.getChapters());
            adapter.notifyDataSetChanged();
        }
        bgSrl.setRefreshing(false);
    }

    private void initOneShotGridView() {
        if (null == oneShotAdapter) {
            oneShotAdapter = new OneShotDetailsAdapter(this, currentManga.getChapters());
            mangaGV.setAdapter(oneShotAdapter);
            mangaGV.setOnItemClickListener(this);
            mangaGV.setNumColumns(2);
            mangaGV.setVerticalSpacing(12);
            mangaGV.setGravity(Gravity.CENTER);
            mangaGV.setHorizontalSpacing(15);
        } else {
            oneShotAdapter.setChapterList(currentManga.getChapters());
            oneShotAdapter.notifyDataSetChanged();
        }
        bgSrl.setRefreshing(false);
    }

    private void initUI() {
        mangaGV = (GridView) findViewById(R.id.ptf_grid_view);
        mangaGV.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (null == bgSrl) {
                    return;
                }
                if (firstVisibleItem == 0) {
                    bgSrl.setEnabled(true);
                } else {
                    bgSrl.setEnabled(false);
                }
            }
        });
        thumbnailIV = (ImageView) findViewById(R.id.thumbnail);
        mangaNameTv = (TextView) findViewById(R.id.manga_name);
        mangaAuthorTv = (TextView) findViewById(R.id.manga_author);
        mangaTypeTv = (TextView) findViewById(R.id.manga_type);
        bgSrl = findViewById(R.id.bg_srl);
        bgSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initWebManga(mangaUrl);
            }
        });
        lastUpdateTv = (TextView) findViewById(R.id.manga_update_date);
        collectV = findViewById(R.id.collect_view);
        mangaTypeTv.setOnClickListener(this);
        thumbnailIV.setOnClickListener(this);
        thumbnailIV.setOnLongClickListener(this);
        mangaAuthorTv.setOnClickListener(this);
        collectV.setOnClickListener(this);
        baseTopBar.setRightBackground(R.drawable.more);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
                if (isForAdult()) {
                    showGestureDialog(new OnResultListener() {
                        @Override
                        public void onFinish() {
                            showOptionsSelectorDialog();
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
                } else {
                    showOptionsSelectorDialog();
                }
            }

            @Override
            public void onTitleClick() {

            }

            @Override
            public void onLeftClick() {
                WebMangaDetailsActivity.this.finish();
            }
        });
    }

    @Override
    public void onEventMainThread(EventBusEvent event) {
        if (null == event) {
            return;
        }
        Intent intent = null;
        switch (event.getEventType()) {
            case EventBusEvent.TO_LAST_CHAPTER:
                if (spider.isOneShot()) {
                    return;
                }
                if (adapter.getLastReadPosition() > 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //readactivity是singletask 先结束后打开
                            toReadActivity(adapter.getLastReadPosition() - 1);
                        }
                    }, 500);
                } else {
                    baseToast.showToast("已经是第一章");
                }
                break;
            case EventBusEvent.TO_NEXT_CHAPTER:
                if (spider.isOneShot()) {
                    return;
                }
                if (adapter.getLastReadPosition() < currentManga.getChapters().size() - 1) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //readactivity是singletask 先结束后打开
                            toReadActivity(adapter.getLastReadPosition() + 1);
                        }
                    }, 500);
                } else {
                    baseToast.showToast("已经是最后一章");
                }
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manga_details_web;
    }

    private boolean isForAdult() {
        boolean isForAdult = false;
        if (null != spider.getAdultTypes() && spider.getAdultTypes().length > 0) {
            for (String item : spider.getAdultTypes()) {
                if (mangaTypeTv.getText().toString().toLowerCase().contains(item.toLowerCase())) {
                    isForAdult = true;
                    break;
                }
            }
        }
        return isForAdult;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (chooseing) {
            if (firstChoose) {
                baseToast.showToast("请点击下载终点!");
                downloadStartPoint = position;
                firstChoose = false;
            } else {
                doDownload(downloadStartPoint, position);
            }
        } else {
            if (isForAdult()) {
                showGestureDialog(new OnResultListener() {
                    @Override
                    public void onFinish() {
                        toReadActivity(position);
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            } else {
                toReadActivity(position);
            }
        }
    }

    private void toReadActivity(int position) {
        Intent intent = new Intent(WebMangaDetailsActivity.this, ReadMangaActivity.class);
        if (spider.isOneShot() && null != oneShotPathList && oneShotPathList.size() > 0) {
            //one shot
            currentMangaName = currentManga.getName();
            Bundle pathListBundle = new Bundle();
            pathListBundle.putSerializable("pathList", oneShotPathList);
            intent.putExtras(pathListBundle);
            intent.putExtra("img_position", position);
        } else {
            currentMangaName = currentManga.getName() + "(" + currentManga.getChapters().
                    get(position).getChapterPosition() + ")";
            SharedPreferencesUtils.setSharedPreferencesData(WebMangaDetailsActivity.this,
                    ShareKeys.ONLINE_MANGA_READ_CHAPTER_POSITION + currentManga.getName(), position);
            adapter.setLastReadPosition(SharedPreferencesUtils.getIntSharedPreferencesData
                    (WebMangaDetailsActivity.this,
                            ShareKeys.ONLINE_MANGA_READ_CHAPTER_POSITION + currentManga.getName()));
            intent.putExtra("chapterUrl", currentManga.getChapters().get(position).getChapterUrl());
        }
        intent.putExtra("currentMangaName", currentMangaName);
        if (null != intent) {
            startActivity(intent);
        }
    }

    private void showTagsSelector() {
        if (null == tagSelector) {
            tagSelector = new SingleSelectorDialog(this);
            tagSelector.setCancelable(true);
        }
        tagSelector.setOnSingleSelectedListener(new SingleSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                JumpEvent jumpEvent = new JumpEvent(EventBusEvent.JUMP_EVENT);
                jumpEvent.setJumpPoint(0);
                TagClickEvent tagClickEvent = new TagClickEvent(EventBusEvent.TAG_CLICK_EVENT);
                selectedRes = selectedRes.toLowerCase();
                selectedRes = selectedRes.replaceAll(" ", "-");
                tagClickEvent.setSelectTag(selectedRes);
                if (!TextUtils.isEmpty(selectedCodeRes)) {
                    tagClickEvent.setSelectCode(selectedCodeRes);
                }
                EventBus.getDefault().post(jumpEvent);
                EventBus.getDefault().post(tagClickEvent);
                ActivityPoor.finishAllActivityButThis(MainActivity.class);
            }

            @Override
            public void onOkBtnClick(int position) {

            }
        });
        tagSelector.show();
        tagSelector.setWheelViewTitle("选择标签");
        if (null != currentManga.getTypeCodes() && currentManga.getTypeCodes().length > 0) {
            tagSelector.initOptionsData(currentManga.getTypes(), currentManga.getTypeCodes());
        } else {
            tagSelector.initOptionsData(currentManga.getTypes());
        }
    }

    private void showAuthorSelector() {
        if (null == authorSelector) {
            authorSelector = new SingleSelectorDialog(this);
            authorSelector.setCancelable(true);
        }
        authorSelector.setOnSingleSelectedListener(new SingleSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                Intent intent = new Intent(WebMangaDetailsActivity.this, SearchActivity.class);
                intent.putExtra("selectedWebSite", BaseParameterUtil.getInstance().getCurrentWebSite(WebMangaDetailsActivity.this));
                intent.putExtra("searchType", "author");
                intent.putExtra("keyWord", selectedRes);
                intent.putExtra("immediateSearch", true);
                startActivity(intent);
            }

            @Override
            public void onOkBtnClick(int position) {

            }
        });
        authorSelector.show();
        authorSelector.setWheelViewTitle("选择作者");
        authorSelector.initOptionsData(authorOptions);
    }

    private void downloadAll() {
        doDownload(0, currentManga.getChapters().size() - 1);
    }

//    @AfterPermissionGranted(Configure.PERMISSION_FILE_REQUST_CODE)
//    private void doDownload(int start, int end) {
//        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        if (EasyPermissions.hasPermissions(this, perms)) {
//            // Already have permission, do the thing
//            // ...
//            baseToast.showToast("开始下载!");
//            DownloadMangaManager.getInstance().reset(this);
//            MangaBean temp = currentManga;
//            ArrayList<ChapterBean> chapters = new ArrayList<>();
//            for (int i = start; i <= end; i++) {
//                ChapterBean item = new ChapterBean();
//                item = currentManga.getChapters().get(i);
//                chapters.add(item);
//            }
//            temp.setChapters(chapters);
//            DownloadBean.getInstance().setMangaBean(this, temp);
//            DownloadBean.getInstance().setOne_shot(this, spider.isOneShot());
//            DownloadBean.getInstance().initDownloadChapters();
//            DownloadBean.getInstance().setWebSite(this, BaseParameterUtil.getInstance().getCurrentWebSite(this));
//            DownloadMangaManager.getInstance().doDownload(getApplicationContext());
//
//            Intent intent = new Intent(this, DownloadActivity.class);
//            startActivity(intent);
//            WebMangaDetailsActivity.this.finish();
//        } else {
//            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
//                    Configure.PERMISSION_FILE_REQUST_CODE, perms);
//        }
//    }

    @AfterPermissionGranted(Configure.PERMISSION_FILE_REQUST_CODE)
    private void doDownload(int start, int end) {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            baseToast.showToast("开始下载!");
            Intent stopIntent = new Intent(this, TpDownloadService.class);
            if (ServiceUtil.isServiceWork(this,
                    TpDownloadService.SERVICE_PCK_NAME)) {
                //先结束
                stopService(stopIntent);
            }
            DownloadCaretaker.clean(this);
            FailedPageCaretaker.clean(this);
            RxDownloadBean downloadBean = new RxDownloadBean();
            downloadBean.setDownloader(new CommonDownloader(spider));
            downloadBean.setMangaName(currentManga.getName());
            downloadBean.setMangaUrl(currentManga.getUrl());
            downloadBean.setThumbnailUrl(currentManga.getWebThumbnailUrl());
            downloadBean.setChapterCount(end - start + 1);
            ArrayList<RxDownloadChapterBean> chapters = new ArrayList<>();
            for (int i = start; i <= end; i++) {
                RxDownloadChapterBean item = new RxDownloadChapterBean();
                item.setChapterUrl(currentManga.getChapters().get(i).getChapterUrl());
                item.setChapterName((i + 1) + "");
                chapters.add(item);
            }
            downloadBean.setChapters(chapters);
            DownloadCaretaker.saveDownloadMemoto(this, downloadBean);

            Intent serviceIntent = new Intent(this, TpDownloadService.class);

            serviceIntent.putExtra("downloadBean", downloadBean);
            //重新打开
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }

            Intent intent = new Intent(this, TpDownloadActivity.class);
            startActivity(intent);
            WebMangaDetailsActivity.this.finish();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    Configure.PERMISSION_FILE_REQUST_CODE, perms);
        }
    }

    private void showOptionsSelectorDialog() {
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
                        downloadAll();
                        break;
                    case 1:
                        chooseing = true;
                        firstChoose = true;
                        baseToast.showToast("请点击下载起点!");
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(optionsList);
    }

    private void showGestureDialog(final OnResultListener listener) {
        final GestureDialog gestureDialog = new GestureDialog(this);
        gestureDialog.show();
        gestureDialog.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {
            @Override
            public void onBlockSelected(int cId) {

            }

            @Override
            public void onGestureEvent(boolean matched) {

            }

            @Override
            public void onGestureEvent(String choose) {
                if (choose.equals("7485")) {
                    listener.onFinish();
                } else {
                    listener.onFailed();
                }
                gestureDialog.dismiss();
            }

            @Override
            public void onUnmatchedExceedBoundary() {

            }
        });
    }

    private void showDescription() {
        String description = currentManga.getDescription();
        for (int i = 0; i < Configure.VPN_MUST_LIST.length; i++) {
            if (BaseParameterUtil.getInstance().getCurrentWebSite(this).equals(Configure.VPN_MUST_LIST[i])) {
                description = "该漫画需要开启VPN后浏览";
                break;
            }
        }
        if (TextUtils.isEmpty(description)) {
            return;
        }
        EasyPopupWindow ppw = new EasyPopupWindow(this);
        ppw.adaptiveShowAsDropDown(thumbnailIV, 0, 0);
        ppw.setMessage(description);
        ppw.hideIKnowTv();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_view:
                if (isCollected) {
                    deleteCollected();
                } else {
                    doCollect();
                }
                break;
            case R.id.manga_type:
                showTagsSelector();
                break;
            case R.id.thumbnail:
                showDescription();
                break;
            case R.id.manga_author:
                showAuthorSelector();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        try {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        } catch (RuntimeException e) {
            //RuntimeException: Cannot execute method doDownload because it is non-void method and/or has input parameters
            //google这个东西没法调用带参数的方法
            MangaDialog dialog = new MangaDialog(WebMangaDetailsActivity.this);
            dialog.show();
            dialog.setTitle("已获得授权,请重新点击下载.");
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        baseToast.showToast("没文件读取/写入授权,你让我怎么下载漫画?", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
    }

    private void toggleCollect() {
        if (isCollected) {
            collectV.setBackgroundResource(R.drawable.collected);
        } else {
            collectV.setBackgroundResource(R.drawable.collect);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.thumbnail:
                text2Speech(currentManga.getDescription());
                break;
            case R.id.collect_view:
                break;
        }
        return true;
    }
}
