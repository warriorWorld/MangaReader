package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.WordsBookBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.listener.OnImgSizeListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemLongClickListener;
import com.truthower.suhang.mangareader.utils.ImageUtil;
import com.truthower.suhang.mangareader.widget.imageview.WrapPhotoView;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public class WordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<WordsBookBean> list = null;
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private OnRecycleItemClickListener onTranslateItemClickListener;
    private OnRecycleItemLongClickListener mOnRecycleItemLongClickListener;
    private int currentWidth = 0;
    private HashMap<String, int[]> sizeHash = new HashMap<>();
    private HashMap<String, Bitmap> bpHash = new HashMap<>();

    public WordsAdapter(Context context) {
        this.mContext = context;
    }

    //创建新View，被LayoutManager所调用
    @Override
    public NormalViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_word, viewGroup, false);
        NormalViewHolder vh = new NormalViewHolder(view);
        return vh;
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        final WordsBookBean item = list.get(position);
        ((NormalViewHolder) viewHolder).wordTv.setText(item.getWord() + "(" + item.getTime() + ")");
        ((NormalViewHolder) viewHolder).translateTv.setText(item.getTranslate());
        if (TextUtils.isEmpty(item.getTranslate())) {
            ((NormalViewHolder) viewHolder).translateTv.setBackgroundColor(mContext.getResources().getColor(R.color.divide_line));
            ((NormalViewHolder) viewHolder).translateTv.setTextColor(mContext.getResources().getColor(R.color.transparency));
        } else {
            ((NormalViewHolder) viewHolder).translateTv.setBackgroundColor(mContext.getResources().getColor(R.color.transparency));
            ((NormalViewHolder) viewHolder).translateTv.setTextColor(mContext.getResources().getColor(R.color.main_text_color_gray));
        }
        ((NormalViewHolder) viewHolder).translateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((NormalViewHolder) viewHolder).translateTv.getCurrentTextColor() == mContext.getResources().getColor(R.color.main_text_color_gray)) {
                    ((NormalViewHolder) viewHolder).translateTv.setBackgroundColor(mContext.getResources().getColor(R.color.divide_line));
                    ((NormalViewHolder) viewHolder).translateTv.setTextColor(mContext.getResources().getColor(R.color.transparency));
                } else {
                    ((NormalViewHolder) viewHolder).translateTv.setBackgroundColor(mContext.getResources().getColor(R.color.transparency));
                    ((NormalViewHolder) viewHolder).translateTv.setTextColor(mContext.getResources().getColor(R.color.main_text_color_gray));
                }
                if (null != onTranslateItemClickListener && TextUtils.isEmpty(item.getTranslate())) {
                    onTranslateItemClickListener.onItemClick(position);
                }
            }
        });
        ((NormalViewHolder) viewHolder).translateTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mOnRecycleItemLongClickListener) {
                    mOnRecycleItemLongClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
        if (TextUtils.isEmpty(item.getExample_path())) {
            ((NormalViewHolder) viewHolder).wordIv.setVisibility(View.GONE);
        } else {
            ((NormalViewHolder) viewHolder).wordIv.setVisibility(View.VISIBLE);
            Bitmap bitmap;
            if (bpHash.containsKey(item.getExample_path())) {
                bitmap = bpHash.get(item.getExample_path());
            } else {
                bitmap = ImageUtil.getLoacalBitmap(item.getExample_path()); //从本地取图片(在cdcard中获取)  //
                bpHash.put(item.getExample_path(), bitmap);
            }
            if (sizeHash.containsKey(item.getExample_path())) {
                ((NormalViewHolder) viewHolder).wordIv.setBitmap(bitmap, sizeHash.get(item.getExample_path())[0], sizeHash.get(item.getExample_path())[1]);
            } else {
                ((NormalViewHolder) viewHolder).wordIv.setBitmap(bitmap, new OnImgSizeListener() {
                    @Override
                    public void onSized(int width, int height) {
                        sizeHash.put(item.getExample_path(), new int[]{width, height});
                    }
                });
            }
        }
        ((NormalViewHolder) viewHolder).killTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRecycleItemClickListener) {
                    onRecycleItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == list) {
            return 0;
        } else {
            return list.size();
        }
    }

    public void setList(ArrayList<WordsBookBean> list) {
        this.list = list;
    }

    public ArrayList<WordsBookBean> getList() {
        return list;
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        this.onRecycleItemClickListener = onRecycleItemClickListener;
    }

    public void setOnRecycleItemLongClickListener(OnRecycleItemLongClickListener onRecycleItemLongClickListener) {
        mOnRecycleItemLongClickListener = onRecycleItemLongClickListener;
    }

    public void setCurrentWidth(int currentWidth) {
        this.currentWidth = currentWidth;
    }

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        //必须让后边的刷新 因为上边那个notify是不会重新bindview的所以会使后边的view的position错误
        notifyItemRangeChanged(position, list.size() - position);
    }

    public void add(int position, WordsBookBean data) {
        list.add(position, data);
        notifyItemInserted(position);
        //必须让后边的刷新 因为上边那个notify是不会重新bindview的所以会使后边的view的position错误
        notifyItemRangeChanged(position, list.size() - position);
    }

    public void change(int position, WordsBookBean data) {
        list.remove(position);
        list.add(position, data);
        notifyItemChanged(position);
    }

    public void setOnTranslateItemClickListener(OnRecycleItemClickListener onTranslateItemClickListener) {
        this.onTranslateItemClickListener = onTranslateItemClickListener;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout wordRl;
        public WrapPhotoView wordIv;
        public TextView wordTv;
        public TextView translateTv;
        public TextView killTv;

        public NormalViewHolder(View view) {
            super(view);
            wordRl = (RelativeLayout) view.findViewById(R.id.word_rl);
            wordIv = (WrapPhotoView) view.findViewById(R.id.word_iv);
            wordTv = (TextView) view.findViewById(R.id.word_tv);
            translateTv = (TextView) view.findViewById(R.id.translate_tv);
            killTv = (TextView) view.findViewById(R.id.kill_tv);
        }
    }
}
