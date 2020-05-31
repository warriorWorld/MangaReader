package com.truthower.suhang.mangareader.widget.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.OnImgSizeListener;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.ImageUtil;
import com.truthower.suhang.mangareader.utils.Logger;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class WrapPhotoView extends PhotoView {
    private Context mContext;
    /**
     * Handler处理类
     */
    private Handler mHandler;

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
        mHandler = new ImageHandler(mContext, this);
        setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                EventBus.getDefault().post(new EventBusEvent(EventBusEvent.ON_TAP_EVENT, new float[]{x, y}));
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
    }

    public void setBitmap(final Bitmap bm, final int width, final int height) {
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams vgl = getLayoutParams();
                if (bm == null) {
                    setVisibility(GONE);
                    return;
                } else {
                    setVisibility(VISIBLE);
                }
                vgl.width = width;
                vgl.height = height;

                //设置图片充满ImageView控件
                setScaleType(ScaleType.FIT_XY);
                //等比例缩放
                setAdjustViewBounds(true);
                setLayoutParams(vgl);
                setImageBitmap(bm);
            }
        });
    }

    public void setBitmap(final Bitmap bm, final OnImgSizeListener listener) {
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams vgl = getLayoutParams();
                if (bm == null) {
                    setVisibility(GONE);
                    return;
                } else {
                    setVisibility(VISIBLE);
                }
                //获取bitmap的宽度
                float bitWidth = bm.getWidth();
                //获取bitmap的宽度
                float bithight = bm.getHeight();

                //计算出图片的宽高比，然后按照图片的比列去缩放图片
                float bitScalew = bitWidth / bithight;
                float maxWidth = DisplayUtil.getScreenWidth(mContext);
                if (maxWidth <= bitWidth) {
                    vgl.width = (int) maxWidth;
                    vgl.height = (int) (maxWidth / bitScalew);
                } else {
                    vgl.width = (int) bitWidth;
                    vgl.height = (int) bithight;
                }

                //设置图片充满ImageView控件
                setScaleType(ScaleType.FIT_XY);
                //等比例缩放
                setAdjustViewBounds(true);
                setLayoutParams(vgl);
                setImageBitmap(bm);
                if (null != listener) {
                    listener.onSized(vgl.width, vgl.height);
                }
            }
        });
    }

    private static class ImageHandler extends Handler {
        private Context mContext;
        private ImageView mImageView;

        public ImageHandler(Context context, ImageView imageView) {
            mContext = context.getApplicationContext();
            mImageView = imageView;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Bitmap mBitmap = (Bitmap) msg.obj;
                    if (mBitmap == null) {
                        mImageView.setImageResource(R.drawable.spider_hat_gray512);
                        return;
                    }
                    if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(mContext, ShareKeys.CLOSE_WRAP_IMG, false)) {
                        //获取bitmap的宽度
                        float bitWidth = mBitmap.getWidth();
                        //获取bitmap的宽度
                        float bithight = mBitmap.getHeight();
                        // 高按照比例计算
                        if (bitWidth > bithight) {
                            EventBus.getDefault().post(new EventBusEvent(msg.arg1, EventBusEvent.NEED_LANDSCAPE_EVENT));
//                                    mBitmap = ImageUtil.getRotateBitmap(mBitmap, Configure.currentOrientation);
                        } else {
                            EventBus.getDefault().post(new EventBusEvent(msg.arg1, EventBusEvent.NEED_PORTRAIT_EVENT));
                        }
                    }
                    mImageView.setImageBitmap(mBitmap);
                    break;
            }
        }
    }

    public void setImgUrl(final String url, final DisplayImageOptions options) {
        setImgUrl(url, options, -1);
    }

    public void setImgUrl(final String url, final DisplayImageOptions options, final int position) {
        setImageResource(R.drawable.spider_hat_color512);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap mBitmap = ImageLoader.getInstance().loadImageSync(url, options);
                Message msg = Message.obtain();
                msg.what = 0;
                msg.arg1 = position;
                msg.obj = mBitmap;
                mHandler.sendMessage(msg);
            }
        }).start();
    }
}
