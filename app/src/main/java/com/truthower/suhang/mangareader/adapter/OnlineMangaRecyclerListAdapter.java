package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemLongClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2017/11/15.
 */
public class OnlineMangaRecyclerListAdapter extends RecyclerView.Adapter<OnlineMangaRecyclerListAdapter.ViewHolder> {
    private Context context;
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private OnRecycleItemLongClickListener onRecycleItemLongClickListener;
    private ArrayList<MangaBean> thumbleFailedList = new ArrayList<MangaBean>();
    private Map<String, MangaBean> failImgList = new HashMap<>();

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    private ArrayList<MangaBean> list = new ArrayList<>();

    public OnlineMangaRecyclerListAdapter(Context context) {
        this(context, null);
    }

    public OnlineMangaRecyclerListAdapter(Context context, ArrayList<MangaBean> list) {
        this.context = context;
        this.list = list;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_online_manga_recyler, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final MangaBean item = list.get(position);
        if (!TextUtils.isEmpty(item.getWebThumbnailUrl())) {
            ImageLoader.getInstance().displayImage(item.getWebThumbnailUrl(), viewHolder.mangaThumbnailIv, Configure.smallImageOptions);
            ImageLoader.getInstance().setDefaultLoadingListener(new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason reason) {
//                    if (thumbleFailedList.contains(item)){
//                        return;
//                    }
//                    thumbleFailedList.add(item);
                    if (TextUtils.isEmpty(s) || failImgList.containsKey(s))
                        return;
                    MangaBean mangaRes = null;
                    for (MangaBean manga : list) {
                        if (s.equals(manga.getWebThumbnailUrl())) {
                            mangaRes = manga;
                            break;
                        }
                    }
                    if (mangaRes != null)
                        failImgList.put(s, mangaRes);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        } else {
            ImageLoader.getInstance().displayImage(item.getLocalThumbnailUrl(), viewHolder.mangaThumbnailIv, Configure.smallImageOptions);
        }
        if (!TextUtils.isEmpty(item.getName())) {
            if (TextUtils.isEmpty(item.getLast_update())) {
                viewHolder.mangaTitleTv.setText(Html.fromHtml(item.getName()));
            } else {
                viewHolder.mangaTitleTv.setText(Html.fromHtml(item.getName()) + "\n" + item.getLast_update());
            }
        }
        viewHolder.item_collect_manga_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
        viewHolder.item_collect_manga_rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != onRecycleItemLongClickListener) {
                    onRecycleItemLongClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
    }

    public Map<String, MangaBean> getFailImgList() {
        return failImgList;
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    public void setOnRecycleItemLongClickListener(OnRecycleItemLongClickListener onRecycleItemLongClickListener) {
        this.onRecycleItemLongClickListener = onRecycleItemLongClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mangaThumbnailIv;
        public TextView mangaTitleTv;
        public RelativeLayout item_collect_manga_rl;

        public ViewHolder(View view) {
            super(view);
            item_collect_manga_rl = (RelativeLayout) view.findViewById(R.id.item_collect_manga_rl);
            mangaThumbnailIv = (ImageView) view.findViewById(R.id.manga_thumbnail_iv);
            mangaTitleTv = (TextView) view.findViewById(R.id.manga_title_tv);
        }
    }

    public ArrayList<MangaBean> getThumbleFailedList() {
        return thumbleFailedList;
    }

    public ArrayList<MangaBean> getList() {
        return list;
    }

    public void setList(ArrayList<MangaBean> list) {
        this.list = list;
    }
}
