package com.truthower.suhang.mangareader.service;


import com.truthower.suhang.mangareader.base.BaseActivity;
import com.truthower.suhang.mangareader.business.user.CollectedActivity;
import com.truthower.suhang.mangareader.widget.dialog.SingleLoadBarUtil;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/8/26.
 */

public abstract class BaseObserver<T> implements Observer<T> {
    private BaseActivity mContext;

    public BaseObserver(BaseActivity context) {
        this.mContext = context;
    }

    @Override
    public void onSubscribe(Disposable d) {
        mContext.mDisposable.add(d);
        SingleLoadBarUtil.getInstance().showLoadBar(mContext);
    }

    @Override
    public void onComplete() {
        SingleLoadBarUtil.getInstance().dismissLoadBar();
    }

    @Override
    public void onError(Throwable e) {
        SingleLoadBarUtil.getInstance().dismissLoadBar();
    }
}
