package com.truthower.suhang.mangareader.business.onlinedetail;

import android.content.Context;
import android.os.Handler;

import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.db.DbAdapter;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;
import com.truthower.suhang.mangareader.widget.toast.EasyToast;

import java.util.Arrays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OnlineDetailVM extends ViewModel {
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private MutableLiveData<MangaBean> manga = new MutableLiveData<>();
    private MutableLiveData<String> message = new MutableLiveData<>();
    private MutableLiveData<String[]> authorOptions = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCollected = new MutableLiveData<>();
    private DbAdapter db;//数据库
    private Context mContext;
    private SpiderBase spider;
    //因为我不知道当期收藏的漫画是哪个网站的 所以就一个个试
    private int trySpiderPosition = 0;
    private Handler mHandler = new Handler();
    private MangaBean cacheManga = null;

    void init(Context context) {
        mContext = context.getApplicationContext();
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

    void getMangaDetails(final String url) {
        getMangaDetails(url, false);
    }

    void getMangaDetails(final String url, boolean useCache) {
        isUpdating.setValue(true);
        if (useCache) {
            cacheManga = (MangaBean) ShareObjUtil.getObject(mContext, getKeyFromUrl(url));
            if (null != cacheManga) {
                isUpdating.setValue(false);
                manga.setValue(cacheManga);
                return;
            }
        }
        spider.getMangaDetail(url, new JsoupCallBack<MangaBean>() {
            @Override
            public void loadSucceed(final MangaBean result) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isUpdating.setValue(false);
                        manga.setValue(result);
                        if (null != cacheManga) {
                            //说明使用了缓存,那就更新缓存
                            cacheDetail();
                        }
                    }
                });
            }

            @Override
            public void loadFailed(final String error) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isUpdating.setValue(false);
                        if (error.equals(Configure.WRONG_WEBSITE_EXCEPTION)) {
                            try {
                                BaseParameterUtil.getInstance().saveCurrentWebSite(mContext, Configure.websList[trySpiderPosition]);
                                initSpider();
                                getMangaDetails(url, false);
                                trySpiderPosition++;
                            } catch (IndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    void cacheDetail() {
        ShareObjUtil.saveObject(mContext, manga.getValue(), getKeyFromUrl(manga.getValue().getUrl()));
    }

    void getIsCollected(String url) {
        isCollected.setValue(db.queryCollectExist(url));
    }

    void doCollect(String mangaName, String url, String thumbnailUrl) {
        if (isCollected.getValue()) {
            db.deleteCollect(url);
            isCollected.setValue(false);
            message.setValue("取消收藏");
        } else {
            db.insertCollect(mangaName, url, thumbnailUrl);
            isCollected.setValue(true);
            message.setValue("收藏成功");
        }
    }

    LiveData<MangaBean> getManga() {
        return manga;
    }

    LiveData<String> getMessage() {
        return message;
    }

    LiveData<String[]> getAuthorOptions() {
        return authorOptions;
    }

    LiveData<Boolean> getIsCollected() {
        return isCollected;
    }

    boolean getIsForAdult() {
        boolean isForAdult = false;
        if (null == manga.getValue() || null == manga.getValue().getTypes()) {
            return false;
        }
        String types = Arrays.toString(manga.getValue().getTypes());
        if (null != spider.getAdultTypes() && spider.getAdultTypes().length > 0) {
            for (String item : spider.getAdultTypes()) {
                if (types.contains(item.toLowerCase())) {
                    isForAdult = true;
                    break;
                }
            }
        }
        return isForAdult;
    }

    LiveData<Boolean> getIsUpdating() {
        return isUpdating;
    }

    SpiderBase getSpider() {
        return spider;
    }

    private String getKeyFromUrl(String url) {
        String result = "";
        int index = url.lastIndexOf("/") + 1;
        result = url.substring(index);
        return result;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
//        new EasyToast(mContext).showToast("viewmodel销毁了");
        isUpdating.setValue(false);
        db.closeDb();
    }
}
