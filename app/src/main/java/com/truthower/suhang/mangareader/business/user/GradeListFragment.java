package com.truthower.suhang.mangareader.business.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.GradeListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.GradeBean;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.business.detail.WebMangaDetailsActivity;
import com.truthower.suhang.mangareader.listener.OnRecycleItemClickListener;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;
import com.truthower.suhang.mangareader.widget.recyclerview.LinearLayoutMangerWithoutBug;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 个人信息页
 */
public class GradeListFragment extends BaseFragment implements View.OnClickListener {
    protected ArrayList<GradeBean> gradeList = new ArrayList<>();
    private RecyclerView gradeRcv;
    private GradeListAdapter adapter;
    protected String owner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_only_recycler, container, false);
        initUI(v);
        doGetData();
        return v;
    }

    protected void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(getActivity()))) {
            getActivity().finish();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        AVQuery<AVObject> query = new AVQuery<>("Grade");
        query.whereEqualTo("owner", owner);
        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    gradeList = new ArrayList<>();
                    if (null != list && list.size() > 0) {
                        GradeBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new GradeBean();
                            item.setCreate_at(list.get(i).getCreatedAt());
                            item.setMangaName(list.get(i).getString("manga_name"));
                            item.setMangaUrl(list.get(i).getString("mangaUrl"));
                            item.setOwner(list.get(i).getString("owner"));
                            item.setStar(list.get(i).getInt("star"));
                            item.setObjectId(list.get(i).getObjectId());
                            gradeList.add(item);
                        }
                    }
                    initListView();
                }
            }
        });
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    protected void initListView() {
        try {
            if (null == adapter) {
                adapter = new GradeListAdapter(getActivity(), gradeList);
                adapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), WebMangaDetailsActivity.class);
                        intent.putExtra("mangaUrl", gradeList.get(position).getMangaUrl());
                        startActivity(intent);
                    }
                });
                gradeRcv.setAdapter(adapter);
            } else {
                adapter.setDatas(gradeList);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
        }
    }


    private void initUI(View v) {
        gradeRcv = (RecyclerView) v.findViewById(R.id.only_rcv);
        gradeRcv.setLayoutManager
                (new LinearLayoutMangerWithoutBug
                        (getActivity(), LinearLayoutManager.VERTICAL, false));
        gradeRcv.setFocusableInTouchMode(false);
        gradeRcv.setFocusable(false);
        gradeRcv.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
