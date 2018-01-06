package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.ChapterBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/22.
 */

public class OnlineMangaDetailAdapter extends BaseAdapter {
    private Context context;
    private ImageSize imageSize;
    private ArrayList<ChapterBean> chapters = new ArrayList<>();
    private int lastReadPosition = -1;

    public OnlineMangaDetailAdapter(Context context) {
        this(context, null);
    }

    public OnlineMangaDetailAdapter(Context context, ArrayList<ChapterBean> chapters) {
        this.context = context;
        this.chapters = chapters;
        imageSize = new ImageSize(240, 400);
    }

    @Override
    public int getCount() {
        if (null == chapters) {
            return 0;
        }
        return chapters.size();
    }

    @Override
    public Object getItem(int position) {
        return chapters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_manga_web, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.manga_title = (TextView) convertView
                    .findViewById(R.id.chapter);
            viewHolder.bookMarkIv = (ImageView)
                    convertView.findViewById(R.id.bookmark_iv);
            convertView.setTag(viewHolder);
        } else {
            // 初始化过的话就直接获取
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChapterBean item = chapters.get(position);

        if (lastReadPosition == position) {
            viewHolder.bookMarkIv.setVisibility(View.VISIBLE);
            viewHolder.manga_title.setText("");
        } else {
            viewHolder.bookMarkIv.setVisibility(View.GONE);
            viewHolder.manga_title.setText("第" + item.getChapterPosition() + "话");
        }
        return convertView;
    }

    public ArrayList<ChapterBean> getChapters() {
        return chapters;
    }

    public void setChapters(ArrayList<ChapterBean> chapters) {
        this.chapters = chapters;
    }

    public int getLastReadPosition() {
        return lastReadPosition;
    }

    public void setLastReadPosition(int lastReadPosition) {
        this.lastReadPosition = lastReadPosition;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        private TextView manga_title;
        private ImageView bookMarkIv;
    }
}
