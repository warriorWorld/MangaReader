package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.CalendarBean;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by Administrator on 2017/11/15.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {
    private Context context;
    private boolean hideOtherMonthDay = false;
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private int selectedPosition = -1;

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    public enum DateState {
        NORMAL,
        //选中状态
        SELECTED,
        //浅色的选中颜色
        HALF_SELECTED,
        //不是本月的日
        OTHER_MONTH
    }

    private ArrayList<CalendarBean> dateList = new ArrayList<>();

    public CalendarAdapter(Context context) {
        this(context, null);
    }

    public CalendarAdapter(Context context, ArrayList<CalendarBean> list) {
        this.context = context;
        this.dateList = list;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_calendar_only_text, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        CalendarBean item = dateList.get(position);
        viewHolder.dateTv.setText(item.getDayOfMonth() + "");
        viewHolder.onlyTextItemRv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                    selectedPosition = position;
                    notifyDataSetChanged();
                }
            }
        });
        if (null != onRecycleItemClickListener) {
            if (position == selectedPosition) {
                //这个是手动选择的
                viewHolder.dateTv.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.dateTv.setBackgroundResource(R.drawable.calendar_circle_orange);
            } else {
                viewHolder.dateTv.setTextColor(context.getResources().getColor(R.color.main_text_color));
                viewHolder.dateTv.setBackgroundResource(R.color.transparency);
            }
            if (item.getDateState()== DateState.OTHER_MONTH){
                viewHolder.onlyTextItemRv.setVisibility(View.GONE);
            }else {
                viewHolder.onlyTextItemRv.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.onlyTextItemRv.setVisibility(View.VISIBLE);
            switch (item.getDateState()) {
                case NORMAL:
                    viewHolder.dateTv.setTextColor(context.getResources().getColor(R.color.main_text_color));
                    viewHolder.dateTv.setBackgroundResource(R.color.transparency);
                    break;
                case SELECTED:
                    viewHolder.dateTv.setTextColor(context.getResources().getColor(R.color.white));
                    viewHolder.dateTv.setBackgroundResource(R.drawable.calendar_circle_orange);
                    break;
                case HALF_SELECTED:
                    viewHolder.dateTv.setTextColor(context.getResources().getColor(R.color.white));
                    viewHolder.dateTv.setBackgroundResource(R.drawable.calendar_circle_orange_shallow);
                    break;
                case OTHER_MONTH:
                    if (hideOtherMonthDay) {
                        viewHolder.onlyTextItemRv.setVisibility(View.GONE);
                    } else {
                        viewHolder.onlyTextItemRv.setVisibility(View.VISIBLE);
                    }
                    viewHolder.dateTv.setTextColor(context.getResources().getColor(R.color.main_text_color_gray));
                    viewHolder.dateTv.setBackgroundResource(R.color.transparency);
                    break;
            }
        }
    }

    public void resetSelectedPosition(){
        selectedPosition=-1;
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        if (null == dateList) {
            return 0;
        }
        return dateList.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTv;
        public RelativeLayout onlyTextItemRv;

        public ViewHolder(View view) {
            super(view);
            onlyTextItemRv = (RelativeLayout) view.findViewById(R.id.only_text_item_rv);
            dateTv = (TextView) view.findViewById(R.id.text_tv);
        }
    }

    public void setHideOtherMonthDay(boolean hide) {
        this.hideOtherMonthDay = hide;
    }

    public ArrayList<CalendarBean> getDateList() {
        return dateList;
    }

    public void setDateList(ArrayList<CalendarBean> dateList) {
        this.dateList = dateList;
    }
}
