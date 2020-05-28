package com.truthower.suhang.mangareader.business.onlinedetail;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailsRecyclerAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.RxDownloadBean;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.business.main.MainActivity;
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity;
import com.truthower.suhang.mangareader.business.rxdownload.CommonDownloader;
import com.truthower.suhang.mangareader.business.rxdownload.DownloadCaretaker;
import com.truthower.suhang.mangareader.business.search.SearchActivity;
import com.truthower.suhang.mangareader.business.threadpooldownload.TpDownloadActivity;
import com.truthower.suhang.mangareader.business.threadpooldownload.TpDownloadService;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.eventbus.JumpEvent;
import com.truthower.suhang.mangareader.eventbus.TagClickEvent;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnResultListener;
import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.ActivityPoor;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.ServiceUtil;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.UltimateTextSizeUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.GestureDialog;
import com.truthower.suhang.mangareader.widget.dialog.ListDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.gesture.GestureLockViewGroup;
import com.truthower.suhang.mangareader.widget.popupwindow.EasyPopupWindow;
import com.truthower.suhang.mangareader.widget.recyclerview.SpaceItemDecoration;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.SingleSelectorDialog;

import org.greenrobot.eventbus.EventBus;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class OnlineDetailsActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private RelativeLayout mangaInfoRl;
    private ImageView thumbnailIv;
    private TextView nameTv;
    private TextView authorTv;
    private TextView typeTv;
    private TextView updateTimeTv;
    private View collectView;
    private RecyclerView ptfGridView;
    private SwipeRefreshLayout detailsSrl;
    private OnlineDetailVM mOnlineDetailVM;
    private MangaBean currentManga;
    private OnlineMangaDetailsRecyclerAdapter adapter;
    private String mangaUrl;
    private boolean chooseing = false;//判断是否在选择状态
    private boolean firstChoose = true;
    private String[] optionsList = {"下载全部", "区间下载", "缓存详情", "清空缓存", "导入缓存", "导出缓存"};
    private int downloadStartPoint = 0;
    private SparseArray<RxDownloadChapterBean> cacheChapters = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mangaUrl = intent.getStringExtra("mangaUrl");
        if (TextUtils.isEmpty(mangaUrl)) {
            this.finish();
        }
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.LAST_READ_MANGA_URL,
                mangaUrl);
        initUI();
        initViewModel();
        mOnlineDetailVM.getMangaDetails(mangaUrl, true);
        mOnlineDetailVM.getIsCollected(mangaUrl);
    }

    private void initUI() {
        mangaInfoRl = (RelativeLayout) findViewById(R.id.manga_info_rl);
        thumbnailIv = (ImageView) findViewById(R.id.thumbnail_iv);
        nameTv = (TextView) findViewById(R.id.name_tv);
        authorTv = (TextView) findViewById(R.id.author_tv);
        typeTv = (TextView) findViewById(R.id.type_tv);
        updateTimeTv = (TextView) findViewById(R.id.update_time_tv);
        detailsSrl = findViewById(R.id.detail_srl);
        detailsSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mOnlineDetailVM.getMangaDetails(mangaUrl);
            }
        });
        collectView = (View) findViewById(R.id.collect_view);
        ptfGridView = (RecyclerView) findViewById(R.id.ptf_grid_view);
        int column;
        if (Configure.isPad) {
            column = 8;
        } else {
            column = 5;
        }
        // 减掉RecyclerView父布局两侧padding和item的宽度，然后平分，默认每个item右侧会填充剩余空间
        int spaceWidth = (DisplayUtil.getScreenWidth(this) -
                (int) (DisplayUtil.dip2px(this, 20)) -
                (int) (DisplayUtil.dip2px(this, 60) * column)) / (column * (column - 1));
        ptfGridView.addItemDecoration(new SpaceItemDecoration(spaceWidth, column));

        ptfGridView.setLayoutManager(new GridLayoutManager(this, column));
        ptfGridView.setFocusableInTouchMode(false);
        ptfGridView.setFocusable(false);
        ptfGridView.setHasFixedSize(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ptfGridView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (null == detailsSrl) {
                        return;
                    }
                    if (scrollY == 0) {
                        detailsSrl.setEnabled(true);
                    } else {
                        detailsSrl.setEnabled(false);
                    }
                }
            });
        }
        collectView.setOnClickListener(this);
        typeTv.setOnClickListener(this);
        authorTv.setOnClickListener(this);
        thumbnailIv.setOnClickListener(this);
        updateTimeTv.setOnClickListener(this);
        baseTopBar.setRightBackground(R.drawable.more);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
                if (mOnlineDetailVM.getIsForAdult()) {
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
                tempRepairCache();
            }

            @Override
            public void onLeftClick() {
                OnlineDetailsActivity.this.finish();
            }
        });
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
                    case 2:
                        mOnlineDetailVM.cacheDetail();
                        baseToast.showToast("缓存完成");
                        break;
                    case 3:
                        showCleanCacheDialog();
                        break;
                    case 4:
                        showFileChooser();
                        break;
                    case 5:
                        mOnlineDetailVM.exportCache();
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(optionsList);
    }

    /**
     * 调用文件选择软件来选择文件
     **/
    public void showFileChooser() {
        baseToast.showToast("请选择.mangaCachew文件");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("text/plain");//设置类型和后缀 txt
        intent.setType("*/*");//设置类型和后缀  全部文件
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            switch (requestCode) {
                case 1:
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    String path = null;
                    try {
                        path = FileSpider.getInstance().getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(path)) {
                        return;
                    }
                    assert path != null;
                    if (path.endsWith(".mangaCache")) {
                        try {
                            ArrayList<RxDownloadChapterBean> cacheArray =
                                    (ArrayList<RxDownloadChapterBean>) FileSpider.getInstance().readObjFromSDCard(path);
                            ShareObjUtil.saveObject(OnlineDetailsActivity.this, cacheArray,
                                    currentManga.getName() + ShareKeys.BRIDGE_KEY);
                            initCacheData();
                            initRec();
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                    } else {
                        baseToast.showToast("请选择.mangaCache文件");
                    }
                    break;
            }
        }
    }

    private void showCleanCacheDialog() {
        MangaDialog dialog = new MangaDialog(this);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                mOnlineDetailVM.cleanAllCache();
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("确定清空缓存?");
        dialog.setOkText("确定");
        dialog.setCancelText("算了");
    }

    private void downloadAll() {
        doDownload(0, currentManga.getChapters().size() - 1);
    }

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
            RxDownloadBean downloadBean = new RxDownloadBean();
            downloadBean.setDownloader(new CommonDownloader(mOnlineDetailVM.getSpider()));
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
            this.finish();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    Configure.PERMISSION_FILE_REQUST_CODE, perms);
        }
    }

    @Override
    public void onEventMainThread(EventBusEvent event) {
        if (null == event) {
            return;
        }
        Intent intent = null;
        switch (event.getEventType()) {
            case EventBusEvent.TO_LAST_CHAPTER:
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
            case EventBusEvent.DOWNLOAD_CHAPTER_FINISH_EVENT:
                initRec();
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    private void toReadActivity(int position) {
        Intent intent = new Intent(this, ReadMangaActivity.class);
        String currentMangaName = currentManga.getName() + "(" + currentManga.getChapters().
                get(position).getChapterPosition() + ")";
        SharedPreferencesUtils.setSharedPreferencesData(this,
                ShareKeys.ONLINE_MANGA_READ_CHAPTER_POSITION + currentManga.getName(), position);
        adapter.setLastReadPosition(position);
        RxDownloadChapterBean chapterBean = cacheChapters.get(Integer.valueOf(currentManga.getChapters().get(position).getChapterPosition()));
        if (null != chapterBean) {
            intent.putExtra("chapterBean", chapterBean);
        } else {
            intent.putExtra("chapterUrl", currentManga.getChapters().get(position).getChapterUrl());
        }
        intent.putExtra("currentMangaName", currentMangaName);
        startActivity(intent);
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

    private void initViewModel() {
//        mOnlineDetailVM = ViewModelProviders.of(this).get(OnlineDetailVM.class);
        mOnlineDetailVM = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new OnlineDetailVM(OnlineDetailsActivity.this);
            }
        }).get(OnlineDetailVM.class);
        mOnlineDetailVM.getManga().observe(this, new Observer<MangaBean>() {
            @Override
            public void onChanged(MangaBean bean) {
                currentManga = bean;
                initCacheData();
                refreshUI();
                showDescription();
            }
        });
        mOnlineDetailVM.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                baseToast.showToast(s);
            }
        });
        mOnlineDetailVM.getAuthorOptions().observe(this, new Observer<String[]>() {
            @Override
            public void onChanged(String[] strings) {

            }
        });
        mOnlineDetailVM.getIsCollected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    collectView.setBackgroundResource(R.drawable.collected);
                } else {
                    collectView.setBackgroundResource(R.drawable.collect);
                }
            }
        });
        mOnlineDetailVM.getIsUpdating().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    detailsSrl.setRefreshing(true);
                } else {
                    detailsSrl.setRefreshing(false);
                }
            }
        });
    }

    private void initCacheData() {
        ArrayList<RxDownloadChapterBean> cacheArray = (ArrayList<RxDownloadChapterBean>) ShareObjUtil.getObject(
                OnlineDetailsActivity.this, currentManga.getName()
                        + ShareKeys.BRIDGE_KEY);
        if (null != cacheArray) {
            for (int i = 0; i < cacheArray.size(); i++) {
                cacheChapters.put(Integer.valueOf(cacheArray.get(i).getChapterName()), cacheArray.get(i));
            }
        }
    }

    private void tempRepairCache() {
//        ArrayList<RxDownloadChapterBean> cacheArray = (ArrayList<RxDownloadChapterBean>) ShareObjUtil.getObject(
//                OnlineDetailsActivity.this, currentManga.getName()
//                        + ShareKeys.BRIDGE_KEY);
//        if (null != cacheArray) {
//            for (int i = 0; i < cacheArray.size(); i++) {
//                ArrayList<RxDownloadPageBean> pages = cacheArray.get(i).getPages();
//                for (int j = 0; j < pages.size() - 1; j++) {
//                    long l0 = getLFromUrl(pages.get(j).getPageUrl());
//                    long l1 = getLFromUrl(pages.get(j + 1).getPageUrl());
//                    if (l0 >= l1) {
//                        long res = l0 + 1;
//                        String url = pages.get(j + 1).getPageUrl().replace(l1 + ".jpg", res + ".jpg");
//                        pages.get(j + 1).setPageUrl(url);
//                    }
//                }
//                cacheArray.get(i).setPages(pages);
//            }
//            ShareObjUtil.saveObject(OnlineDetailsActivity.this, cacheArray, currentManga.getName() + ShareKeys.BRIDGE_KEY);
//        }
    }

    private long getLFromUrl(String url) {
        String[] ss = url.split("-");
        String s = ss[ss.length - 1];
        s = s.replaceAll(".jpg", "");
        return Long.valueOf(s);
    }

    private void refreshUI() {
        if (null == currentManga) {
            return;
        }
        baseTopBar.setTitle(currentManga.getName());
        ImageLoader.getInstance().displayImage(currentManga.getWebThumbnailUrl(), thumbnailIv, Configure.smallImageOptions);
        nameTv.setText("漫画名称:" + currentManga.getName());
        if (!TextUtils.isEmpty(currentManga.getAuthor())) {
            authorTv.setVisibility(View.VISIBLE);
            authorTv.setText(UltimateTextSizeUtil.getEmphasizedSpannableString
                    ("作者:" + currentManga.getAuthor(), currentManga.getAuthor(), 0,
                            getResources().getColor(R.color.manga_reader), 0));
        } else {
            authorTv.setVisibility(View.GONE);
        }
        String mangaTags = "";
        for (int i = 0; i < currentManga.getTypes().length; i++) {
            //漫画类型
            mangaTags = mangaTags + " " + currentManga.getTypes()[i];
        }
        typeTv.setText(UltimateTextSizeUtil.getEmphasizedSpannableString
                ("类型:" + mangaTags, mangaTags, 0,
                        getResources().getColor(R.color.manga_reader), 0));
        if (!TextUtils.isEmpty(currentManga.getLast_update())) {
            updateTimeTv.setVisibility(View.VISIBLE);
            updateTimeTv.setText("最后更新:" + currentManga.getLast_update());
        } else {
            updateTimeTv.setVisibility(View.GONE);
        }

        initRec();
        adapter.setLastReadPosition(SharedPreferencesUtils.getIntSharedPreferencesData
                (this, ShareKeys.ONLINE_MANGA_READ_CHAPTER_POSITION + currentManga.getName()));
    }

    private void showTagsSelector() {
        SingleSelectorDialog tagSelector = new SingleSelectorDialog(this);
        tagSelector.setCancelable(true);
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
        SingleSelectorDialog authorSelector = new SingleSelectorDialog(this);
        authorSelector.setCancelable(true);
        authorSelector.setOnSingleSelectedListener(new SingleSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
                Intent intent = new Intent(OnlineDetailsActivity.this, SearchActivity.class);
                intent.putExtra("selectedWebSite", BaseParameterUtil.getInstance().getCurrentWebSite(OnlineDetailsActivity.this));
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
        authorSelector.initOptionsData(currentManga.getAuthor().split(","));
    }

    private void showDescription() {
        final String description = currentManga.getDescription();
        if (TextUtils.isEmpty(description)) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EasyPopupWindow ppw = new EasyPopupWindow(OnlineDetailsActivity.this);
                ppw.adaptiveShowAsDropDown(thumbnailIv, 0, 0);
                ppw.setMessage(description);
                ppw.hideIKnowTv();
            }
        }, 1000);
    }

    private void initRec() {
        try {
            if (null == adapter) {
                adapter = new OnlineMangaDetailsRecyclerAdapter(this);
                adapter.setList(currentManga.getChapters());
                adapter.setCacheChapters(cacheChapters);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(final int position) {
                        if (chooseing) {
                            if (firstChoose) {
                                baseToast.showToast("请点击下载终点!");
                                downloadStartPoint = position;
                                firstChoose = false;
                            } else {
                                doDownload(downloadStartPoint, position);
                            }
                        } else {
                            if (mOnlineDetailVM.getIsForAdult()) {
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
                });
                ptfGridView.setAdapter(adapter);
            } else {
                adapter.setList(currentManga.getChapters());
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manga_details_online;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collect_view:
                mOnlineDetailVM.doCollect(currentManga.getName(), mangaUrl, currentManga.getWebThumbnailUrl());
                break;
            case R.id.thumbnail_iv:
                showDescription();
                break;
            case R.id.type_tv:
                showTagsSelector();
                break;
            case R.id.author_tv:
                showAuthorSelector();
                break;
            case R.id.update_time_tv:
                mOnlineDetailVM.cacheDetail();
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
            MangaDialog dialog = new MangaDialog(this);
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
}
