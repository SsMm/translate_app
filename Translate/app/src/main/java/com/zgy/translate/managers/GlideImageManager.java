package com.zgy.translate.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zgy.translate.R;
import com.zgy.translate.utils.GlideCacheUtil;


import java.io.File;

/**
 * Created by zhouguangyue on 2017/7/25.
 */

public class GlideImageManager {

    private static File fileLocal;//本地文件

    /**
     * 显示资源图片
     * */
    public static void showResImage(Context context, int resId, ImageView imageView){
        glideRes(context,resId,imageView);
    }

    /**
     * 显示本地图片
     * */
    public static void showFileImage(Context context, String filePath, ImageView imageView){
        glideFile(context,filePath,imageView);
    }

    /**
     * 下载显示本地图片
     * */
    public static void showFileDownloadImage(Context context, String filePath, ImageView imageView){
        glideFileDownload(context,filePath,imageView);
    }

    /**
     * 显示网络图片
     * */
    public static void showURLImage(Context context, String url, ImageView imageView){
        glideURL(context,url,imageView);
    }

    /**
     * 下载网络图片进行显示
     * */
    public static void showURLDownloadImage(Context context, String url, ImageView imageView){
        glideURLDownload(context,url,imageView);
    }

    public static void showResDownloadImage(Context context, int resId, ImageView imageView){
        glideResDownload(context,resId,imageView);
    }

    /**
     * 获取glide缓存大小
     * */
    public static String getGlideCacheSize(Context context){
        return GlideCacheUtil.getCacheSize(context);
    }

    /**
     * 清理缓存
     * */
    public static void clearGlideCache(Context context, GlideCacheUtil.GlideCacheSucInterface cacheSucInterface){
        GlideCacheUtil.clearImageMemoryCache(context);
        GlideCacheUtil.clearImageDiskCache(context,cacheSucInterface);
    }


    private static void glideRes(Context context, int resId, ImageView imageView){
        Glide.with(context.getApplicationContext()).load(resId)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView);
    }

    private static void glideFile(Context context, String filePath, ImageView imageView){
        fileLocal = new File(filePath);
        Glide.with(context.getApplicationContext()).load(fileLocal)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView);
    }

    private static void glideURL(Context context, String url, ImageView imageView){
        Glide.with(context.getApplicationContext()).load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView);
    }

    private static void glideResDownload(Context context, int resId, final ImageView imageView){
        Glide.with(context.getApplicationContext()).load(resId)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if(resource != null){
                            imageView.setImageBitmap(resource);
                        }else{
                            imageView.setImageResource(R.drawable.error);
                        }
                    }
                });
    }

    private static void glideFileDownload(Context context, String filePath, final ImageView imageView){
        fileLocal = new File(filePath);
        Glide.with(context.getApplicationContext()).load(fileLocal)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if(resource != null){
                            imageView.setImageBitmap(resource);
                        }else{
                            imageView.setImageResource(R.drawable.error);
                        }
                    }
                });
    }

    private static void glideURLDownload(Context context, String url, final ImageView imageView){
        Glide.with(context.getApplicationContext()).load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        if(resource != null){
                            imageView.setImageBitmap(resource);
                        }else{
                            imageView.setImageResource(R.drawable.error);
                        }
                    }
                });
    }


}
