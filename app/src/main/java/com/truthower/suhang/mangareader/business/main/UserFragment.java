package com.truthower.suhang.mangareader.business.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.base.BaseFragment;
import com.truthower.suhang.mangareader.business.collect.CollectedActivity;
import com.truthower.suhang.mangareader.business.download.DownloadActivity;
import com.truthower.suhang.mangareader.business.other.AboutActivity;
import com.truthower.suhang.mangareader.business.wordsbook.WordsBookActivity;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.utils.NumberUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;
import com.truthower.suhang.mangareader.widget.dialog.MangaDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout newWordBookRl;
    private RelativeLayout downloadRl;
    private RelativeLayout aboutRl;
    private RelativeLayout userTopBarRl;
    private TextView dayTv;
    private TextView monthTv;
    private TextView dayOfWeekTv;
    private int currentMonth, currentDay;
    private String currentDayOfWeek;
    private RelativeLayout collectRl;
    private RelativeLayout shareRl;
    private ClipboardManager clip;//复制文本用

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        clip = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        View v = inflater.inflate(R.layout.fragment_user, container, false);
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("EEEE");

        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentDayOfWeek = format.format(date);
        initUI(v);
        refreshUI();
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(getActivity(), ShareKeys.CLOSE_TUTORIAL, true)) {
            MangaDialog dialog = new MangaDialog(getActivity());
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("想要更新App可以进入设置页检查更新下载最新App" +
                    "\n设置页可以关闭本教程");
        }
        return v;
    }

    private void initUI(View v) {
        newWordBookRl = (RelativeLayout) v.findViewById(R.id.new_word_book_rl);
        downloadRl = (RelativeLayout) v.findViewById(R.id.download_rl);
        userTopBarRl = (RelativeLayout) v.findViewById(R.id.user_top_bar_rl);
        aboutRl = (RelativeLayout) v.findViewById(R.id.about_rl);
        dayTv = (TextView) v.findViewById(R.id.day_tv);
        monthTv = (TextView) v.findViewById(R.id.month_tv);
        dayOfWeekTv = (TextView) v.findViewById(R.id.day_of_week_tv);
        collectRl = (RelativeLayout) v.findViewById(R.id.collect_rl);
        shareRl = (RelativeLayout) v.findViewById(R.id.share_rl);
        collectRl.setOnClickListener(this);
        shareRl.setOnClickListener(this);
        newWordBookRl.setOnClickListener(this);
        downloadRl.setOnClickListener(this);
        userTopBarRl.setOnClickListener(this);
        aboutRl.setOnClickListener(this);
    }

    private void refreshUI() {
        dayTv.setText(NumberUtil.toDoubleNum(currentDay));
        monthTv.setText(currentMonth + "月");
        dayOfWeekTv.setText(currentDayOfWeek);
    }

    private void showOrderSelectDialog() {
        MangaDialog dialog = new MangaDialog(getActivity());
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                Intent intent = new Intent(getActivity(), WordsBookActivity.class);
                intent.putExtra("orderType", "random");
                startActivity(intent);
            }

            @Override
            public void onCancelClick() {
                Intent intent = new Intent(getActivity(), WordsBookActivity.class);
                intent.putExtra("orderType", "order");
                startActivity(intent);
            }
        });
        dialog.show();
        dialog.setTitle("请选择生词展示顺序");
        dialog.setOkText("随机");
        dialog.setCancelText("顺序");
    }

    private void showShareDialog() {
        MangaDialog dialog = new MangaDialog(getActivity());
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                clip.setText("782685214");
                baseToast.showToast("复制成功");
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("云服务停用");
        dialog.setMessage("由于失去云服务,后续更新只能在qq群里发布所以请大家加qq群782685214.");
        dialog.setOkText("复制群号");
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.new_word_book_rl:
                showOrderSelectDialog();
                break;
            case R.id.download_rl:
                intent = new Intent(getActivity(), DownloadActivity.class);
                break;
            case R.id.about_rl:
                intent = new Intent(getActivity(), AboutActivity.class);
                break;
            case R.id.collect_rl:
                intent = new Intent(getActivity(), CollectedActivity.class);
                break;
            case R.id.share_rl:
                showShareDialog();
                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }
}
