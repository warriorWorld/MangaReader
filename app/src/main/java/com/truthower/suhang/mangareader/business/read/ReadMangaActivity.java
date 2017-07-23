package com.truthower.suhang.mangareader.business.read;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;


import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.ReadMangaAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaEditDialog;
import com.truthower.suhang.mangareader.widget.dialog.MangaImgEditDialog;
import com.truthower.suhang.mangareader.widget.shotview.ScreenShot;
import com.truthower.suhang.mangareader.widget.shotview.ShotView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * /storage/sdcard0/reptile/one-piece
 * <p/>
 * Created by Administrator on 2016/4/4.
 */
public class ReadMangaActivity extends BaseActivity implements OnClickListener {
    private HackyViewPager mangaPager;
    private DiscreteSeekBar seekBar;
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
    private MangaEditDialog searchDialog;
    private MangaImgEditDialog mangaImgEditDialog;
    private ClipboardManager clip;//复制文本用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initUI();
        initProgressBar();
        Intent intent = getIntent();
        pathList = (ArrayList<String>) intent.getSerializableExtra("pathList");
        if (null == pathList || pathList.size() == 0) {
            isLocalManga = false;
        } else {
            isLocalManga = true;
            refresh();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_manga;
    }


    private void initProgressBar() {
        loadBar = new ProgressDialog(ReadMangaActivity.this);
        loadBar.setCancelable(false);
        loadBar.setTitle("读取中");
        loadBar.setMessage("每20页大约需耗时4秒");
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
                if (finalPosition >= 0) {
                    mangaPager.setCurrentItem(finalPosition - 1);
//                    cutSeekBar();
//                seekBar.setVisibility(View.GONE);
                }
            }
        });
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
        readProgressTv = (TextView) findViewById(R.id.read_progress_tv);

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
        topBar.setTitle(Configure.currentMangaName);
        topBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onRightClick() {
                if (shotView == null) {
                    shotView = (ShotView) findViewById(R.id.shot_view);
                    shotView.setL(new ShotView.FinishShotListener() {
                        @Override
                        public void finishShot(Bitmap bp) {
                            showImgEditDialog(bp);
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
                shotView.setBitmap(bgBitmap);
                shotView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTitleClick() {
                showSearchDialog();
            }

            @Override
            public void onLeftClick() {
                ReadMangaActivity.this.finish();
            }
        });
    }

    private void showSearchDialog() {
        if (null == searchDialog) {
            searchDialog = new MangaEditDialog(this);
            searchDialog.setOnPeanutEditDialogClickListener(new MangaEditDialog.OnPeanutEditDialogClickListener() {
                @Override
                public void onOkClick(String text) {
                    translateWord(text);
                }

                @Override
                public void onCancelClick() {

                }
            });
            searchDialog.setCancelable(true);
        }
        searchDialog.show();
        searchDialog.setTitle("查单词");
        searchDialog.setHint("输入单个单词");
        searchDialog.clearEdit();
    }

    private void showImgEditDialog(Bitmap bp) {
        if (null == mangaImgEditDialog) {
            mangaImgEditDialog = new MangaImgEditDialog(this);
            mangaImgEditDialog.setOnImgEditDialogListener(new MangaImgEditDialog.OnImgEditDialogListener() {
                @Override
                public void finish(String text) {
                    translateWord(text);
                }
            });
        }
        mangaImgEditDialog.show();
        mangaImgEditDialog.setImgRes(bp);
        mangaImgEditDialog.clearEdit();
    }

    private void translateWord(String text) {
        //TODO 翻译
        clip.setText(text);
        baseToast.showToast(text);
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
                    bp = loadImageFromNetwork(path);
                    if (null != bp) {
                        //TODO 把图片保存到本地
//                        FileUtil.saveBitmap(bp, Globle.mangaTitle + "_" + Globle.currentChapter
//                                + "_" + historyPosition + ".jpg", Globle.mangaTitle);
                    }
                }
            }
        }.start();
    }

    /**
     * 网络获取图片
     *
     * @param imageUrl
     * @return
     */
    private Bitmap loadImageFromNetwork(String imageUrl) {
        Bitmap bitmap = null;
        try {
            InputStream is = new URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Logger.d("下载失败" + e);
        }
        return bitmap;
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
        SharedPreferencesUtils.setSharedPreferencesData(this, Configure.currentMangaName + "progress",
                historyPosition);
    }

    private void recoverState() {
        int p = SharedPreferencesUtils.getIntSharedPreferencesData(this,
                Configure.currentMangaName + "progress");
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
        }
    }
}
