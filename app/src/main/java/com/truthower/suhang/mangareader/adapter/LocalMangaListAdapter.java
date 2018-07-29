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
import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/7/22.
 */

public class LocalMangaListAdapter extends BaseAdapter {
    private Context context;
    private boolean isInEditMode=false;
    private ArrayList<MangaBean> mangaList = new ArrayList<MangaBean>();

    public LocalMangaListAdapter(Context context) {
        this(context, null);
    }

    public LocalMangaListAdapter(Context context, ArrayList<MangaBean> mangaList) {
        this.context = context;
        this.mangaList = mangaList;
    }

    @Override
    public int getCount() {
        if (null == mangaList)
            return 0;
        return mangaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mangaList.get(position);
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
            viewHolder.editModeIv = (ImageView) convertView
                    .findViewById(R.id.edit_mode_iv);
            viewHolder.checkedIv = (ImageView) convertView
                    .findViewById(R.id.checked_iv);
            convertView.setTag(viewHolder);
        } else {
            // 初始化过的话就直接获取
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MangaBean item = mangaList.get(position);
        if (!TextUtils.isEmpty(item.getUserThumbnailUrl())) {
            ImageLoader.getInstance().displayImage(item.getUserThumbnailUrl(), viewHolder.manga_view, Configure.smallImageOptions);
        } else if (!TextUtils.isEmpty(item.getLocalThumbnailUrl())) {
            ImageLoader.getInstance().displayImage(item.getLocalThumbnailUrl(), viewHolder.manga_view, Configure.smallImageOptions);
        }
        viewHolder.manga_title.setText(item.getName());
        if (isInEditMode){
            viewHolder.editModeIv.setVisibility(View.VISIBLE);
        }else {
            viewHolder.editModeIv.setVisibility(View.GONE);
        }
        if (item.isChecked()&&isInEditMode){
            viewHolder.checkedIv.setVisibility(View.VISIBLE);
        }else {
            viewHolder.checkedIv.setVisibility(View.GONE);
        }
        return convertView;
    }

    public ArrayList<MangaBean> getMangaList() {
        return mangaList;
    }

    public void setMangaList(ArrayList<MangaBean> mangaList) {
        this.mangaList = mangaList;
    }

    public void setInEditMode(boolean inEditMode) {
        isInEditMode = inEditMode;
    }

    private class ViewHolder {
        private ImageView manga_view;
        private TextView manga_title;
        private ImageView editModeIv,checkedIv;
    }
}
