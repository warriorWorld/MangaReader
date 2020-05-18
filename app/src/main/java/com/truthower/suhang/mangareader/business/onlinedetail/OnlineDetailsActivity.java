package com.truthower.suhang.mangareader.business.onlinedetail;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.DownloadRecyclerAdapter;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailsRecyclerAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.business.threadpooldownload.TpDownloadActivity;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.UltimateTextSizeUtil;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class OnlineDetailsActivity extends BaseActivity {
    private RelativeLayout mangaInfoRl;
    private ImageView thumbnailIv;
    private TextView nameTv;
    private TextView authorTv;
    private TextView typeTv;
    private TextView updateTimeTv;
    private View collectView;
    private RecyclerView ptfGridView;
    private OnlineDetailVM mOnlineDetailVM;
    private MangaBean currentManga;
    private OnlineMangaDetailsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initViewModel();
    }

    private void initUI() {
        mangaInfoRl = (RelativeLayout) findViewById(R.id.manga_info_rl);
        thumbnailIv = (ImageView) findViewById(R.id.thumbnail_iv);
        nameTv = (TextView) findViewById(R.id.name_tv);
        authorTv = (TextView) findViewById(R.id.author_tv);
        typeTv = (TextView) findViewById(R.id.type_tv);
        updateTimeTv = (TextView) findViewById(R.id.update_time_tv);
        collectView = (View) findViewById(R.id.collect_view);
        ptfGridView = (RecyclerView) findViewById(R.id.ptf_grid_view);
        if (Configure.isPad) {
            ptfGridView.setLayoutManager(new StaggeredGridLayoutManager(8, StaggeredGridLayoutManager.VERTICAL));
        } else {
            ptfGridView.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
        }
        ptfGridView.setFocusableInTouchMode(false);
        ptfGridView.setFocusable(false);
        ptfGridView.setHasFixedSize(true);
    }

    private void initViewModel() {
        mOnlineDetailVM = ViewModelProviders.of(this).get(OnlineDetailVM.class);
        mOnlineDetailVM.init(this);
        mOnlineDetailVM.getManga().observe(this, new Observer<MangaBean>() {
            @Override
            public void onChanged(MangaBean bean) {
                currentManga = bean;
                refreshUI();
//                showDescription();
            }
        });
        mOnlineDetailVM.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

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
                    baseToast.showToast("收藏成功");
                    collectView.setBackgroundResource(R.drawable.collected);
                } else {
                    baseToast.showToast("取消收藏");
                    collectView.setBackgroundResource(R.drawable.collect);
                }
            }
        });
        mOnlineDetailVM.getIsForAdult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

            }
        });
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

    private void initRec() {
        try {
            if (null == adapter) {
                adapter = new OnlineMangaDetailsRecyclerAdapter(this);
                adapter.setList(currentManga.getChapters());
                ptfGridView.setAdapter(adapter);
                ColorDrawable dividerDrawable = new ColorDrawable(0x00000000) {
                    @Override
                    public int getIntrinsicHeight() {
                        return DisplayUtil.dip2px(OnlineDetailsActivity.this, 8);
                    }

                    @Override
                    public int getIntrinsicWidth() {
                        return DisplayUtil.dip2px(OnlineDetailsActivity.this, 2);
                    }
                };
                RecyclerGridDecoration itemDecoration = new RecyclerGridDecoration(OnlineDetailsActivity.this,
                        dividerDrawable, true);
                ptfGridView.addItemDecoration(itemDecoration);
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
}
