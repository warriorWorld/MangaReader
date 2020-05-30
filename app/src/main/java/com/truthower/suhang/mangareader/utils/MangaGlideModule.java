package com.truthower.suhang.mangareader.utils;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.truthower.suhang.mangareader.R;

import androidx.annotation.NonNull;

@GlideModule
public class MangaGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .placeholder(context.getResources().getDrawable(R.drawable.spider_hat_color512))
                        .error(context.getResources().getDrawable(R.drawable.spider_hat_gray512))
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE));
    }
}
