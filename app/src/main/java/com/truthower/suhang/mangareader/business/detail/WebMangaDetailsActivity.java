package com.truthower.suhang.mangareader.business.detail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.widget.bar.TopBar;
import com.truthower.suhang.mangareader.widget.wheelview.wheelselector.WheelSelectorDialog;

public class WebMangaDetailsActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    private GridView mangaGV;
    private View collectV;
    private boolean chooseing = false;//判断是否在选择状态
    private boolean firstChoose = true;

    private MangaBean currentManga;
    private OnlineMangaDetailAdapter adapter;
    private ImageView thumbnailIV;
    private TextView mangaNameTv, mangaAuthorTv, mangaTypeTv, lastUpdateTv;
    private String[] optionsList = {"下载全部", "选择起始点下载"};
    private ProgressDialog progressBar;
    private WheelSelectorDialog optionsSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String mangaUrl = intent.getStringExtra("mangaUrl");
        if (TextUtils.isEmpty(mangaUrl)) {
            this.finish();
        }
        initUI();
        initProgressBar();
        initWebManga(mangaUrl);
    }

    private void initProgressBar() {
        progressBar = new ProgressDialog(WebMangaDetailsActivity.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("加载中...");
    }

    private void initWebManga(String url) {
        progressBar.show();

    }

    private void refreshUI() {
        baseTopBar.setTitle(currentManga.getName());
        ImageLoader.getInstance().displayImage(currentManga.getWebThumbnailUrl(), thumbnailIV, Configure.normalImageOptions);
        mangaNameTv.setText("漫画名称:" + currentManga.getName());
        mangaAuthorTv.setText("作者:" + currentManga.getAuthor());
        //TODO 多类型 可点击
        mangaTypeTv.setText("类型:" + currentManga.getTypes()[0]);
        lastUpdateTv.setText("最后更新:" + currentManga.getLast_update());
        toggleCollect();

        initGridView();
    }

    private void initGridView() {
        if (null == adapter) {
            adapter = new OnlineMangaDetailAdapter(this, currentManga.getChapters());
            mangaGV.setAdapter(adapter);
            mangaGV.setOnItemClickListener(this);
        } else {
            adapter.setChapters(currentManga.getChapters());
            adapter.notifyDataSetChanged();
        }
    }

    private void initUI() {
        mangaGV = (GridView) findViewById(R.id.manga_gv);
        thumbnailIV = (ImageView) findViewById(R.id.thumbnail);
        mangaNameTv = (TextView) findViewById(R.id.manga_name);
        mangaAuthorTv = (TextView) findViewById(R.id.manga_author);
        mangaTypeTv = (TextView) findViewById(R.id.manga_type);
        lastUpdateTv = (TextView) findViewById(R.id.manga_update_date);
        collectV = findViewById(R.id.collect_view);

        collectV.setOnClickListener(this);
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manga_details_web;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (chooseing) {
//            if (firstChoose) {
//                Globle.startPoint = Integer.valueOf(mangaList.get(position).getTitle());
//                firstChoose = false;
//            } else {
//                Globle.mangaName = Globle.mangaTitle;
//                Globle.endPoint = Integer.valueOf(mangaList.get(position).getTitle());
//                Globle.friendlyDownload = true;
//                Intent intent = new Intent(WebMangaDetailsActivity.this, ReptileMangaReaderActivity.class);
//                startActivity(intent);
//                WebMangaDetailsActivity.this.finish();
//            }
//        } else {
//            Globle.sourceFrom = Globle.Source.web;
//            Globle.mangaPath = mangaList.get(position).getPath();
//            Globle.currentChapter = position + "";
//            Intent intent = new Intent(WebMangaDetailsActivity.this, ReadMangaActivity.class);
//            startActivity(intent);
//        }
    }

    private void downloadAll() {
//        Globle.startPoint = 1;
//        Globle.mangaName = Globle.mangaTitle;
//        Globle.endPoint = Integer.valueOf(mangaList.get(mangaList.size() - 1).getTitle());
//        Globle.friendlyDownload = true;
//        Intent intent = new Intent(WebMangaDetailsActivity.this, ReptileMangaReaderActivity.class);
//        startActivity(intent);
//        WebMangaDetailsActivity.this.finish();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.collect_view:
                toggleCollect();
                break;
        }
    }

    private void toggleCollect() {
        if (currentManga.isCollected()) {
            collectV.setBackgroundResource(R.drawable.collected);
        } else {
            collectV.setBackgroundResource(R.drawable.collect);
        }
    }
}
