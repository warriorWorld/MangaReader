package com.truthower.suhang.mangareader.business.statistics;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.bean.LoginBean;
import com.truthower.suhang.mangareader.bean.StatisticsBean;
import com.truthower.suhang.mangareader.utils.LeanCloundUtil;
import com.truthower.suhang.mangareader.utils.WeekUtil;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * 个人信息页
 */
public abstract class BaseStatisticsFragment extends BaseFragment implements View.OnClickListener {
    protected ArrayList<StatisticsBean> data_list = new ArrayList<>();
    protected int currentYear, currentMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(getLayout(), container, false);
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        initUI(v);
        doGetData();
        return v;
    }

    protected abstract int getLayout();

    protected abstract void initUI(View v);

    protected void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(getActivity()))) {
            getActivity().finish();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(getActivity());
        AVQuery<AVObject> ownerQuery = new AVQuery<>("Statistics");
        ownerQuery.whereEqualTo("owner", LoginBean.getInstance().getUserName());

        final AVQuery<AVObject> startDateQuery = new AVQuery<>("Statistics");
        startDateQuery.whereGreaterThanOrEqualTo("createdAt", WeekUtil.getDateWithDateString(currentYear + "-" + currentMonth + "-1"));

        final AVQuery<AVObject> endDateQuery = new AVQuery<>("Statistics");
//        endDateQuery.whereLessThan("createdAt", WeekUtil.getDateWithDateString(currentYear + "-" + currentMonth + "-" + calendarCvl.getMaxDay()));
        //结束日貌似是不包含的
        int nextMonth = currentMonth + 1;
        int year = currentYear;
        if (nextMonth == 13) {
            nextMonth = 1;
            year = year + 1;
        }
        endDateQuery.whereLessThan("createdAt", WeekUtil.getDateWithDateString(year + "-" + nextMonth + "-1"));
        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(ownerQuery, startDateQuery, endDateQuery));

        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
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

    protected abstract void refreshData();
}
