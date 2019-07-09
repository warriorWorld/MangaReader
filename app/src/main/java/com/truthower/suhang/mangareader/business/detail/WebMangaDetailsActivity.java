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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OneShotDetailsAdapter;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailAdapter;
import com.truthower.suhang.mangareader.base.TTSActivity;
import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.DownloadBean;
import com.truthower.suhang.mangareader.bean.GradeBean;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.comment.CommentActivity;
import com.truthower.suhang.mangareader.business.download.DownloadActivity;
import com.truthower.suhang.mangareader.business.download.DownloadMangaManager;
import com.truthower.suhang.mangareader.business.gesture.SetGestureActivity;
import com.truthower.suhang.mangareader.business.main.MainActivity;
import com.truthower.suhang.mangareader.business.read.ReadMangaActivity;
import com.truthower.suhang.mangareader.business.search.SearchActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.eventbus.JumpEvent;
import com.truthower.suhang.mangareader.eventbus.TagClickEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnGradeDialogSelectedListener;
import com.truthower.suhang.mangareader.listener.OnResultListener;
import com.truthower.suhang.mangareader.listener.OnSevenFourteenListDialogListener;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.ActivityPoor;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.NumberUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.ThreeDESUtil;
import com.truthower.suhang.mangareader.utils.UltimateTextSizeUtil;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.GestureDialog;
import com.truthower.suhang.mangareader.widget.dialog.GradeDialog;
import com.truthower.suhang.mangareader.widget.dialog.ListDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;
import com.truthower.suhang.mangareader.widget.layout.StarLinearlayout;
import com.truthower.suhang.mangareader.widget.popupwindow.EasyPopupWindow;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshBase;
import com.truthower.suhang.mangareader.widget.pulltorefresh.PullToRefreshGridView;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.SingleSelectorDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class WebMangaDetailsActivity extends TTSActivity implements AdapterView.OnItemClickListener, View.OnClickListener,
        PullToRefreshBase.OnRefreshListener<GridView>,
        EasyPermissions.PermissionCallbacks, View.OnLongClickListener {
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
    private String[] optionsList = {"下载全部", "选择起始点下载", "加入正在追更", "加入我已看完"};
    private ProgressDialog loadBar;
    private SingleSelectorDialog optionsSelector;
    private String mangaUrl;
    private MangaDialog downloadDialog;
    private boolean isCollected = false;
    private String collectedId = "";
    private SingleSelectorDialog tagSelector, authorSelector;
    //one shot 直接获取到了所有图片的地址
    private ArrayList<String> oneShotPathList = new ArrayList<String>();
    //因为我不知道当期收藏的漫画是哪个网站的 所以就一个个试
    private int trySpiderPosition = 0;
    private String currentMangaName;
    private boolean isTopied, isFinished;
    private String[] authorOptions;
    private StarLinearlayout starLinearlayout;
    private TextView gradeTv, gradeCountTv, commentCountTv;
    private LinearLayout commentMsgLl;
    private RelativeLayout gradeRl;
    private ArrayList<GradeBean> gradeList = new ArrayList<>();
    private int wrongNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mangaUrl = intent.getStringExtra("mangaUrl");
        if (TextUtils.isEmpty(mangaUrl)) {
            this.finish();
        }
        initSpider();

        initUI();
        initPullGridView();
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        doGetIsCollected();
        doGetCommentCount();
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
                if (WebMangaDetailsActivity.this.isFinishing()){
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
                                doGetIsRead();
                                if (spider.isOneShot() && null != result.getChapters() && result.getChapters().size() > 0
                                        && !TextUtils.isEmpty(result.getChapters().get(0).getImgUrl())) {
                                    for (int i = 0; i < result.getChapters().size(); i++) {
                                        oneShotPathList.add(result.getChapters().get(i).getImgUrl());
                                    }
                                }

                                doGetGrade();
                            }
                        });
                    }

                    @Override
                    public void loadFailed(String error) {
                        loadBar.dismiss();
                        if (error.equals(Configure.WRONG_WEBSITE_EXCEPTION)) {
                            try {
                                if (LoginBean.getInstance().isMaster()) {
                                    BaseParameterUtil.getInstance().saveCurrentWebSite(WebMangaDetailsActivity.this, Configure.masterWebsList[trySpiderPosition]);
                                } else {
                                    BaseParameterUtil.getInstance().saveCurrentWebSite(WebMangaDetailsActivity.this, Configure.websList[trySpiderPosition]);
                                }
                                initSpider();
                                initWebManga(url);
                                trySpiderPosition++;
                            } catch (IndexOutOfBoundsException e) {
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
//        toggleCollect();

        if (spider.isOneShot()) {
            initOneShotGridView();
        } else {
            initGridView();
            adapter.setLastReadPosition(SharedPreferencesUtils.getIntSharedPreferencesData
                    (WebMangaDetailsActivity.this,
                            ShareKeys.ONLINE_MANGA_READ_CHAPTER_POSITION + currentManga.getName()));
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
        starLinearlayout = (StarLinearlayout) findViewById(R.id.grade_star_sll);
        starLinearlayout.setMaxStar(5);
        starLinearlayout.setStarNum(0f);
        gradeCountTv = (TextView) findViewById(R.id.grade_count_tv);
        gradeTv = (TextView) findViewById(R.id.grade_tv);
        commentCountTv = (TextView) findViewById(R.id.comment_count_tv);
        commentMsgLl = (LinearLayout) findViewById(R.id.comment_msg_ll);
        gradeRl = (RelativeLayout) findViewById(R.id.grade_rl);

        gradeRl.setOnClickListener(this);
        commentMsgLl.setOnClickListener(this);
        collectV.setOnClickListener(this);
        downloadIv.setOnClickListener(this);
        mangaTypeTv.setOnClickListener(this);
        thumbnailIV.setOnClickListener(this);
        thumbnailIV.setOnLongClickListener(this);
        collectV.setOnLongClickListener(this);
        mangaAuthorTv.setOnClickListener(this);
        baseTopBar.setRightBackground(R.drawable.more);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {

            @Override
            public void onRightClick() {
                if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(WebMangaDetailsActivity.this))) {
                    baseToast.showToast("请先登录");
                    return;
                }
                if (isForAdult()) {
                    doGetGesture();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (chooseing) {
            if (firstChoose) {
                downloadStartPoint = position;
                firstChoose = false;
            } else {
                doDownload(downloadStartPoint, position);
            }
        } else {
            if (isForAdult()) {
                doGetGesture(position);
            } else {
                toReadActivity(position);
            }
        }
    }

    private void doGetGesture(final int position) {
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(this);

        AVQuery<AVObject> query = new AVQuery<>("Gesture");
        query.whereEqualTo("owner", userName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject account, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (null == account) {
                    toReadActivity(position);
                } else {
                    if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                        String password = account.getString("password");
                        showGestureDialog(position, password);
                    }
                }
            }
        });
    }

    private void doGetGesture() {
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(this);

        AVQuery<AVObject> query = new AVQuery<>("Gesture");
        query.whereEqualTo("owner", userName);
        query.getFirstInBackground(new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject account, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (null == account) {
                    showOptionsSelectorDialog();
                } else {
                    if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                        String password = account.getString("password");
                        showGestureDialog(password);
                    }
                }
            }
        });
    }

    private void showGestureDialog(final int position, String answer) {
        GestureDialog gestureDialog = new GestureDialog(this);
        gestureDialog.setOnResultListener(new OnResultListener() {
            @Override
            public void onFinish() {
                wrongNum = 0;
                toReadActivity(position);
            }

            @Override
            public void onFailed() {
                wrongNum++;
                if (wrongNum > 4) {
                    baseToast.showToast("输入错误次数过多");
                    finish();
                }
            }
        });
        gestureDialog.show();
        gestureDialog.setAnswer(answer);
    }

    private void showGestureDialog(String answer) {
        GestureDialog gestureDialog = new GestureDialog(this);
        gestureDialog.setOnResultListener(new OnResultListener() {
            @Override
            public void onFinish() {
                wrongNum = 0;
                showOptionsSelectorDialog();
            }

            @Override
            public void onFailed() {
                wrongNum++;
                if (wrongNum > 4) {
                    baseToast.showToast("输入错误次数过多");
                    finish();
                }
            }
        });
        gestureDialog.show();
        gestureDialog.setAnswer(answer);
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

    @AfterPermissionGranted(Configure.PERMISSION_FILE_REQUST_CODE)
    private void doDownload(int start, int end) {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            baseToast.showToast("开始下载!");
            DownloadMangaManager.getInstance().reset(this);
            MangaBean temp = currentManga;
            ArrayList<ChapterBean> chapters = new ArrayList<>();
            for (int i = start; i <= end; i++) {
                ChapterBean item = new ChapterBean();
                item = currentManga.getChapters().get(i);
                chapters.add(item);
            }
            temp.setChapters(chapters);
            DownloadBean.getInstance().setMangaBean(this, temp);
            DownloadBean.getInstance().setOne_shot(this, spider.isOneShot());
            DownloadBean.getInstance().initDownloadChapters();
            DownloadBean.getInstance().setWebSite(this, BaseParameterUtil.getInstance().getCurrentWebSite(this));
            DownloadMangaManager.getInstance().doDownload(getApplicationContext());

            Intent intent = new Intent(this, DownloadActivity.class);
            startActivity(intent);
            WebMangaDetailsActivity.this.finish();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    Configure.PERMISSION_FILE_REQUST_CODE, perms);
        }
    }

    /**
     * 给已收藏的漫画置顶
     */
    private void doTopThisManga(final boolean isTop) {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(WebMangaDetailsActivity.this);
        AVQuery<AVObject> query1 = new AVQuery<>("Collected");
        query1.whereEqualTo("mangaUrl", mangaUrl);

        AVQuery<AVObject> query2 = new AVQuery<>("Collected");
        query2.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        //已存在的保存
                        AVObject object = AVObject.createWithoutData("Collected", list.get(0).getObjectId());
                        object.put("top", isTop);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                                    if (isTop) {
                                        baseToast.showToast("设置正在追更成功!");
                                    } else {
                                        baseToast.showToast("取消正在追更成功!");
                                    }
                                    isTopied = isTop;
                                }
                            }
                        });
                    } else {
                        //一定不会是新建的
                    }
                }
            }
        });
    }

    /**
     * 给已收藏的漫画标记为已看完
     */
    private void doFinishedThisManga(final boolean isFinished) {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(WebMangaDetailsActivity.this);
        AVQuery<AVObject> query1 = new AVQuery<>("Collected");
        query1.whereEqualTo("mangaUrl", mangaUrl);

        AVQuery<AVObject> query2 = new AVQuery<>("Collected");
        query2.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        //已存在的保存
                        AVObject object = AVObject.createWithoutData("Collected", list.get(0).getObjectId());
                        object.put("finished", isFinished);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                                    if (isFinished) {
                                        baseToast.showToast("设置已经看完成功!");
                                    } else {
                                        baseToast.showToast("取消已经看完成功!");
                                    }
                                    WebMangaDetailsActivity.this.isFinished = isFinished;
                                }
                            }
                        });
                    } else {
                        //一定不会是新建的
                    }
                }
            }
        });
    }

    private void doGetIsCollected() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(WebMangaDetailsActivity.this);
        AVQuery<AVObject> query1 = new AVQuery<>("Collected");
        query1.whereEqualTo("mangaUrl", mangaUrl);

        AVQuery<AVObject> query2 = new AVQuery<>("Collected");
        query2.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        collectedId = list.get(0).getObjectId();
                        isCollected = true;
                        isTopied = list.get(0).getBoolean("top");
                        isFinished = list.get(0).getBoolean("finished");
                    } else {
                        collectedId = "";
                        isCollected = false;
                        isTopied = false;
                        isFinished = false;
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
        SingleLoadBarUtil.getInstance().showLoadBar(WebMangaDetailsActivity.this);
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
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    baseToast.showToast("收藏成功");
                    doGetIsCollected();
                }
            }
        });
    }

    private void doGetGrade() {
        SingleLoadBarUtil.getInstance().showLoadBar(WebMangaDetailsActivity.this);
        AVQuery<AVObject> query = new AVQuery<>("Grade");
        query.whereEqualTo("manga_name", currentManga.getName());
        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        gradeList = new ArrayList<>();
                        GradeBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new GradeBean();
                            item.setMangaName(list.get(i).getString("manga_name"));
                            item.setMangaUrl(list.get(i).getString("mangaUrl"));
                            item.setStar(list.get(i).getInt("star"));
                            item.setOwner(list.get(i).getString("owner"));
                            gradeList.add(item);
                        }
                    }
                    refreshGrade();
                }
            }
        });
    }

    private void doGetCommentCount() {
        AVQuery<AVObject> query = new AVQuery<>("Comment");
        query.whereEqualTo("mangaUrl", mangaUrl);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    commentCountTv.setText(i + "");
                }
            }
        });
    }

    private void refreshGrade() {
        if (null == gradeList || gradeList.size() == 0) {
            return;
        }
        float gradeF = 0;
        float total = 0;
        for (int i = 0; i < gradeList.size(); i++) {
            total += gradeList.get(i).getStar();
        }
        gradeF = total / gradeList.size();
        starLinearlayout.setStarNum(gradeF);
        gradeTv.setText(NumberUtil.doubleDecimals(gradeF) + "");
        gradeCountTv.setText(gradeList.size() + "人评分");
    }


    private void doGrade(int star) {
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(WebMangaDetailsActivity.this);
        String mangaName = currentManga.getName();

        AVObject object = new AVObject("Grade");
        object.put("owner", userName);
        object.put("star", star);
        object.put("mangaUrl", mangaUrl);
        object.put("manga_name", mangaName);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(WebMangaDetailsActivity.this, e)) {
                    baseToast.showToast("评分成功");
                    doGetGrade();
                }
            }
        });
    }

    private void deleteCollected() {
        if (TextUtils.isEmpty(collectedId)) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(WebMangaDetailsActivity.this);
        // 执行 CQL 语句实现删除一个  对象
        AVQuery.doCloudQueryInBackground(
                "delete from Collected where objectId='" + collectedId + "'"
                , new CloudQueryCallback<AVCloudQueryResult>() {
                    @Override
                    public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                        SingleLoadBarUtil.getInstance().dismissLoadBar();
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
                        baseToast.showToast("请先点击起始话然后点击结束话!");
                        break;
                    case 2:
                        if (isCollected) {
                            showTopThisDialog();
                        } else {
                            baseToast.showToast("请先收藏漫画");
                        }
                        break;
                    case 3:
                        if (isCollected) {
                            showFinishedThisDialog();
                        } else {
                            baseToast.showToast("请先收藏漫画");
                        }
                        break;
                }
            }
        });
        listDialog.show();
        if (isTopied) {
            optionsList[2] = "取消正在追更";
        } else {
            optionsList[2] = "加入正在追更";
        }
        if (isFinished) {
            optionsList[3] = "取消我已看完";
        } else {
            optionsList[3] = "加入我已看完";
        }
        listDialog.setOptionsList(optionsList);
    }

    private void showTopThisDialog() {
        MangaDialog mangaDialog = new MangaDialog(this);
        mangaDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                doTopThisManga(!isTopied);
            }

            @Override
            public void onCancelClick() {

            }
        });
        mangaDialog.show();
        if (isTopied) {
            mangaDialog.setTitle("取消正在追更?");
        } else {
            mangaDialog.setTitle("加入正在追更?");
        }
        mangaDialog.setOkText("确定");
        mangaDialog.setCancelText("取消");
    }

    private void showFinishedThisDialog() {
        MangaDialog mangaDialog = new MangaDialog(this);
        mangaDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                doFinishedThisManga(!isFinished);
            }

            @Override
            public void onCancelClick() {

            }
        });
        mangaDialog.show();
        if (isFinished) {
            mangaDialog.setTitle("取消我已看完?");
        } else {
            mangaDialog.setTitle("加入我已看完?");
        }
        mangaDialog.setOkText("确定");
        mangaDialog.setCancelText("取消");
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

    private void showGradeDialog() {
        GradeDialog dialog = new GradeDialog(this);
        dialog.setOnGradeDialogSelectedListener(new OnGradeDialogSelectedListener() {
            @Override
            public void onSelected(float grade) {
                doGrade((int) grade);
            }
        });
        dialog.show();
    }

    private boolean isGraded() {
        if (null == gradeList || gradeList.size() == 0) {
            return false;
        }
        for (int i = 0; i < gradeList.size(); i++) {
            if (LoginBean.getInstance().getUserName().equals(gradeList.get(i).getOwner())) {
                return true;
            }
        }
        return false;
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
//                showDownloadDialog();
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
            case R.id.grade_rl:
                if (isGraded()) {
                    baseToast.showToast("不可对同一漫画重复评分!");
                    return;
                }
                showGradeDialog();
                break;
            case R.id.comment_msg_ll:
                Intent intent = new Intent(WebMangaDetailsActivity.this, CommentActivity.class);
                intent.putExtra("mangaName", currentManga.getName());
                intent.putExtra("mangaUrl", mangaUrl);
                startActivity(intent);
                break;
        }
    }

    private void toggleCollect() {
        if (isCollected) {
            collectV.setBackgroundResource(R.drawable.collected);
        } else {
            collectV.setBackgroundResource(R.drawable.collect);
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
