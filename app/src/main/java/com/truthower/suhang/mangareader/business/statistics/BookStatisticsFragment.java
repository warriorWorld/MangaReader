package com.truthower.suhang.mangareader.business.statistics;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.adapter.BookStatisticsListAdapter;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.StatisticsBean;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.WeekUtil;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 个人信息页
 */
public class BookStatisticsFragment extends BaseFragment implements View.OnClickListener {
    protected ArrayList<StatisticsBean> data_list = new ArrayList<>();
    private ArrayList<StatisticsBean> handled_list = new ArrayList<>();
    private RecyclerView calendarStatisticsRcv;
    private View emptyView;
    private BookStatisticsListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.activity_book_statistics, container, false);
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
        AVQuery<AVObject> ownerQuery = new AVQuery<>("Statistics");
        ownerQuery.whereEqualTo("owner", LoginBean.getInstance().getUserName());

        ownerQuery.limit(999);
        ownerQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(getActivity(), e)) {
                    data_list = new ArrayList<StatisticsBean>();
                    if (null != list && list.size() > 0) {
                        StatisticsBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new StatisticsBean();
                            item.setManga_name(list.get(i).getString("manga_name"));
                            item.setCreate_at(list.get(i).getCreatedAt());
                            item.setQuery_word_c(list.get(i).getInt("query_word_c"));
                            item.setRead_page(list.get(i).getInt("read_page"));

                            data_list.add(item);
                        }
                    }
                } else {
                    data_list = null;
                }
                refreshData();
            }
        });
    }

    private void initDateRv() {
        try {
            if (null == handled_list || handled_list.size() <= 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
            if (null == adapter) {
                adapter = new BookStatisticsListAdapter(getActivity(), handled_list);
                calendarStatisticsRcv.setAdapter(adapter);
            } else {
                adapter.setList(handled_list);
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }


    private void initUI(View v) {
        calendarStatisticsRcv = (RecyclerView) v.findViewById(R.id.calendar_statistics_rcv);
        calendarStatisticsRcv.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        calendarStatisticsRcv.setFocusableInTouchMode(false);
        calendarStatisticsRcv.setFocusable(false);
        calendarStatisticsRcv.setHasFixedSize(true);
        emptyView = v.findViewById(R.id.empty_view);
    }

    private void refreshData() {
        handled_list = handleBookList(data_list);
        initDateRv();
    }

    /**
     * 获取书的列表
     *
     * @param list
     * @return
     */
    private ArrayList<StatisticsBean> handleBookList(ArrayList<StatisticsBean> list) {
        ArrayList<String> bookList = new ArrayList<>();
        ArrayList<StatisticsBean> finalRes = new ArrayList<>();//总体统计数据
        for (int i = list.size() - 1; i >= 0; i--) {
            //获取所有书
            if (!bookList.contains(list.get(i).getManga_name())) {
                bookList.add(list.get(i).getManga_name());
                finalRes.add(list.get(i));
            }
        }
        while (bookList.size() > 0) {
            String bookName = bookList.get(0);
            int totalQueryC = 0, totalReadPage = 0;
            ArrayList<StatisticsBean> oneBook = new ArrayList<>();//一本书的统计数据
            for (int i = 0; i < list.size(); i++) {
                if (bookName.equals(list.get(i).getManga_name())) {
                    oneBook.add(list.get(i));
                }
            }
            for (int i = 0; i < oneBook.size(); i++) {
                totalQueryC += oneBook.get(i).getQuery_word_c();
                totalReadPage += oneBook.get(i).getRead_page();
            }
            for (int i = 0; i < finalRes.size(); i++) {
                //把得到的真正的数据给他
                if (bookName.equals(finalRes.get(i).getManga_name())) {
                    StatisticsBean item = finalRes.get(i);
                    item.setQurey_word_c_total(totalQueryC);
                    item.setRead_page_total(totalReadPage);
                    item.setDateStart(WeekUtil.getDateStringWithDate(oneBook.get(0).getCreate_at()));
                    item.setDateEnd(WeekUtil.getDateStringWithDate(oneBook.get(oneBook.size() - 1).getCreate_at()));
                    finalRes.set(i, item);
                }
            }
            bookList.remove(0);
        }
        for (int i = 0; i < finalRes.size(); i++) {
            //计算没有的`
            StatisticsBean item = finalRes.get(i);
            int readC = item.getRead_page_total();
            if (readC == 0) {
                item.setQuery_word_r(0);
            } else {
                item.setQuery_word_r(((float) item.getQurey_word_c_total() * 100 / (float) readC));
            }
            finalRes.set(i, item);
        }
        return finalRes;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
