package com.truthower.suhang.mangareader.widget.imageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.ShareKeys;
import com.truthower.suhang.mangareader.eventbus.EventBusEvent;
import com.truthower.suhang.mangareader.listener.OnImgSizeListener;
import com.truthower.suhang.mangareader.utils.DisplayUtil;
import com.truthower.suhang.mangareader.utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class GlidePhotoView extends PhotoView {
    private Context mContext;

    public GlidePhotoView(Context context) {
        super(context);
        init(context);
    }

    public GlidePhotoView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public GlidePhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
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

    public void setImgUrl(final String url, final DisplayImageOptions options) {
        setImgUrl(url, options, -1);
    }

    public void setImgUrl(final String url, final DisplayImageOptions options, final int position) {
        setImageResource(R.drawable.spider_hat_color512);
        Glide.with(mContext)
                .asBitmap()
                .load(url)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        setImageResource(R.drawable.spider_hat_gray512);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(mContext, ShareKeys.CLOSE_WRAP_IMG, false)) {
                            //获取bitmap的宽度
                            float bitWidth = resource.getWidth();
                            //获取bitmap的宽度
                            float bithight = resource.getHeight();
                            // 高按照比例计算
                            if (bitWidth > bithight) {
                                EventBus.getDefault().post(new EventBusEvent(position, EventBusEvent.NEED_LANDSCAPE_EVENT));
//                                    mBitmap = ImageUtil.getRotateBitmap(mBitmap, Configure.currentOrientation);
                            } else {
                                EventBus.getDefault().post(new EventBusEvent(position, EventBusEvent.NEED_PORTRAIT_EVENT));
                            }
                        }
                        return false;
                    }
                })
                .into(this);


//        this.position = position;
//        setImageResource(R.drawable.spider_hat_color512);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                FutureTarget<Bitmap> futureTarget =
//                        Glide.with(mContext)
//                                .asBitmap()
//                                .load(url)
//                                .submit();
//                try {
//                    mBitmap = futureTarget.get();
//                    Message msg = Message.obtain();
//                    msg.what = 0;
//                    mHandler.sendMessage(msg);
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                    // Do something with the Bitmap and then when you're done with it:
//                    Glide.with(mContext).clear(futureTarget);
//                }
//            }
//        }).start();


//        this.position = position;
//        setImageResource(R.drawable.spider_hat_color512);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                mBitmap = ImageLoader.getInstance().loadImageSync(url, options);
//                Message msg = Message.obtain();
//                msg.what = 0;
//                mHandler.sendMessage(msg);
//            }
//        }).start();
    }
}
