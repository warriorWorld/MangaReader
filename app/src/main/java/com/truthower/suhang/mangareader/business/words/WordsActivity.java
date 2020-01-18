package com.truthower.suhang.mangareader.business.words;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.SearchMangaRecyclerAdapter;
import com.truthower.suhang.mangareader.adapter.WordsAdapter;
import com.truthower.suhang.mangareader.base.DependencyInjectorIml;
import com.truthower.suhang.mangareader.base.TTSActivity;
import com.truthower.suhang.mangareader.bean.WordsBookBean;
import com.truthower.suhang.mangareader.bean.YoudaoResponse;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.search.SearchActivity;
import com.truthower.suhang.mangareader.business.wordsbook.WordsBookAdapter;
import com.truthower.suhang.mangareader.business.wordsbook.WordsBookView;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.db.DbAdapter;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.utils.ImageUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.VibratorUtil;
import com.truthower.suhang.mangareader.volley.VolleyCallBack;
import com.truthower.suhang.mangareader.volley.VolleyTool;
import com.truthower.suhang.mangareader.widget.dialog.TailorImgDialog;
import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * /storage/sdcard0/reptile/one-piece
 * <p/>
 * Created by Administrator on 2016/4/4.
 */
public class WordsActivity extends TTSActivity implements OnClickListener, WordsContract.View {
    private WordsAdapter adapter;
    private View emptyView;
    private TextView topBarRight, topBarLeft;
    private ArrayList<WordsBookBean> wordsList = new ArrayList<WordsBookBean>();
    private int nowPosition = 0;
    private ClipboardManager clip;//复制文本用
    private WordsContract.Presenter mPresenter;
    private RecyclerView wordsRcv;

    @Override
    public void displayWords(ArrayList<WordsBookBean> list) {
        wordsList = list;
        initRec();
    }

    @Override
    public void displayTranslate(String translate) {

    }

    @Override
    public void displayKillWord() {

    }

    @Override
    public void setPresenter(WordsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private enum OrderType {
        ORDER,
        RANDOM
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initUI();
        setPresenter(new WordsPresenter(this, new DependencyInjectorIml(), this));
        mPresenter.onViewCreated();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        text2Speech(wordsList.get(nowPosition).getWord());
    }

    private void refresh() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        text2Speech(wordsList.get(0).getWord());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            if (null == wordsList || wordsList.size() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            initRec();
            try {
                WordsBookBean item = wordsList.get(nowPosition);
                topBarRight.setText("查询次数:" + item.getTime());
                topBarLeft.setText("总计:" + wordsList.size() + "个生词,当前位置:" + (nowPosition + 1));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {

        }
    }


    private void initUI() {
        emptyView = findViewById(R.id.empty_view);
        topBarLeft = (TextView) findViewById(R.id.top_bar_left);
        topBarRight = (TextView) findViewById(R.id.top_bar_right);
        wordsRcv = findViewById(R.id.words_rcv);
        wordsRcv.setLayoutManager
                (new LinearLayoutMangerWithoutBug
                        (this, LinearLayoutManager.VERTICAL, false));
        wordsRcv.setFocusable(false);
        wordsRcv.setHasFixedSize(true);
        baseTopBar.setTitle("生词本");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_words;
    }


    private void initRec() {
        try {
            if (null == wordsList || wordsList.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new WordsAdapter(this);
                adapter.setList(wordsList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                    }
                });
                wordsRcv.setAdapter(adapter);
            } else {
                adapter.setList(wordsList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void translation(final String word) {
        clip.setText(word);
        text2Speech(word);
        String url = Configure.YOUDAO + word;
        HashMap<String, String> params = new HashMap<String, String>();
        VolleyCallBack<YoudaoResponse> callback = new VolleyCallBack<YoudaoResponse>() {

            @Override
            public void loadSucceed(YoudaoResponse result) {
//                if (null != result && result.getErrorCode() == 0) {
//                    YoudaoResponse.BasicBean item = result.getBasic();
//                    String t = "";
//                    if (null != item) {
//                        for (int i = 0; i < item.getExplains().size(); i++) {
//                            t = t + item.getExplains().get(i) + ";";
//                        }
//                        adapter.getNowView().setTranslate(result.getQuery() + " [" + item.getPhonetic() +
//                                "]: " + "\n" + t);
//                    } else {
//                        adapter.getNowView().setTranslate("没查到该词");
//                    }
//                } else {
//                    adapter.getNowView().setTranslate("网络连接失败");
//                }
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
                WordsActivity.this, url, params,
                YoudaoResponse.class, callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kill_btn:
                break;
        }
    }

}
