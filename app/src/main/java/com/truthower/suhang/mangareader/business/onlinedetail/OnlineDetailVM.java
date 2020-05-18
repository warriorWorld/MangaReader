package com.truthower.suhang.mangareader.business.onlinedetail;

import android.content.Context;

import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.db.DbAdapter;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OnlineDetailVM extends ViewModel {
    private MutableLiveData<MangaBean> manga;
    private MutableLiveData<String> error;
    private MutableLiveData<String[]> authorOptions;
    private MutableLiveData<Boolean> isCollected;
    private MutableLiveData<Boolean> isForAdult;
    private DbAdapter db;//数据库
    private Context mContext;
    private SpiderBase spider;

    public void init(Context context) {
        mContext = context;
        db = new DbAdapter(mContext);
        initSpider();
    }

    private void initSpider() {
        try {
            spider = (SpiderBase) Class.forName
                    ("com.truthower.suhang.mangareader.spider." + BaseParameterUtil.getInstance().getCurrentWebSite(mContext) + "Spider").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public LiveData<MangaBean> getManga() {
        return manga;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String[]> getAuthorOptions() {
        return authorOptions;
    }

    public LiveData<Boolean> getIsCollected() {
        return isCollected;
    }

    public LiveData<Boolean> getIsForAdult() {
        return isForAdult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        db.closeDb();
        mContext = null;
    }
}
