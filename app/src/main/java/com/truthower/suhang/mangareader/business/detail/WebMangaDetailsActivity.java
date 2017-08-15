package com.truthower.suhang.mangareader.business.detail;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OneShotDetailsAdapter;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.download.DownloadService;
import com.truthower.suhang.mangareader.business.main.MainActivity;
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.DownLoadEvent;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.eventbus.JumpEvent;
import com.truthower.suhang.mangareader.eventbus.TagClickEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.ActivityPoor;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.ThreeDESUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.popupwindow.EasyPopupWindow;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.WheelSelectorDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class WebMangaDetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener,
        PullToRefreshBase.OnRefreshListener<GridView>,
        EasyPermissions.PermissionCallbacks {
    private SpiderBase spider;
    private PullToRefreshGridView pullToRefreshGridView;
    private GridView mangaGV;
    private View collectV;
    private boolean chooseing = false;//判断是否在选择状态
    private boolean firstChoose = true;
    private int downloadStartPoint = 0;
    private MangaBean currentManga;
    private OnlineMangaDetailAdapter adapter;
    private OneShotDetailsAdapter oneShotAdapter;
    private ImageView thumbnailIV, downloadIv;
    private TextView mangaNameTv, mangaAuthorTv, mangaTypeTv, lastUpdateTv, downloadTagTv;
    private String[] optionsList = {"下载全部", "选择起始点下载"};
    private ProgressDialog loadBar;
    private WheelSelectorDialog optionsSelector;
    private String mangaUrl;
    private MangaDialog downloadDialog;
    private boolean isCollected = false;
    private String collectedId = "";
    private WheelSelectorDialog tagSelector;
    //one shot 直接获取到了所有图片的地址
    private ArrayList<String> oneShotPathList = new ArrayList<String>();
    //因为我不知道当期收藏的漫画是哪个网站的 所以就一个个试
    private int trySpiderPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mangaUrl = intent.getStringExtra("mangaUrl");
        if (TextUtils.isEmpty(mangaUrl)) {
            this.finish();
        }
        initSpider(Configure.currentWebSite);

        initUI();
        initPullGridView();
        initProgressBar();
        initWebManga(mangaUrl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doGetIsCollected();
    }

    private void initSpider(String currentWebSite) {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.truthower.suhang.mangareader.spider." + currentWebSite + "Spider").newInstance();
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
                                toggleDownload();
                                showDescription();
                                doGetIsRead();
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
                        if (error.equals("catch 1 exception")) {
                            if (LoginBean.getInstance().isMaster()) {
                                initSpider(Configure.masterWebsList[trySpiderPosition]);
                            } else {
                                initSpider(Configure.websList[trySpiderPosition]);
                            }
                            initWebManga(url);
                            trySpiderPosition++;
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
            mangaAuthorTv.setText("作者:" + currentManga.getAuthor());
        } else {
            mangaAuthorTv.setVisibility(View.GONE);
        }
        String mangaTags = "";
        for (int i = 0; i < currentManga.getTypes().length; i++) {
            //漫画类型
            mangaTags = mangaTags + " " + currentManga.getTypes()[i];
        }
        mangaTypeTv.setText("类型:" + mangaTags);
        if (!TextUtils.isEmpty(currentManga.getLast_update())) {
            lastUpdateTv.setVisibility(View.VISIBLE);
            lastUpdateTv.setText("最后更新:" + currentManga.getLast_update());
        } else {
            lastUpdateTv.setVisibility(View.GONE);
        }
//        toggleCollect();

        if (spider.isOneShot()) {
            initOneShotGridView();
        } else {
            initGridView();
        }
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new OnlineMangaDetailAdapter(this, currentManga.getChapters());
            mangaGV.setAdapter(adapter);
            mangaGV.setColumnWidth(50);
            mangaGV.setNumColumns(5);
            mangaGV.setVerticalSpacing(10);
            mangaGV.setHorizontalSpacing(3);
            mangaGV.setOnItemClickListener(this);
        } else {
            adapter.setChapters(currentManga.getChapters());
            adapter.notifyDataSetChanged();
        }
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
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
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
    }

    private void initUI() {
        pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.ptf_grid_view);
        mangaGV = (GridView) pullToRefreshGridView.getRefreshableView();
        thumbnailIV = (ImageView) findViewById(R.id.thumbnail);
        mangaNameTv = (TextView) findViewById(R.id.manga_name);
        mangaAuthorTv = (TextView) findViewById(R.id.manga_author);
        mangaTypeTv = (TextView) findViewById(R.id.manga_type);
        lastUpdateTv = (TextView) findViewById(R.id.manga_update_date);
        collectV = findViewById(R.id.collect_view);
        downloadIv = (ImageView) findViewById(R.id.download_iv);
        downloadTagTv = (TextView) findViewById(R.id.download_tag_tv);

        collectV.setOnClickListener(this);
        downloadIv.setOnClickListener(this);
        mangaTypeTv.setOnClickListener(this);
        thumbnailIV.setOnClickListener(this);
        baseTopBar.setRightText("下载");
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
                showOptionsSelector();
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

    private void initPullGridView() {
        // 上拉加载更多
        pullToRefreshGridView.setPullLoadEnabled(true);
        // 滚到底部自动加载
        pullToRefreshGridView.setScrollLoadEnabled(false);
        pullToRefreshGridView.setOnRefreshListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manga_details_web;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (chooseing) {
            if (firstChoose) {
                downloadStartPoint = position;
                firstChoose = false;
            } else {
                doDownload(downloadStartPoint, position, 1);
            }
        } else {
            Intent intent = new Intent(WebMangaDetailsActivity.this, ReadMangaActivity.class);
            if (spider.isOneShot() && null != oneShotPathList && oneShotPathList.size() > 0) {
                Configure.currentMangaName = currentManga.getName();
                Bundle pathListBundle = new Bundle();
                pathListBundle.putSerializable("pathList", oneShotPathList);
                intent.putExtras(pathListBundle);
                intent.putExtra("img_position", position);
            } else {
                Configure.currentMangaName = currentManga.getName() + "(" + currentManga.getChapters().
                        get(position).getChapterPosition() + ")";
                intent.putExtra("chapterUrl", currentManga.getChapters().get(position).getChapterUrl());
            }
            if (null != intent) {
                startActivity(intent);
            }
        }
    }

    private void showTagsSelector() {
        if (null == tagSelector) {
            tagSelector = new WheelSelectorDialog(this);
            tagSelector.setCancelable(true);
        }
        tagSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

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
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
            }
        });
        tagSelector.show();
        if (null != currentManga.getTypeCodes() && currentManga.getTypeCodes().length > 0) {
            tagSelector.initOptionsData(currentManga.getTypes(), currentManga.getTypeCodes());
        } else {
            tagSelector.initOptionsData(currentManga.getTypes());
        }
    }

    private void downloadAll() {
        doDownload(0, currentManga.getChapters().size() - 1, 1);
    }

    @AfterPermissionGranted(Configure.PERMISSION_FILE_REQUST_CODE)
    private void doDownload(int start, int end, int startPage) {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            //先停掉服务
            Intent stopServiceIntent = new Intent(WebMangaDetailsActivity.this, DownloadService.class);
            stopService(stopServiceIntent);
            //再打开
            Intent intent = new Intent(WebMangaDetailsActivity.this, DownloadService.class);
            Bundle mangaBundle = new Bundle();
            mangaBundle.putSerializable("download_MangaBean", currentManga);
            intent.putExtras(mangaBundle);
            intent.putExtra("download_folderSize", 3);
            intent.putExtra("download_startPage", startPage);
            intent.putExtra("download_currentChapter", start);
            intent.putExtra("download_endChapter", end);
            startService(intent);
            baseToast.showToast("开始下载!");
            showDownloadDialog();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    Configure.PERMISSION_FILE_REQUST_CODE, perms);
        }
    }

    private void doGetIsCollected() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            return;
        }
        AVQuery<AVObject> query1 = new AVQuery<>("Collected");
        query1.whereEqualTo("mangaUrl", mangaUrl);

        AVQuery<AVObject> query2 = new AVQuery<>("Collected");
        query2.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        collectedId = list.get(0).getObjectId();
                        isCollected = true;

                    } else {
                        collectedId = "";
                        isCollected = false;
                    }
                    toggleCollect();
                }
            }
        });
    }

    private void doCollect() {
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        String webThumbnailUrl = currentManga.getWebThumbnailUrl();
        String mangaName = currentManga.getName();

        AVObject object = new AVObject("Collected");
        object.put("owner", userName);
        object.put("webThumbnailUrl", webThumbnailUrl);
        object.put("mangaUrl", mangaUrl);
        object.put("mangaName", mangaName);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    baseToast.showToast("收藏成功");
                    doGetIsCollected();
                }
            }
        });
    }

    private void deleteCollected() {
        if (TextUtils.isEmpty(collectedId)) {
            return;
        }
        // 执行 CQL 语句实现删除一个 Todo 对象
        AVQuery.doCloudQueryInBackground(
                "delete from Collected where objectId='" + collectedId + "'"
                , new CloudQueryCallback<AVCloudQueryResult>() {
                    @Override
                    public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                        if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                            baseToast.showToast("取消收藏");
                            isCollected = false;
                            toggleCollect();
                        }
                    }
                });
    }

    private void doGetIsRead() {
        if (!spider.isOneShot()) {
            return;
        }
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            return;
        }
        AVQuery<AVObject> query1 = new AVQuery<>("History");
        query1.whereEqualTo("mangaName", ThreeDESUtil.encode(Configure.key, currentManga.getName()));

        AVQuery<AVObject> query2 = new AVQuery<>("History");
        query2.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        baseTopBar.setTitle("(阅)" + currentManga.getName());
                    } else {
                        baseTopBar.setTitle(currentManga.getName());
                        addToRead();
                    }
                }
            }
        });
    }

    private void addToRead() {
        if (!spider.isOneShot()) {
            return;
        }
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        AVObject object = new AVObject("History");
        object.put("owner", userName);
        object.put("mangaName", ThreeDESUtil.encode(Configure.key, currentManga.getName()));
        object.saveInBackground();
    }

    private void showOptionsSelector() {
        if (null == optionsSelector) {
            optionsSelector = new WheelSelectorDialog(this);
            optionsSelector.setCancelable(true);
        }
        optionsSelector.setOnSingleSelectedListener(new WheelSelectorDialog.OnSingleSelectedListener() {

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes) {
            }

            @Override
            public void onOkBtnClick(String selectedRes, String selectedCodeRes, String selectedTypeRes) {
            }

            @Override
            public void onOkBtnClick(int position) {
                switch (position) {
                    case 0:
                        baseToast.showToast("开始下载!");
                        downloadAll();
                        break;
                    case 1:
                        chooseing = true;
                        firstChoose = true;
                        baseToast.showToast("请先点击起始话然后点击结束话!");
                        break;
                }
            }
        });
        optionsSelector.show();

        optionsSelector.initOptionsData(optionsList);
    }

    @Subscribe
    public void onEventMainThread(final DownLoadEvent event) {
        if (null == event)
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (event.getEventType()) {
                    case EventBusEvent.DOWNLOAD_EVENT:

                        break;
                    case EventBusEvent.DOWNLOAD_FINISH_EVENT:
                        stopDownload();
                        showDownloadDialog();
                        break;
                    case EventBusEvent.DOWNLOAD_FAIL_EVENT:
                        baseToast.showToast(event.getDownloadExplain());
                        break;
                }
                //刷新UI放在这里才准确
                refreshDownloadDialogMsg(event);
                toggleDownload();
            }
        });
    }

    private void showDescription() {
        if (TextUtils.isEmpty(currentManga.getDescription())) {
            return;
        }
        EasyPopupWindow ppw = new EasyPopupWindow(this);
        ppw.adaptiveShowAsDropDown(thumbnailIV, 0, 0);
        ppw.setMessage(currentManga.getDescription());
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
            case R.id.download_iv:
                showDownloadDialog();
                break;
            case R.id.manga_type:
                showTagsSelector();
                break;
            case R.id.thumbnail:
                showDescription();
                break;
        }
    }


    private void stopDownload() {
        Intent stopIntent = new Intent(WebMangaDetailsActivity.this, DownloadService.class);
        stopService(stopIntent);
        toggleDownload();
        baseToast.showToast("已停止");
    }

    private void showDownloadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new MangaDialog(this);
            downloadDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    if (Configure.isDownloadServiceRunning) {
                        stopDownload();
                    } else {
                        doDownload(SharedPreferencesUtils.getIntSharedPreferencesData
                                        (WebMangaDetailsActivity.this, ShareKeys.CURRENT_DOWNLOAD_EPISODE),
                                SharedPreferencesUtils.getIntSharedPreferencesData
                                        (WebMangaDetailsActivity.this, ShareKeys.DOWNLOAD_END_EPISODE),
                                SharedPreferencesUtils.getIntSharedPreferencesData
                                        (WebMangaDetailsActivity.this, ShareKeys.CURRENT_DOWNLOAD_PAGE));
                    }
                }

                @Override
                public void onCancelClick() {

                }
            });
        }
        downloadDialog.show();
        String downloadMsg = SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.DOWNLOAD_EXPLAIN);
        if (TextUtils.isEmpty(downloadMsg)) {
            downloadMsg = "开始下载";
        }
        String downloadingMangaName = SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.DOWNLOAD_MANGA_NAME);
        if (TextUtils.isEmpty(downloadingMangaName)) {
            downloadingMangaName = currentManga.getName();
        } else {
            downloadingMangaName = "下载" + downloadingMangaName;
        }
        downloadDialog.setTitle(downloadingMangaName);
        downloadDialog.setMessage(downloadMsg);
        downloadDialog.setCancelText("知道了");
        if (Configure.isDownloadServiceRunning) {
            downloadDialog.setOkText("停止下载");
        } else {
            downloadDialog.setOkText("继续下载");
        }
    }

    private void refreshDownloadDialogMsg(DownLoadEvent event) {
        if (null != downloadDialog) {
            downloadDialog.setMessage(event.getDownloadExplain());
            downloadDialog.setTitle(event.getDownloadMangaName());
            if (Configure.isDownloadServiceRunning) {
                downloadDialog.setOkText("停止下载");
            } else {
                downloadDialog.setOkText("继续下载");
            }
        }
    }

    private void toggleCollect() {
        if (isCollected) {
            collectV.setBackgroundResource(R.drawable.collected);
        } else {
            collectV.setBackgroundResource(R.drawable.collect);
        }
    }

    private void toggleDownload() {
        if (Configure.isDownloadServiceRunning && null != currentManga && currentManga.getName().equals
                (SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.DOWNLOAD_MANGA_NAME))) {
            downloadTagTv.setVisibility(View.VISIBLE);
        } else {
            downloadTagTv.setVisibility(View.GONE);
        }
        if (currentManga.getName().equals
                (SharedPreferencesUtils.getSharedPreferencesData(this, ShareKeys.DOWNLOAD_MANGA_NAME))) {
            //只有当前下载的漫画才看得到这个
            downloadIv.setVisibility(View.VISIBLE);
        } else {
            downloadIv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
        initWebManga(mangaUrl);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
        pullToRefreshGridView.onPullDownRefreshComplete();// 动画结束方法
        pullToRefreshGridView.onPullUpRefreshComplete();
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
