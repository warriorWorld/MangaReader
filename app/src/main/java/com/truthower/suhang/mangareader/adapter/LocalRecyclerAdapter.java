package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemLongClickListener;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Administrator on 2017/11/15.
 */
public class LocalRecyclerAdapter extends RecyclerView.Adapter<LocalRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<MangaBean> list;
    private boolean isInEditMode = false;
    private OnRecycleItemClickListener mOnRecycleItemClickListener;
    private OnRecycleItemLongClickListener mOnRecycleItemLongClickListener;

    public LocalRecyclerAdapter(Context context) {
        this.context = context;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_manga, viewGroup, false);
        return new ViewHolder(view);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final MangaBean item = list.get(position);
        if (!TextUtils.isEmpty(item.getUserThumbnailUrl())) {
            Glide.with(context).load(item.getUserThumbnailUrl()).apply(Configure.ROUND_CORNERS_OPTIONS).into(viewHolder.mangaView);
        } else if (!TextUtils.isEmpty(item.getLocalThumbnailUrl())) {
            Glide.with(context).load(item.getLocalThumbnailUrl()).apply(Configure.ROUND_CORNERS_OPTIONS).into(viewHolder.mangaView);
        }
        viewHolder.mangaTitle.setText(item.getName());
        if (isInEditMode) {
            viewHolder.editModeIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editModeIv.setVisibility(View.GONE);
        }
        if (item.isChecked() && isInEditMode) {
            viewHolder.checkedIv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.checkedIv.setVisibility(View.GONE);
        }
        viewHolder.mangaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnRecycleItemClickListener) {
                    mOnRecycleItemClickListener.onItemClick(position);
                }
            }
        });
        viewHolder.mangaView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mOnRecycleItemLongClickListener) {
                    mOnRecycleItemLongClickListener.onItemLongClick(position);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    public void setList(List<MangaBean> list) {
        this.list = list;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        mOnRecycleItemClickListener = onRecycleItemClickListener;
    }

    public void setInEditMode(boolean inEditMode) {
        isInEditMode = inEditMode;
    }

    public void setOnRecycleItemLongClickListener(OnRecycleItemLongClickListener onRecycleItemLongClickListener) {
        mOnRecycleItemLongClickListener = onRecycleItemLongClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mangaView;
        private ImageView editModeIv;
        private ImageView checkedIv;
        private TextView mangaTitle;

        public ViewHolder(View view) {
            super(view);
            mangaView = (ImageView) view.findViewById(R.id.manga_view);
            editModeIv = (ImageView) view.findViewById(R.id.edit_mode_iv);
            checkedIv = (ImageView) view.findViewById(R.id.checked_iv);
            mangaTitle = (TextView) view.findViewById(R.id.manga_title);
        }
    }
}
