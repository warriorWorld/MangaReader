package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;

/**
 * Created by Administrator on 2017/7/22.
 */

public class OnlyTextListAdapter extends BaseAdapter {
    private Context context;
    private String[] list;


    public OnlyTextListAdapter(Context context) {
        this(context, null);
    }

    public OnlyTextListAdapter(Context context, String[] list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getCount() {
        if (null == list) {
            return 0;
        }
        return list.length;
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
                    R.layout.item_text, parent, false);
            viewHolder.text_tv = (TextView) convertView
                    .findViewById(R.id.text_tv);
            convertView.setTag(viewHolder);
        } else {
            // 初始化过的话就直接获取
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text_tv.setText(list[position]);
        return convertView;
    }

    public String[] getList() {
        return list;
    }

    public void setList(String[] list) {
        this.list = list;
    }

    private class ViewHolder {
        private TextView text_tv;
    }
}
