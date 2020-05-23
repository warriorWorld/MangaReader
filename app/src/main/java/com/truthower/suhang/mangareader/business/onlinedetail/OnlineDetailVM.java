package com.truthower.suhang.mangareader.business.onlinedetail;

import android.content.Context;
import android.os.Handler;

import com.truthower.suhang.mangareader.bean.MangaBean;
import com.truthower.suhang.mangareader.bean.RxDownloadChapterBean;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.db.DbAdapter;
import com.truthower.suhang.mangareader.listener.JsoupCallBack;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.spider.SpiderBase;
import com.truthower.suhang.mangareader.utils.BaseParameterUtil;
import com.truthower.suhang.mangareader.utils.ShareObjUtil;
import com.truthower.suhang.mangareader.utils.StringUtil;
import com.truthower.suhang.mangareader.widget.toast.EasyToast;

import java.util.ArrayList;
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
            cacheManga = (MangaBean) ShareObjUtil.getObject(mContext, StringUtil.getKeyFromUrl(url));
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

    void exportCache() {
        ArrayList<RxDownloadChapterBean> cacheArray = (ArrayList<RxDownloadChapterBean>) ShareObjUtil.getObject(
                mContext, manga.getValue().getName()
                        + ShareKeys.BRIDGE_KEY);
        if (null != cacheArray && cacheArray.size() > 0) {
            FileSpider.getInstance().fileSave2SDCard(manga.getValue().getName(),cacheArray);
            message.setValue("已成功将缓存导出到manga文件夹下!");
        } else {
            message.setValue("缓存为空!");
        }
    }

    void cacheDetail() {
        ShareObjUtil.saveObject(mContext, manga.getValue(), StringUtil.getKeyFromUrl(manga.getValue().getUrl()));
    }

    void cleanAllCache() {
        ShareObjUtil.deleteFile(mContext, StringUtil.getKeyFromUrl(manga.getValue().getUrl()));
        ShareObjUtil.deleteFile(mContext, manga.getValue().getName() + ShareKeys.BRIDGE_KEY);
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

    @Override
    protected void onCleared() {
        super.onCleared();
//        new EasyToast(mContext).showToast("viewmodel销毁了");
        isUpdating.setValue(false);
        db.closeDb();
    }
}
