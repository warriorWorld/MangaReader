package com.truthower.suhang.mangareader.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.truthower.suhang.mangareader.R;
import com.truthower.suhang.mangareader.config.Configure;
import com.truthower.suhang.mangareader.spider.FileSpider;
import com.truthower.suhang.mangareader.widget.imageview.GlidePhotoView;
import com.truthower.suhang.mangareader.widget.imageview.WrapPhotoView;
import com.truthower.suhang.mangareader.widget.video.LoopVideoView;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;


public class ReadMangaAdapter extends PagerAdapter {
    private ArrayList<String> pathList = new ArrayList<String>();
    private Context context;
    private Bitmap bp;
    //为解决删除后不刷新问题
    private int mChildCount = 0;
    private final SparseArray<LoopVideoView> videoViews = new SparseArray<>();
    private int activePosition = -1;

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
        for (int i = 0; i < videoViews.size(); i++) {
            videoViews.valueAt(i).stopPlayback();
        }
        this.videoViews.clear();
    }

    public void setActivePosition(int position) {
        activePosition = position;
        for (int i = 0; i < videoViews.size(); i++) {
            LoopVideoView videoView = videoViews.valueAt(i);
            if (videoViews.keyAt(i) == position) {
                videoView.start();
            } else {
                videoView.pause();
            }
        }
    }

    public void pauseAllVideos() {
        for (int i = 0; i < videoViews.size(); i++) {
            videoViews.valueAt(i).pause();
        }
    }

    public void resumeActiveVideo() {
        LoopVideoView videoView = videoViews.get(activePosition);
        if (videoView != null) {
            videoView.start();
        }
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
        LoopVideoView videoView = videoViews.get(position);
        if (videoView != null) {
            videoView.stopPlayback();
            videoViews.remove(position);
        }
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String path = pathList.get(position);
        if (FileSpider.getInstance().isVideo(path)) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_videoview, container, false);
            LoopVideoView videoView = itemView.findViewById(R.id.loop_video_view);
            videoViews.put(position, videoView);
            videoView.setVideoUrl(path);
            if (position == activePosition) {
                videoView.start();
            } else {
                videoView.pause();
            }
            container.addView(itemView);
            return itemView;
        } else if (path.endsWith(".gif") || path.endsWith(".GIF")) {
            GlidePhotoView glidePhotoView = (GlidePhotoView) LayoutInflater.from(context).inflate(R.layout.item_glidephotoview, container, false);
            glidePhotoView.setImgUrl(path, position);
            container.addView(glidePhotoView);
            return glidePhotoView;
        } else {
            WrapPhotoView wrapPhotoView = (WrapPhotoView) LayoutInflater.from(context).inflate(R.layout.item_wrapphotoview, container, false);
            wrapPhotoView.setImgUrl(path, Configure.smallImageOptions, position);
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
