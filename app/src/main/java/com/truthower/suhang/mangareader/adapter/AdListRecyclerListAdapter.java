package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.AdBean;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/11/15.
 */
public class AdListRecyclerListAdapter extends RecyclerView.Adapter<AdListRecyclerListAdapter.ViewHolder> {
    private Context context;
    private OnRecycleItemClickListener onRecycleItemClickListener;

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    private ArrayList<AdBean> list = new ArrayList<>();

    public AdListRecyclerListAdapter(Context context) {
        this(context, null);
    }

    public AdListRecyclerListAdapter(Context context, ArrayList<AdBean> list) {
        this.context = context;
        this.list = list;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_advetising, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        AdBean item = list.get(position);
        viewHolder.adTitleTv.setText(item.getTitle());
        viewHolder.adSubtitleTv.setText(item.getSubtitle());
        String message = item.getMessage().replaceAll("\\\\n", "\n");
        viewHolder.adMessageTv.setText(item.getMessage());
        viewHolder.itemAdRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
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


    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView adIv;
        public TextView adTitleTv;
        public TextView adMessageTv;
        public TextView adSubtitleTv;
        public RelativeLayout itemAdRl;

        public ViewHolder(View view) {
            super(view);
            itemAdRl = (RelativeLayout) view.findViewById(R.id.item_ad_rl);
            adIv = (ImageView) view.findViewById(R.id.ad_iv);
            adTitleTv = (TextView) view.findViewById(R.id.ad_title_tv);
            adMessageTv = (TextView) view.findViewById(R.id.ad_message_tv);
            adSubtitleTv = (TextView) view.findViewById(R.id.ad_subtitle_tv);
        }
    }

    public ArrayList<AdBean> getList() {
        return list;
    }

    public void setList(ArrayList<AdBean> list) {
        this.list = list;
    }
}
