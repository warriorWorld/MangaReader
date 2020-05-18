package com.truthower.suhang.mangareader.business.onlinedetail;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.OnlineMangaDetailsRecyclerAdapter;
import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.utils.UltimateTextSizeUtil;
import com.truthower.suhang.mangareader.widget.popupwindow.EasyPopupWindow;
import com.truthower.suhang.mangareader.widget.recyclerview.RecyclerGridDecoration;
import com.truthower.suhang.mangareader.widget.recyclerview.SpaceItemDecoration;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class OnlineDetailsActivity extends BaseActivity {
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
        mOnlineDetailVM.getMangaDetails(mangaUrl);
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
                (int) (DisplayUtil.dip2px(this,20)) -
                (int) (DisplayUtil.dip2px(this,60) * column)) / (column * (column - 1));
        ptfGridView.addItemDecoration(new SpaceItemDecoration(spaceWidth, column));

        ptfGridView.setLayoutManager(new GridLayoutManager(this, column));
        ptfGridView.setFocusableInTouchMode(false);
        ptfGridView.setFocusable(false);
//        ptfGridView.setHasFixedSize(true);
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
    }

    private void initViewModel() {
        mOnlineDetailVM = ViewModelProviders.of(this).get(OnlineDetailVM.class);
        mOnlineDetailVM.init(this);
        mOnlineDetailVM.getManga().observe(this, new Observer<MangaBean>() {
            @Override
            public void onChanged(MangaBean bean) {
                currentManga = bean;
                refreshUI();
                showDescription();
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

    private void showDescription() {
        String description = currentManga.getDescription();
        if (TextUtils.isEmpty(description)) {
            return;
        }
        EasyPopupWindow ppw = new EasyPopupWindow(this);
        ppw.adaptiveShowAsDropDown(thumbnailIv, 0, 0);
        ppw.setMessage(description);
        ppw.hideIKnowTv();
    }

    private void initRec() {
        try {
            if (null == adapter) {
                adapter = new OnlineMangaDetailsRecyclerAdapter(this);
                adapter.setList(currentManga.getChapters());
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
}
