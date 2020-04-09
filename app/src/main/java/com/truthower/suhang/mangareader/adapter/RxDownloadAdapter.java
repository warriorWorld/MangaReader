package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.bean.WordsBookBean;
import com.truthower.suhang.mangareader.listener.OnImgSizeListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemLongClickListener;
import com.truthower.suhang.mangareader.utils.ImageUtil;
import com.truthower.suhang.mangareader.widget.imageview.WrapPhotoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public class RxDownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<RxDownloadChapterBean> list = null;

    public RxDownloadAdapter(Context context) {
        this.mContext = context;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public NormalViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rxdownload, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        refreshItem(viewHolder, position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position, @NonNull List<Object> payloads) {
        //重写这个方法解决点击翻译图片闪烁问题 详见：https://blog.csdn.net/qq15357971925/article/details/78043332
        if (payloads.isEmpty()) {
            onBindViewHolder(viewHolder, position);
        } else {
            refreshItem(viewHolder, position);
        }
    }

    private void refreshItem(final RecyclerView.ViewHolder viewHolder, int position) {
        final RxDownloadChapterBean item = list.get(position);

        ((NormalViewHolder) viewHolder).chapterNameTv.setText("第" + item.getChapterName() + "话");
        if (item.getPageCount() == 0) {
            ((NormalViewHolder) viewHolder).progressTv.setText("等待下载");
        } else {
            ((NormalViewHolder) viewHolder).downloadProgressBar.setMax(item.getPageCount());
            int progress = item.getPageCount() - item.getPages().size();
            ((NormalViewHolder) viewHolder).downloadProgressBar.setProgress(progress);
            ((NormalViewHolder) viewHolder).progressTv.setText(progress + "/" + item.getPageCount());
        }
    }

    @Override
    public int getItemCount() {
        if (null == list) {
            return 0;
        } else {
            return list.size();
        }
    }

    public void setList(ArrayList<RxDownloadChapterBean> list) {
        this.list = list;
    }

    public ArrayList<RxDownloadChapterBean> getList() {
        return list;
    }

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        //必须让后边的刷新 因为上边那个notify是不会重新bindview的所以会使后边的view的position错误
        notifyItemRangeChanged(position, list.size() - position);
    }

    public void add(int position, RxDownloadChapterBean data) {
        list.add(position, data);
        notifyItemInserted(position);
        //必须让后边的刷新 因为上边那个notify是不会重新bindview的所以会使后边的view的position错误
        notifyItemRangeChanged(position, list.size() - position);
    }

    public void change(int position, RxDownloadChapterBean data) {
        list.remove(position);
        list.add(position, data);
        notifyItemChanged(position);
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private TextView chapterNameTv;
        private ProgressBar downloadProgressBar;
        private TextView progressTv;

        public NormalViewHolder(View view) {
            super(view);
            chapterNameTv = (TextView) view.findViewById(R.id.chapter_name_tv);
            downloadProgressBar = (ProgressBar) view.findViewById(R.id.download_progress_bar);
            progressTv = (TextView) view.findViewById(R.id.progress_tv);
        }
    }
}
