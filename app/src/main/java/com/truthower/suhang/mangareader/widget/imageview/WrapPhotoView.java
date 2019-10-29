package com.truthower.suhang.mangareader.widget.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.utils.ImageUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import uk.co.senab.photoview.PhotoView;

public class WrapPhotoView extends PhotoView {
    private Context mContext;
    private Bitmap mBitmap;
    private int position;
    /**
     * Handler处理类
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (mBitmap == null) {
                                setImageResource(R.drawable.spider_hat_gray512);
                                return;
                            }
                            if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(mContext, ShareKeys.CLOSE_WRAP_IMG, false)) {
                                //获取bitmap的宽度
                                float bitWidth = mBitmap.getWidth();
                                //获取bitmap的宽度
                                float bithight = mBitmap.getHeight();
                                // 高按照比例计算
                                if (bitWidth > bithight) {
                                    EventBus.getDefault().post(new EventBusEvent(position,EventBusEvent.NEED_LANDSCAPE_EVENT));
//                                    mBitmap = ImageUtil.getRotateBitmap(mBitmap, Configure.currentOrientation);
                                }else {
                                    EventBus.getDefault().post(new EventBusEvent(position,EventBusEvent.NEED_PORTRAIT_EVENT));
                                }
                            }
                            setImageBitmap(mBitmap);
                        }
                    });
                    break;
            }
        }
    };

    public WrapPhotoView(Context context) {
        super(context);
        init(context);
    }

    public WrapPhotoView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public WrapPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
    }

    public void setImgUrl(final String url, final DisplayImageOptions options) {
        setImgUrl(url, options, -1);
    }

    public void setImgUrl(final String url, final DisplayImageOptions options, int position) {
        this.position = position;
        setImageResource(R.drawable.spider_hat_color512);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBitmap = ImageLoader.getInstance().loadImageSync(url, options);
                Message msg = Message.obtain();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}
