package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.utils.SerializableSparseArray;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Administrator on 2017/11/15.
 */
public class OnlineMangaDetailsRecyclerAdapter extends RecyclerView.Adapter<OnlineMangaDetailsRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ChapterBean> list = new ArrayList<>();
    private int lastReadPosition = -1;
    private OnRecycleItemClickListener mOnRecycleItemClickListener;
    private SparseArray<RxDownloadChapterBean> cacheChapters;

    public OnlineMangaDetailsRecyclerAdapter(Context context) {
        this.context = context;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_manga_online, viewGroup, false);
        return new ViewHolder(view);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final ChapterBean item = list.get(position);
        if (lastReadPosition == position) {
            viewHolder.bookmarkIv.setVisibility(View.VISIBLE);
            viewHolder.chapterTv.setText(item.getChapterPosition());
        } else {
            viewHolder.bookmarkIv.setVisibility(View.GONE);
            viewHolder.chapterTv.setText(item.getChapterPosition());
        }

        if (null != cacheChapters && null != cacheChapters.get(Integer.valueOf(item.getChapterPosition()))) {
            viewHolder.chapterTv.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.chapterRl.setBackgroundResource(R.drawable.main_round_solid);
        } else {
            viewHolder.chapterTv.setTextColor(context.getResources().getColor(R.color.manga_reader));
            viewHolder.chapterRl.setBackgroundResource(R.drawable.rect_white_main_5);
        }
        viewHolder.chapterTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnRecycleItemClickListener) {
                    mOnRecycleItemClickListener.onItemClick(position);
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

    public void setList(ArrayList<ChapterBean> list) {
        this.list = list;
    }

    public void setLastReadPosition(int lastReadPosition) {
        this.lastReadPosition = lastReadPosition;
        notifyDataSetChanged();
    }

    public int getLastReadPosition() {
        return lastReadPosition;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        mOnRecycleItemClickListener = onRecycleItemClickListener;
    }

    public void setCacheChapters(SparseArray<RxDownloadChapterBean> cacheChapters) {
        this.cacheChapters = cacheChapters;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chapterTv;
        public View bookmarkIv;
        public View chapterRl;

        public ViewHolder(View view) {
            super(view);
            chapterTv = view.findViewById(R.id.chapter);
            bookmarkIv = view.findViewById(R.id.bookmark_iv);
            chapterRl = view.findViewById(R.id.chapter_rl);
        }
    }
}
