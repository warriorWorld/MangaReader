package com.truthower.suhang.mangareader.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Administrator on 2017/11/15.
 */
public class StarAdapter extends RecyclerView.Adapter<StarAdapter.ViewHolder> {
    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    public enum StarState {
        STAR,
        UNSTAR,
        HALF_STAR
    }

    private OnRecycleItemClickListener onRecycleItemClickListener;
    private ArrayList<StarState> starStates = new ArrayList<>();

    public StarAdapter() {
    }

    public StarAdapter(ArrayList<StarState> list) {
        this.starStates = list;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_star, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        StarState state = starStates.get(position);
        switch (state) {
            case STAR:
                viewHolder.starIv.setImageResource(R.drawable.star_yellow);
                break;
            case UNSTAR:
                viewHolder.starIv.setImageResource(R.drawable.star_gray);
                break;
            case HALF_STAR:
                //目前没有
                break;
        }
        viewHolder.starIv.setOnClickListener(new View.OnClickListener() {
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
        if (null == starStates) {
            return 0;
        }
        return starStates.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView starIv;

        public ViewHolder(View view) {
            super(view);
            starIv = (ImageView) view.findViewById(R.id.star_iv);
        }
    }

    public ArrayList<StarState> getStarStates() {
        return starStates;
    }

    public void setStarStates(ArrayList<StarState> starStates) {
        this.starStates = starStates;
    }
}
