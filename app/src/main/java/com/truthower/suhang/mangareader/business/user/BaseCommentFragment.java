package com.truthower.suhang.mangareader.business.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.CommentAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.CommentBean;
import com.truthower.suhang.mangareader.business.comment.CommentActivity;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 个人信息页
 */
public abstract class BaseCommentFragment extends BaseFragment implements View.OnClickListener {
    protected ArrayList<CommentBean> commentList = new ArrayList<>();
    private RecyclerView commentRcv;
    private CommentAdapter adapter;
    protected String owner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_only_recycler, container, false);
        initUI(v);
        doGetData();
        return v;
    }

    protected abstract void doGetData();

    public void setOwner(String owner) {
        this.owner = owner;
    }

    protected void initListView() {
        try {
            if (null == adapter) {
                adapter = new CommentAdapter(getActivity(), commentList);
                adapter.setUserCenter(true);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), CommentActivity.class);
                        intent.putExtra("mangaName", commentList.get(position).getMangaName());
                        intent.putExtra("mangaUrl", commentList.get(position).getMangaUrl());
                        startActivity(intent);
                    }
                });
                commentRcv.setAdapter(adapter);
            } else {
                adapter.setDatas(commentList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }


    private void initUI(View v) {
        commentRcv = (RecyclerView) v.findViewById(R.id.only_rcv);
        commentRcv.setLayoutManager
                (new LinearLayoutMangerWithoutBug
                        (getActivity(), LinearLayoutManager.VERTICAL, false));
        commentRcv.setFocusableInTouchMode(false);
        commentRcv.setFocusable(false);
        commentRcv.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
