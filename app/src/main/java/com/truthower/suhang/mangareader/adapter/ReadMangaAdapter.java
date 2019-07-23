package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoView;


public class ReadMangaAdapter extends PagerAdapter {
    private ArrayList<String> pathList = new ArrayList<String>();
    private Context context;
    private Bitmap bp;
    //为解决删除后不刷新问题
    private int mChildCount = 0;

//    @Override
//    public float getPageWidth(int position) {
//        return 0.5f;
//    }


    public ReadMangaAdapter(Context context, ArrayList<String> pathList) {
        this.context = context;
        this.pathList = pathList;
    }

    public void setPathList(ArrayList<String> pathList) {
        this.pathList = pathList;
    }

    @Override
    public int getCount() {
        return pathList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // 官方提示这样写
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (pathList.get(position).endsWith(".gif") ||
                pathList.get(position).endsWith(".GIF")) {
            GifImageView gv = (GifImageView) LayoutInflater.from(context).inflate(R.layout.item_gif, container, false);
//            ImageLoader.getInstance().displayImage(pathList.get(position), gv, Configure.smallImageOptions);
            Uri uri= Uri.parse(pathList.get(position));
            gv.setImageURI(uri);
            container.addView(gv);
            return gv;
        } else {
            PhotoView v0;
            v0 = new PhotoView(context);
            ImageLoader.getInstance().displayImage(pathList.get(position), v0, Configure.smallImageOptions);
            container.addView(v0);
            return v0;
        }
    }
//根据源码 这个方法可以解决缓存至少是2的问题 但是这个方法不太好 所以我改成用六个缓存view
//    @Override
//    public float getPageWidth(int position) {
//        return 1.01f;
//    }

    //为解决删除后不刷新问题
    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    //为解决删除后不刷新问题
    @Override
    public int getItemPosition(Object object) {
        if (mChildCount > 0) {
            mChildCount--;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
