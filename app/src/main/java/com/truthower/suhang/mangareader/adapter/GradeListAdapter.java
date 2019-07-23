package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.GradeBean;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.utils.WeekUtil;
import com.truthower.suhang.mangareader.widget.layout.StarLinearlayout;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public class GradeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<GradeBean> datas = null;
    private final int TYPE_NORMAL = 0;
    private final int TYPE_EMPTY = 1;
    private OnRecycleItemClickListener onRecycleItemClickListener;

    public GradeListAdapter(Context context, ArrayList<GradeBean> datas) {
        this.datas = datas;
        this.context = context;
    }

    // 创建新View，被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_grade, viewGroup, false);
            NormalViewHolder vh = new NormalViewHolder(view);
            return vh;
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.emptyview_list, viewGroup, false);
            EmptyViewHolder emptyViewHolder = new EmptyViewHolder(view);
            return emptyViewHolder;
        }
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof NormalViewHolder) {
            final GradeBean item = datas.get(position);
            ((NormalViewHolder) viewHolder).mangaNameTv.setText(item.getMangaName());
            ((NormalViewHolder) viewHolder).gradeDateTv.setText(WeekUtil.getDateDetailStringWithDate(item.getCreate_at()));
            ((NormalViewHolder) viewHolder).gradeStarSll.setStarNum(Float.valueOf(item.getStar()));
            ((NormalViewHolder) viewHolder).itemGradeRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onRecycleItemClickListener) {
                        onRecycleItemClickListener.onItemClick(position);
                    }
                }
            });
        } else if (viewHolder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) viewHolder).emptyText.setText("还没有人评分过!");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (null == datas || datas.size() == 0) {
            return TYPE_EMPTY;
        } else {
            return TYPE_NORMAL;
        }
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        if (null == datas || datas.size() == 0) {
            return 1;
        }
        return datas.size();
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout itemGradeRl;
        private TextView mangaNameTv;
        private StarLinearlayout gradeStarSll;
        private TextView gradeDateTv;

        public NormalViewHolder(View view) {
            super(view);
            itemGradeRl = (RelativeLayout) view.findViewById(R.id.item_grade_rl);
            mangaNameTv = (TextView) view.findViewById(R.id.manga_name_tv);
            gradeStarSll = (StarLinearlayout) view.findViewById(R.id.grade_star_sll);
            gradeStarSll.setMaxStar(5);
            gradeDateTv = (TextView) view.findViewById(R.id.grade_date_tv);
        }
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class EmptyViewHolder extends RecyclerView.ViewHolder {
        private ImageView emptyImage;
        private TextView emptyText;

        public EmptyViewHolder(View view) {
            super(view);
            emptyImage = (ImageView) view.findViewById(R.id.empty_image);
            emptyText = (TextView) view.findViewById(R.id.empty_text);
        }

    }

    public ArrayList<GradeBean> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<GradeBean> datas) {
        this.datas = datas;
    }
}
