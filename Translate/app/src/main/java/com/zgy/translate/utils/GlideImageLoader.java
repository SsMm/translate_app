package com.zgy.translate.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.imnjh.imagepicker.ImageLoader;
import com.zgy.translate.R;

/**
 * Created by zhouguangyue on 2017/6/27.
 */

public class GlideImageLoader implements ImageLoader {

    @Override
    public void bindImage(ImageView imageView, Uri uri, int width, int height) {
        Glide.with(imageView.getContext()).load(uri)
                .placeholder(R.mipmap.ic_boxing_default_image)
                .crossFade()
                .centerCrop()
                .override(width,height)
                .into(imageView);
    }

    @Override
    public void bindImage(ImageView imageView, Uri uri) {
        bindImage(imageView,uri,0,0);
    }

    @Override
    public ImageView createImageView(Context context) {
        return new ImageView(context);
    }

    @Override
    public ImageView createFakeImageView(Context context) {
        return new ImageView(context);
    }
}
