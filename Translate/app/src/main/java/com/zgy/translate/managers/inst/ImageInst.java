package com.zgy.translate.managers.inst;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.imnjh.imagepicker.SImagePicker;
import com.zgy.translate.managers.MultiMediaManager;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by zhouguangyue on 2017/6/28.
 */

public class ImageInst {

    private Intent mediaIntent;

    public ImageInst(){

    }


    /**
     * 相机
     * */
    public Uri openCamera(Context context, File photoFile, int requestCode, String provider){
        WeakReference<Activity> weakReference = new WeakReference<Activity>((Activity) context);
        Uri photoUri = MultiMediaManager.openCameraParams(context,photoFile,provider);
        mediaIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mediaIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
        weakReference.get().startActivityForResult(mediaIntent,requestCode);
        return photoUri;
    }


    /**
     * 获取相册
     * */
    public void openPhotoAlbum(Activity activity, int requestCode, int resId){
        WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        SImagePicker.from(weakReference.get())
                .maxCount(1)
                .rowCount(3)
                .pickMode(SImagePicker.MODE_IMAGE)
                .nav(resId)
                .forResult(requestCode);
        /*BoxingConfig config = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG);
        Boxing.of(config).withIntent(activity, BoxingActivity.class).start((Activity) activity,requestCode);*/

    }

    /**
     * 相册后剪切
     * */
    public void openPhotoAlbumToCrop(Activity activity, String photoPath, int result, int resId){
        WeakReference<Activity> weakReference = new WeakReference<Activity>(activity);
        SImagePicker.from(weakReference.get())
                .pickMode(SImagePicker.MODE_AVATAR)
                .nav(resId)
                .cropFilePath(photoPath)
                .forResult(result);
    }


    public void onMyDestroy() {
        mediaIntent = null;
    }
}
