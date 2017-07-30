package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.ChapterBean;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/22.
 */

public class OneShotDetailsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ChapterBean> chapterList = new ArrayList<ChapterBean>();

    public OneShotDetailsAdapter(Context context) {
        this(context, null);
    }

    public OneShotDetailsAdapter(Context context, ArrayList<ChapterBean> mangaList) {
        this.context = context;
        this.chapterList = mangaList;
    }

    @Override
    public int getCount() {
        if (null == chapterList)
            return 0;
        return chapterList.size();
    }

    @Override
    public Object getItem(int position) {
        return chapterList.get(position);
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
                    R.layout.item_manga, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.manga_view = (ImageView) convertView
                    .findViewById(R.id.manga_view);
            viewHolder.manga_title = (TextView) convertView
                    .findViewById(R.id.manga_title);
            viewHolder.manga_title.setVisibility(View.GONE);

            convertView.setTag(viewHolder);
        } else {
            // 初始化过的话就直接获取
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChapterBean item = chapterList.get(position);
        if (!TextUtils.isEmpty(item.getChapterThumbnailUrl())) {
            ImageLoader.getInstance().displayImage(item.getChapterThumbnailUrl(), viewHolder.manga_view, Configure.normalImageOptions);
        }
        return convertView;
    }

    public ArrayList<ChapterBean> getChapterList() {
        return chapterList;
    }

    public void setChapterList(ArrayList<ChapterBean> chapterList) {
        this.chapterList = chapterList;
    }

    private class ViewHolder {
        private ImageView manga_view;
        private TextView manga_title;
    }
}
