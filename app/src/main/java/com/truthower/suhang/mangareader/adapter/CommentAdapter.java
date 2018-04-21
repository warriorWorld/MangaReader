package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.bean.CommentBean;
import com.truthower.suhang.mangareader.listener.OnCommenttemClickListener;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.utils.WeekUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/15.
 * 还款页的还款计划
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<CommentBean> datas = null;
    private final int TYPE_NORMAL = 0;
    private final int TYPE_EMPTY = 1;
    private OnRecycleItemClickListener onRecycleItemClickListener;
    private OnCommenttemClickListener onCommenttemClickListener;
    private boolean isUserCenter = false;

    public CommentAdapter(Context context, ArrayList<CommentBean> datas) {
        this.datas = datas;
        this.context = context;
    }

    // 创建新View，被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false);
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
            final CommentBean item = datas.get(position);
            ((NormalViewHolder) viewHolder).userNameTv.setText(item.getOwner());
            if (item.getOwner().equals("智障的我")) {
                ((NormalViewHolder) viewHolder).userNameTv.setTextColor(context.getResources().getColor(R.color.manga_reader_deep));
            } else {
                ((NormalViewHolder) viewHolder).userNameTv.setTextColor(context.getResources().getColor(R.color.manga_reader));
            }
            ((NormalViewHolder) viewHolder).userNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onCommenttemClickListener) {
                        onCommenttemClickListener.onUserNameClick(position);
                    }
                }
            });
            ((NormalViewHolder) viewHolder).commentContentTv.setText(item.getComment_content());
            if (item.isHot()) {
                ((NormalViewHolder) viewHolder).commentContentTv.setTextColor(context.getResources().getColor(R.color.manga_reader));
            } else {
                ((NormalViewHolder) viewHolder).commentContentTv.setTextColor(context.getResources().getColor(R.color.main_text_color));
            }
            ((NormalViewHolder) viewHolder).commentDateTv.setText(WeekUtil.getDateDetailStringWithDate(item.getCreate_at()));
            if (isUserCenter) {
                ((NormalViewHolder) viewHolder).replyTv.setTextColor(context.getResources().getColor(R.color.manga_reader));
                ((NormalViewHolder) viewHolder).replyTv.setText(item.getMangaName());
            } else {
                ((NormalViewHolder) viewHolder).replyTv.setTextColor(context.getResources().getColor(R.color.main_text_color));
                ((NormalViewHolder) viewHolder).replyTv.setText("回复");
            }
            ((NormalViewHolder) viewHolder).replyTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onCommenttemClickListener) {
                        onCommenttemClickListener.onReplyClick(position);
                    }
                }
            });
            ((NormalViewHolder) viewHolder).ooTv.setText("OO [" + item.getOo_number() + "]");
            ((NormalViewHolder) viewHolder).xxTv.setText("XX [" + item.getXx_number() + "]");
            ((NormalViewHolder) viewHolder).ooTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onCommenttemClickListener) {
                        onCommenttemClickListener.onOOClick(position);
                    }
                }
            });
            ((NormalViewHolder) viewHolder).xxTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onCommenttemClickListener) {
                        onCommenttemClickListener.onXXClick(position);
                    }
                }
            });
            ((NormalViewHolder) viewHolder).itemCommentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onRecycleItemClickListener) {
                        onRecycleItemClickListener.onItemClick(position);
                    }
                }
            });
        } else if (viewHolder instanceof EmptyViewHolder) {
            ((EmptyViewHolder) viewHolder).emptyText.setText("还没有人评论过!");
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

    public void setOnCommenttemClickListener(OnCommenttemClickListener onCommenttemClickListener) {
        this.onCommenttemClickListener = onCommenttemClickListener;
    }

    public boolean isUserCenter() {
        return isUserCenter;
    }

    public void setUserCenter(boolean userCenter) {
        isUserCenter = userCenter;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout itemCommentRl;
        private TextView userNameTv;
        private TextView replyTv;
        private TextView commentContentTv;
        private TextView commentDateTv;
        private TextView ooTv;
        private TextView xxTv;

        public NormalViewHolder(View view) {
            super(view);
            itemCommentRl = (RelativeLayout) view.findViewById(R.id.item_comment_rl);
            userNameTv = (TextView) view.findViewById(R.id.user_name_tv);
            replyTv = (TextView) view.findViewById(R.id.reply_tv);
            commentContentTv = (TextView) view.findViewById(R.id.comment_content_tv);
            commentDateTv = (TextView) view.findViewById(R.id.comment_date_tv);
            ooTv = (TextView) view.findViewById(R.id.oo_tv);
            xxTv = (TextView) view.findViewById(R.id.xx_tv);
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

    public ArrayList<CommentBean> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<CommentBean> datas) {
        this.datas = datas;
    }
}
