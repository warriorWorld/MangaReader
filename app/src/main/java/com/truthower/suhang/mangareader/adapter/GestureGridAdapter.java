package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public class GestureGridAdapter extends BaseRecyclerAdapter {
    private ArrayList<Boolean> datas = new ArrayList<>();

    public GestureGridAdapter(Context context) {
        super(context);
    }

    @Override
    protected RecyclerView.ViewHolder getEmptyViewHolder(ViewGroup viewGroup) {
        return null;
    }

    @Override
    protected String getEmptyText() {
        return "";
    }

    @Override
    protected String getEmptyBtnText() {
        return "";
    }

    @Override
    protected int getEmptyImg() {
        return R.drawable.empty_list;
    }

    @Override
    protected String getListEndText() {
        return "";
    }

    @Override
    protected RecyclerView.ViewHolder getNormalViewHolder(ViewGroup viewGroup) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gesture_grid, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    @Override
    protected void refreshNormalViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        Boolean item = datas.get(position);
        if (item) {
            ((NormalViewHolder) viewHolder).itemGestureIv.setImageResource(R.drawable.circle_blue_fill);
        } else {
            ((NormalViewHolder) viewHolder).itemGestureIv.setImageResource(R.drawable.circle_blue_fill1);
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemGestureIv;

        public NormalViewHolder(View view) {
            super(view);
            itemGestureIv = (ImageView) view.findViewById(R.id.item_gesture_iv);
        }
    }


    public ArrayList<Boolean> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<Boolean> datas) {
        this.datas = datas;
    }
}
