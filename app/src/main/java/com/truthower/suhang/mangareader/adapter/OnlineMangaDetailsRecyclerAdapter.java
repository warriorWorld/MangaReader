package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.ChapterBean;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Administrator on 2017/11/15.
 */
public class OnlineMangaDetailsRecyclerAdapter extends RecyclerView.Adapter<OnlineMangaDetailsRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ChapterBean> list = new ArrayList<>();
    private int lastReadPosition = -1;

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
            viewHolder.chapterTv.setText("");
        } else {
            viewHolder.bookmarkIv.setVisibility(View.GONE);
            viewHolder.chapterTv.setText(item.getChapterPosition());
        }
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

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chapterTv;
        public View bookmarkIv;

        public ViewHolder(View view) {
            super(view);
            chapterTv = view.findViewById(R.id.chapter);
            bookmarkIv = view.findViewById(R.id.bookmark_iv);
        }
    }
}
