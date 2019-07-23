package com.truthower.suhang.mangareader.base;/**
 * Created by Administrator on 2016/10/26.
 */

import android.os.Bundle;

import com.truthower.suhang.mangareader.widget.toast.EasyToast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * 作者：苏航 on 2016/10/26 14:46
 * 邮箱：772192594@qq.com
 */
public class BaseFragment extends Fragment {
    protected EasyToast baseToast;

    public String getFragmentTag() {
        return getClass().getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseToast = new EasyToast(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
