package com.truthower.suhang.mangareader.business.read;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.ReadMangaAdapter;
import com.truthower.suhang.mangareader.base.TTSActivity;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.StatisticsBean;
import com.truthower.suhang.mangareader.bean.YoudaoResponse;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.tag.TagManagerActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.db.DbAdapter;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.listener.OnEditResultListener;
import com.truthower.suhang.mangareader.listener.OnKeyboardChangeListener;
import com.truthower.suhang.mangareader.listener.OnSpeakClickListener;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.ImageUtil;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.VibratorUtil;
import com.truthower.suhang.mangareader.volley.VolleyCallBack;
import com.truthower.suhang.mangareader.volley.VolleyTool;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.ImgKeyboardDialog;
import com.truthower.suhang.mangareader.widget.dialog.KeyBoardDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaImgEditDialog;
import com.truthower.suhang.mangareader.widget.dialog.OnlyEditDialog;
import com.truthower.suhang.mangareader.widget.dialog.TranslateDialog;
import com.truthower.suhang.mangareader.widget.shotview.ScreenShot;
import com.truthower.suhang.mangareader.widget.shotview.ShotView;
import com.umeng.analytics.MobclickAgent;
import com.youdao.ocr.online.ImageOCRecognizer;
import com.youdao.ocr.online.Line;
import com.youdao.ocr.online.Line_Line;
import com.youdao.ocr.online.OCRListener;
import com.youdao.ocr.online.OCRParameters;
import com.youdao.ocr.online.OCRResult;
import com.youdao.ocr.online.OcrErrorCode;
import com.youdao.ocr.online.RecognizeLanguage;
import com.youdao.ocr.online.Region;
import com.youdao.ocr.online.Region_Line;
import com.youdao.ocr.online.Word;
import com.youdao.sdk.app.EncryptHelper;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * /storage/sdcard0/reptile/one-piece
 * <p/>
 * Created by Administrator on 2016/4/4.
 */
public class ReadMangaActivity extends TTSActivity implements OnClickListener {
    private HackyViewPager mangaPager;
    private SpiderBase spider;
    private DiscreteSeekBar seekBar;
    private Button ocrBtn;
    private View showSeekBar;
    private TextView readProgressTv;
    // 截图的view
    private ShotView shotView = null;
    private ReadMangaAdapter adapter;
    private ArrayList<String> pathList = new ArrayList<String>();
    private int historyPosition = 1;
    private int finalPosition = 1;
    private ProgressDialog loadBar;
    private boolean isLocalManga = false;
    private TopBar topBar;
    private OnlyEditDialog searchDialog;
    private MangaImgEditDialog mangaImgEditDialog;
    private ClipboardManager clip;//复制文本用
    private TranslateDialog translateResultDialog;
    private String chapterUrl;//线上漫画章节地址
    private String progressSaveKey = "";
    private int toPage = 0;
    private ImageView test_iv;
    private DbAdapter db;//数据库
    private int qureyWordCount = 0;
    private String realMangaName;
    /**
     * 时间
     */
    private SimpleDateFormat sdf;
    private Date curDate;
    private String date;
    //OCR识别
    final public static OCRParameters tps = new OCRParameters.Builder().source("youdaoocr").timeout(100000)
            .type(OCRParameters.TYPE_LINE).lanType(RecognizeLanguage.LINE_CHINESE_ENGLISH.getCode()).build();//
    private View screenDv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initSpider();
        initUI();
        initProgressBar();
        Intent intent = getIntent();
        pathList = (ArrayList<String>) intent.getSerializableExtra("pathList");
        if (null == pathList || pathList.size() == 0) {
            isLocalManga = false;
            chapterUrl = intent.getStringExtra("chapterUrl");
            doGetWebPics();
        } else {
            if (!pathList.get(0).contains("file://")) {
                //这种情况说明是one shot
                isLocalManga = false;
                toPage = intent.getIntExtra("img_position", 0);
            } else {
                isLocalManga = true;
            }
            refresh();
            if (toPage != 0) {
                mangaPager.setCurrentItem(toPage);
            }
        }
        String currentMangaName = intent.getStringExtra("currentMangaName");
        if (currentMangaName.contains("(") && currentMangaName.contains(")")) {
            int bracketPosition = currentMangaName.lastIndexOf("(");
            realMangaName = currentMangaName.substring(0, bracketPosition);
        } else {
            realMangaName = currentMangaName;
        }
        topBar.setTitle(currentMangaName);

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        curDate = new Date(System.currentTimeMillis());//获取当前时间
        date = sdf.format(curDate);
        db = new DbAdapter(this);

        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TTS, true)) {
            Handler handler = new Handler();
            Runnable updateThread = new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (SharedPreferencesUtils.getBooleanSharedPreferencesData(ReadMangaActivity.this, ShareKeys.THIS_USER_IS_NOT_AN_IDIOT, true)) {
                                text2Speech("点击右上角图标或者顶部标题可以直接查单词,点击左上角图标可以先划定区域查单词.");
                            }
                        }
                    });
                }
            };
            handler.postDelayed(updateThread, 50);
        }
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TUTORIAL, true)) {
            MangaDialog dialog = new MangaDialog(this);
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("1,点击漫画标题或右上角图标可调出查单词弹窗" +
                    "\n2,点击左下角图标可调出截屏查单词弹窗(可用手指划出指定区域翻译,可以避免输入法遮挡)" +
                    "\n3,双击漫画图片可放大" +
                    "\n4,点击屏幕中间稍微靠下位置可调出进度条,可以跳转到指定位置" +
                    "\n5,长按屏幕中间稍微靠下位置可保存或删除当前图片");
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_manga;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void doGetWebPics() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            //不登录不让用了
            this.finish();
            return;
        }
        loadBar.show();
        spider.getMangaChapterPics(this, chapterUrl, new JsoupCallBack<ArrayList<String>>() {
            @Override
            public void loadSucceed(final ArrayList<String> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadBar.dismiss();
                        pathList = result;
                        refresh();
                    }
                });
            }

            @Override
            public void loadFailed(String error) {
                loadBar.dismiss();
            }
        });
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
        loadBar = new ProgressDialog(ReadMangaActivity.this);
        loadBar.setCancelable(false);
        loadBar.setMessage("稍等...");

    }

    /**
     * 进度条
     */
    private void initSeekBar() {
        seekBar.setMin(1);
        seekBar.setMax(pathList.size());
        seekBar.setProgress(historyPosition);
        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                finalPosition = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                MobclickAgent.onEvent(ReadMangaActivity.this, "seek_bar");
                if (finalPosition >= 0) {
                    mangaPager.setCurrentItem(finalPosition - 1);
//                    cutSeekBar();
//                seekBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(final EventBusEvent event) {
        if (null == event)
            return;
        Intent intent = null;
        switch (event.getEventType()) {
//            case EventBusEvent.NEED_LOGIN:
//                ToastUtil.tipShort(BaseActivity.this, "需要登录");
//                intent = new Intent(BaseActivity.this, LoginActivity.class);
//                break;
            case EventBusEvent.COPY_BOARD_EVENT:
                showBaseDialog("检测到你复制了某漫画地址，是否跳转到详情页？", "", "是", "否",
                        new MangaDialog.OnPeanutDialogClickListener() {
                            @Override
                            public void onOkClick() {
                                Intent intent1 = new Intent(ReadMangaActivity.this, WebMangaDetailsActivity.class);
                                intent1.putExtra("mangaUrl", event.getMsg());
                                startActivity(intent1);
                            }

                            @Override
                            public void onCancelClick() {

                            }
                        });
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    private void refresh() {
        initViewPager();
        initSeekBar();
        readProgressTv.setText(historyPosition + 1 + "/" + pathList.size());
    }


    private void initUI() {
        mangaPager = (HackyViewPager) findViewById(R.id.manga_viewpager);
        seekBar = (DiscreteSeekBar) findViewById(R.id.seekbar);
        showSeekBar = findViewById(R.id.show_seek_bar);
        ocrBtn = (Button) findViewById(R.id.ocr_btn);
        readProgressTv = (TextView) findViewById(R.id.read_progress_tv);
        test_iv = (ImageView) findViewById(R.id.test_iv);
        screenDv = findViewById(R.id.screenshoot_dv);

        readProgressTv.setOnClickListener(this);
        screenDv.setOnClickListener(this);
        showSeekBar.setOnClickListener(this);
        showSeekBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String file = pathList.get(historyPosition);
                if (file.contains("file://")) {
                    file = file.substring(7, file.length());
                    showDeleteDialog(file);
                } else {
                    showSaveDialog(file);
                }
                return true;
            }
        });

        hideBaseTopBar();
        topBar = (TopBar) findViewById(R.id.read_manga_top_bar);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onRightClick() {
                if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                        (ReadMangaActivity.this, ShareKeys.CLOSE_SH_KEYBOARD, false)) {
                    showSearchDialog();
                } else {
                    showKeyboardDialog();
                }
                MobclickAgent.onEvent(ReadMangaActivity.this, "translate");
            }

            @Override
            public void onTitleClick() {
                if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                        (ReadMangaActivity.this, ShareKeys.CLOSE_SH_KEYBOARD, false)) {
                    showSearchDialog();
                } else {
                    showKeyboardDialog();
                }
                MobclickAgent.onEvent(ReadMangaActivity.this, "title_translate");
            }

            @Override
            public void onLeftClick() {
                finish();
            }
        });
        topBar.setTopBarLongClickLister(new TopBar.OnTopBarLongClickListener() {
            @Override
            public void onLeftLongClick() {

            }

            @Override
            public void onRightLongClick() {

            }

            @Override
            public void onTitleLongClick() {
                if (LoginBean.getInstance().isMaster()) {
                    Intent intent = new Intent(ReadMangaActivity.this, TagManagerActivity.class);
                    intent.putExtra("imgUrl", pathList.get(historyPosition));
                    startActivity(intent);
                }
            }
        });

        ocrBtn.setOnClickListener(this);
    }

    private String getResult(OCRResult result) {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        if (OCRParameters.TYPE_TEXT.equals(result.getType())) {
            //按文本识别
            List<Region> regions = result.getRegions();
            for (Region region : regions) {
                List<Line> lines = region.getLines();
                for (Line line : lines) {
                    sb.append("文本" + i++ + "： ");
                    List<Word> words = line.getWords();
                    for (Word word : words) {
                        sb.append(word.getText()).append(" ");
                    }
                    sb.append("\n");
                }
            }
        } else {
            //按行识别
            List<Region_Line> regions = result.getRegions_Line();
            for (Region_Line region : regions) {
                List<Line_Line> lines = region.getLines();
                for (Line_Line line : lines) {
                    sb.append("文本" + i++ + "： ");
                    sb.append(line.getText());
                    sb.append("\n");
                }
            }
        }
        String text = sb.toString();
        if (!TextUtils.isEmpty(text)) {
            text = text.substring(0, text.length() - 2);
        }
        return text;

    }

    private void showOcrResultDialog(SpannableString spannableString) {
        MangaDialog dialog = new MangaDialog(this);
        dialog.show();
        dialog.setTitle("结果");
        dialog.setMessage(spannableString);
        dialog.setOkText("知道了");
    }

    private void updateStatisctics() {
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        db.updateStatistics(qureyWordCount, 1, realMangaName);
                        qureyWordCount = 0;
                    } catch (Exception e) {
                        if (Configure.isTest) {
                            MangaDialog dialog = new MangaDialog(ReadMangaActivity.this);
                            dialog.show();
                            dialog.setTitle(e + "");
                        }
                    }
                }
            }.start();
            if (!date.equals(SharedPreferencesUtils.getSharedPreferencesData(
                    ReadMangaActivity.this, ShareKeys.STATISTICS_UPDATE_KEY + realMangaName))) {
                doStatisctics();
            }
        } catch (Exception e) {
            //有可能空指针 不处理 不能阻断看书进程
            if (Configure.isTest) {
                MangaDialog dialog = new MangaDialog(this);
                dialog.show();
                dialog.setTitle(e + "");
            }
        }
    }

    boolean isRequesting = false;

    private void doStatisctics() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            return;
        }
        try {
            if (isRequesting) {
                return;
            }
            isRequesting = true;
            StatisticsBean item = db.queryStatisticsByBookName(realMangaName);
            AVObject object = new AVObject("Statistics");
            object.put("owner", LoginBean.getInstance().getUserName());
            object.put("query_word_c", item.getQuery_word_c());
            object.put("read_page", item.getRead_page());
            object.put("manga_name", realMangaName);
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    isRequesting = false;
                    if (LeanCloundUtil.handleLeanResult(e)) {
                        SharedPreferencesUtils.setSharedPreferencesData
                                (ReadMangaActivity.this, ShareKeys.STATISTICS_UPDATE_KEY + realMangaName, date);
                        db.deleteStatiscticsByBookName(realMangaName);
                    }
                }
            });
        } catch (Exception e) {
            //有可能空指针 不处理 不能阻断看书进程
        }
    }

    private void showSearchDialog() {
        if (null == searchDialog) {
            searchDialog = new OnlyEditDialog(this);
            searchDialog.setOnEditResultListener(new OnEditResultListener() {
                @Override
                public void onResult(String text) {
                    translateWord(text);
                }

                @Override
                public void onCancelClick() {

                }
            });
            searchDialog.setCancelable(true);
        }
        searchDialog.show();
        searchDialog.clearEdit();
    }

    private void showKeyboardDialog() {
        KeyBoardDialog dialog = new KeyBoardDialog(this);
        dialog.setOnKeyboardChangeListener(new OnKeyboardChangeListener() {
            @Override
            public void onChange(String res) {
                if (!SharedPreferencesUtils.getBooleanSharedPreferencesData
                        (ReadMangaActivity.this, ShareKeys.CLOSE_SH_KEYBOARD_VIBRATION, false)) {
                    VibratorUtil.Vibrate(ReadMangaActivity.this, 20);
                }
                if (!SharedPreferencesUtils.getBooleanSharedPreferencesData
                        (ReadMangaActivity.this, ShareKeys.CLOSE_SH_KEYBOARD_SOUND, false)) {
                    text2Speech(res, true);
                }
            }

            @Override
            public void onFinish(String res) {
                translateWord(res);
            }
        });
        dialog.show();
    }

    private void showImgEditDialog(Bitmap bp) {
        if (null == mangaImgEditDialog) {
            mangaImgEditDialog = new MangaImgEditDialog(this);
            mangaImgEditDialog.setOnEditResultListener(new OnEditResultListener() {
                @Override
                public void onResult(String text) {
                    translateWord(text);
                }

                @Override
                public void onCancelClick() {

                }
            });
        }
        mangaImgEditDialog.show();
        mangaImgEditDialog.setImgRes(bp);
        mangaImgEditDialog.clearEdit();
    }

    private void showImgKeyBoardDialog(Bitmap bp) {
        ImgKeyboardDialog dialog = new ImgKeyboardDialog(this);
        dialog.setOnKeyboardChangeListener(new OnKeyboardChangeListener() {
            @Override
            public void onChange(String res) {
                if (!SharedPreferencesUtils.getBooleanSharedPreferencesData
                        (ReadMangaActivity.this, ShareKeys.CLOSE_SH_KEYBOARD_VIBRATION, false)) {
                    VibratorUtil.Vibrate(ReadMangaActivity.this, 20);
                }
                if (!SharedPreferencesUtils.getBooleanSharedPreferencesData
                        (ReadMangaActivity.this, ShareKeys.CLOSE_SH_KEYBOARD_SOUND, false)) {
                    text2Speech(res, true);
                }
            }

            @Override
            public void onFinish(String res) {
                translateWord(res);
            }
        });
        dialog.show();
        dialog.setImgRes(bp);
    }

    private void translateWord(final String word) {
        SharedPreferencesUtils.setSharedPreferencesData(this, ShareKeys.THIS_USER_IS_NOT_AN_IDIOT,
                false);
        clip.setText(word);
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData
                (this, ShareKeys.CLOSE_TTS, false)) {
            text2Speech(word);
        }
        qureyWordCount++;
        //记录查过的单词
        db.insertWordsBookTb(word);
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TRANSLATE, false)) {
            //关闭自动翻译
            return;
        }
        String url = Configure.YOUDAO + word;
        HashMap<String, String> params = new HashMap<String, String>();
        VolleyCallBack<YoudaoResponse> callback = new VolleyCallBack<YoudaoResponse>() {

            @Override
            public void loadSucceed(YoudaoResponse result) {
                if (null != result && result.getErrorCode() == 0) {
                    YoudaoResponse.BasicBean item = result.getBasic();
                    String t = "";
                    if (null != item) {
                        for (int i = 0; i < item.getExplains().size(); i++) {
                            t = t + item.getExplains().get(i) + ";";
                        }

                        showTranslateResultDialog(word, result.getQuery() + "  [" + item.getPhonetic() + "]: " + "\n" + t);
                    } else {
                        baseToast.showToast("没查到该词");
                    }
                } else {
                    baseToast.showToast("没查到该词");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                baseToast.showToast("error\n" + error);
            }

            @Override
            public void loadSucceedButNotNormal(YoudaoResponse result) {

            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                ReadMangaActivity.this, url, params,
                YoudaoResponse.class, callback);
    }


    private void showTranslateResultDialog(final String title, String msg) {
        if (null == translateResultDialog) {
            translateResultDialog = new TranslateDialog(this);
            translateResultDialog.setOnSpeakClickListener(new OnSpeakClickListener() {
                @Override
                public void onSpeakClick(String word) {
                    text2Speech(word);
                }
            });
        }
        translateResultDialog.show();

        translateResultDialog.setTitle(title);
        translateResultDialog.setMessage(msg);
        translateResultDialog.setOkText("确定");
        translateResultDialog.setCancelable(true);
    }

    private void showDeleteDialog(final String fileName) {
        MangaDialog deleteDialog = new MangaDialog(this);
        deleteDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                FileSpider.getInstance().deleteFile(fileName);
                pathList.remove(historyPosition);
                baseToast.showToast("已删除");
                initViewPager();
            }

            @Override
            public void onCancelClick() {

            }
        });
        deleteDialog.show();

        deleteDialog.setTitle("确定删除?");
        deleteDialog.setOkText("删除");
        deleteDialog.setCancelText("算了");
        deleteDialog.setCancelable(true);
    }


    private void showSaveDialog(final String imgPath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否保存图片?");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadPic(imgPath);
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    /**
     *
     */
    private void downLoadPic(final String path) {
        // 将图片下载并保存
        new Thread() {
            public void run() {
                Bitmap bp = null;
                if (!TextUtils.isEmpty(path)) {
                    //从网络上获取到图片
                    Long time = new Date().getTime();
                    String timeString = time + "";
                    timeString = timeString.substring(5);
                    FileSpider.getInstance().loadImageFromNetwork(path, "scattered", "/scattered(" + timeString + ").png");
                }
            }
        }.start();
    }


    private void initViewPager() {
        if (null == adapter) {
            adapter = new ReadMangaAdapter(ReadMangaActivity.this, pathList);
            if (isLocalManga) {
                mangaPager.setOffscreenPageLimit(1);
            } else {
                mangaPager.setOffscreenPageLimit(3);
            }
            mangaPager.setAdapter(adapter);

            recoverState();
            mangaPager.setPageTransformer(true, new DepthPageTransformer());
            mangaPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    historyPosition = position;
                    readProgressTv.setText(position + 1 + "/" + pathList.size());
                    updateStatisctics();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
//            mangaPager.setCurrentItem(2);
        } else {
            adapter.setPathList(pathList);
            adapter.notifyDataSetChanged();
        }
    }

    private void cutSeekBar() {
        if (seekBar.isShown()) {
            seekBar.setVisibility(View.GONE);
        } else {
            seekBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveState();
        db.closeDb();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recoverState();
    }

    private void saveState() {
        initProgressKey();
        SharedPreferencesUtils.setSharedPreferencesData(this, progressSaveKey + "progress",
                historyPosition);
    }

    private void initProgressKey() {
        if (TextUtils.isEmpty(progressSaveKey)) {
            if (isLocalManga) {
                int pos = pathList.get(0).lastIndexOf("/");
                progressSaveKey = pathList.get(0).substring(0, pos);
            } else {
                progressSaveKey = chapterUrl;
            }
        }
    }

    private void recoverState() {
        initProgressKey();
        int p = SharedPreferencesUtils.getIntSharedPreferencesData(this,
                progressSaveKey + "progress");
        if (p >= 0) {
            historyPosition = p;
            mangaPager.setCurrentItem(p);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_seek_bar:
                cutSeekBar();
                break;
            case R.id.read_progress_tv:

                break;
            case R.id.screenshoot_dv:
                MobclickAgent.onEvent(ReadMangaActivity.this, "shot_translate");
                try {
                    baseToast.showToast("手指划过区域然后松手截屏");

                    if (shotView == null) {
                        shotView = (ShotView) findViewById(R.id.shot_view);
                        shotView.setL(new ShotView.FinishShotListener() {
                            @Override
                            public void finishShot(Bitmap bp) {
                                if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                                        (ReadMangaActivity.this, ShareKeys.CLOSE_SH_KEYBOARD, false)) {
                                    showImgEditDialog(bp);
                                } else {
                                    showImgKeyBoardDialog(bp);
                                }
                            }
                        });
                    } else {
                        shotView.setIsRunning(true);
                    }

                    Bitmap bgBitmap = shotView.getBitmap();
                    if (bgBitmap != null) {
                        bgBitmap.recycle();
                    }
                    bgBitmap = ScreenShot.takeScreenShot(ReadMangaActivity.this);
                    /**
                     * getDecorView这个方法是获取缓存的屏幕 显然PhotoView这个控件放大缩小并没有触发新的缓存 所以截屏后再放大缩小就会有问题了
                     * 而我通过viewpager翻页的方法强行触发新的缓存解决这个问题
                     */
                    if (historyPosition + 1 == pathList.size()) {
                        int temp = historyPosition;
                        mangaPager.setCurrentItem(temp - 1);
                        mangaPager.setCurrentItem(temp);
                    } else {
                        int temp = historyPosition;
                        mangaPager.setCurrentItem(temp + 1);
                        mangaPager.setCurrentItem(temp);
                    }
                    shotView.setBitmap(bgBitmap);
                    shotView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ocr_btn:
                if (LoginBean.getInstance().isCreator()) {
                    Bitmap bitmap = ImageUtil.readBitmapFromFile(pathList.get(mangaPager.getCurrentItem()).replaceAll("file://", ""), 768);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                    byte[] datas = baos.toByteArray();
                    String bases64 = EncryptHelper.getBase64(datas);
                    int count = bases64.length();
                    while (count > 1.4 * 1024 * 1024) {
                        quality = quality - 10;
                        baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                        datas = baos.toByteArray();
                        bases64 = EncryptHelper.getBase64(datas);
                    }
                    final String base64 = bases64;

                    ImageOCRecognizer.getInstance(Configure.tps).recognize(base64,
                            new OCRListener() {

                                @Override
                                public void onResult(final OCRResult result,
                                                     String input) {
                                    //识别成功
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            String text = getResult(result);
                                            SpannableString spannableString = new SpannableString(text);
                                            int start = 0;
                                            while (start < text.length() && start >= 0) {
                                                int s = text.indexOf("文本", start);
                                                int end = text.indexOf("：", s) + 1;
                                                if (s >= 0) {
                                                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#808080"));
                                                    AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(35);
                                                    UnderlineSpan underlineSpan = new UnderlineSpan();
                                                    spannableString.setSpan(sizeSpan, s, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                    spannableString.setSpan(colorSpan, s, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                                    spannableString.setSpan(underlineSpan, s, end - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                                    start = end;
                                                } else {
                                                    break;
                                                }

                                            }
                                            showOcrResultDialog(spannableString);
                                        }
                                    });
                                }

                                @Override
                                public void onError(final OcrErrorCode error) {
                                    //识别失败
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showBaseDialog("失败", error.toString() + error.getCode(), "", "", null);
                                        }
                                    });
                                }
                            });
                }
                break;
        }
    }
}
