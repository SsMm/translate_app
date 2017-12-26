package com.zgy.translate.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;


import com.zgy.translate.utils.ImageUtils;

import java.io.File;

/**
 * Created by zhouguangyue on 2017/8/11.
 */

public class MultiMediaManager {



    /**
     * 获取图片存储路径
     */
    public static File getFile(File file, String imgName, String time){
        return ImageUtils.getFile(file,imgName,time);
    }

    /**
     * 保存图片
     * */
    public static String saveBitmap(Context context, File file, String imgName, String time, Bitmap bitmap, String provider){
        return ImageUtils.getComposeBitmap(context,file,imgName,time,bitmap,provider);
    }

    /**
     * 拍照
     * */
    public static Uri openCameraParams(Context context, File photoFile, String provider){
        return ImageUtils.getUri(context,photoFile,provider);
    }

    /**
     * 更新相册
     * */
    public static void updateImages(Context context, Uri uri){
        ImageUtils.updateImages(context,uri);
    }

}
