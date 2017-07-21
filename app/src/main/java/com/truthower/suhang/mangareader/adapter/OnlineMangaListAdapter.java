package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/21.
 */

public class OnlineMangaListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<MangaBean> list = new ArrayList<>();


    public OnlineMangaListAdapter(Context context) {
        this(context, null);
    }

    public OnlineMangaListAdapter(Context context, ArrayList<MangaBean> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_online_manga, parent, false);
            viewHolder.thumbnail_iv = (ImageView) convertView
                    .findViewById(R.id.thumbnail_iv);
            viewHolder.name_tv = (TextView) convertView
                    .findViewById(R.id.name_tv);
            convertView.setTag(viewHolder);
        } else {
            // 初始化过的话就直接获取
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MangaBean item = list.get(position);
        ImageLoader.getInstance().displayImage(item.getWebThumbnailUrl(), viewHolder.thumbnail_iv, Configure.normalImageOptions);
        viewHolder.name_tv.setText(item.getName());
        return convertView;
    }

    public ArrayList<MangaBean> getList() {
        return list;
    }

    public void setList(ArrayList<MangaBean> list) {
        this.list = list;
    }

    private class ViewHolder {
        private ImageView thumbnail_iv;
        private TextView name_tv;
    }
}
