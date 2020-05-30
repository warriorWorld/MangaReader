package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.widget.imageview.GlidePhotoView;
import com.truthower.suhang.mangareader.widget.imageview.WrapPhotoView;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;


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
            GlidePhotoView glidePhotoView = (GlidePhotoView) LayoutInflater.from(context).inflate(R.layout.item_glidephotoview, container, false);
            glidePhotoView.setImgUrl(pathList.get(position), position);
            container.addView(glidePhotoView);
            return glidePhotoView;
        } else {
            WrapPhotoView wrapPhotoView = (WrapPhotoView) LayoutInflater.from(context).inflate(R.layout.item_wrapphotoview, container, false);
            wrapPhotoView.setImgUrl(pathList.get(position), Configure.smallImageOptions, position);
            container.addView(wrapPhotoView);
            return wrapPhotoView;
        }
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        GlidePhotoView wrapPhotoView = (GlidePhotoView) LayoutInflater.from(context).inflate(R.layout.item_glidephotoview, container, false);
//        wrapPhotoView.setImgUrl(pathList.get(position), position);
//        container.addView(wrapPhotoView);
//        return wrapPhotoView;
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
