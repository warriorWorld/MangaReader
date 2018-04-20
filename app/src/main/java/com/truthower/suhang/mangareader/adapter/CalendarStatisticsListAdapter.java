package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.StatisticsBean;
import com.truthower.suhang.mangareader.utils.NumberUtil;
import com.truthower.suhang.mangareader.utils.StringUtil;
import com.truthower.suhang.mangareader.utils.UltimateTextSizeUtil;
import com.truthower.suhang.mangareader.utils.WeekUtil;

import java.util.ArrayList;


/**
 * Created by Administrator on 2017/11/15.
 */
public class CalendarStatisticsListAdapter extends RecyclerView.Adapter<CalendarStatisticsListAdapter.ViewHolder> {
    private Context context;


    private ArrayList<StatisticsBean> list = new ArrayList<>();

    public CalendarStatisticsListAdapter(Context context) {
        this(context, null);
    }

    public CalendarStatisticsListAdapter(Context context, ArrayList<StatisticsBean> list) {
        this.context = context;
        this.list = list;
    }


    //创建新View，被LayoutManager所调用
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_statistics, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        StatisticsBean item = list.get(position);
        viewHolder.book_title_tv.setText(item.getManga_name());
        viewHolder.query_word_c_tv.setText
                (UltimateTextSizeUtil.getEmphasizedSpannableString("昨日查词量(个):  " +
                                item.getQuery_word_c(), item.getQuery_word_c() + "",
                        0, context.getResources().getColor(R.color.manga_reader), 0));
        viewHolder.query_word_r_tv.setText(
                UltimateTextSizeUtil.getEmphasizedSpannableString("昨日查词率(个/百页):  " + NumberUtil.doubleDecimals(item.getQuery_word_r())
                        , NumberUtil.doubleDecimals(item.getQuery_word_r()),
                        0, context.getResources().getColor(R.color.manga_reader), 0));
        viewHolder.read_word_c_tv.setText(UltimateTextSizeUtil.getEmphasizedSpannableString("昨日阅读量(页):  " + item.getRead_page()
                , item.getRead_page() + "",
                0, context.getResources().getColor(R.color.manga_reader), 0));
        viewHolder.date_tv.setText(WeekUtil.getDateStringWithDate(item.getCreate_at()));
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
        public RelativeLayout item_book_rl;
        public TextView book_title_tv, read_progress_tv, query_word_c_tv, query_word_r_tv, read_word_c_tv, date_tv;

        public ViewHolder(View view) {
            super(view);
            item_book_rl = (RelativeLayout) view.findViewById(R.id.item_book_rl);
            book_title_tv = (TextView) view
                    .findViewById(R.id.book_title_tv);
            read_progress_tv = (TextView) view
                    .findViewById(R.id.read_progress_tv);
            query_word_c_tv = (TextView) view
                    .findViewById(R.id.query_word_c_tv);
            query_word_r_tv = (TextView) view
                    .findViewById(R.id.query_word_r_tv);
            read_word_c_tv = (TextView) view
                    .findViewById(R.id.read_word_c_tv);
            date_tv = (TextView) view
                    .findViewById(R.id.date_tv);
        }
    }

    public ArrayList<StatisticsBean> getList() {
        return list;
    }

    public void setList(ArrayList<StatisticsBean> list) {
        this.list = list;
    }
}
