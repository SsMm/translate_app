package com.zgy.translate.managers;


import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.zgy.translate.utils.ByteConstants;

import java.io.File;

/**
 * Created by zhouguangyue on 2017/6/27.
 */

public class CacheManager {

    private CacheManager(){}

    /**
     * 获取磁盘缓存路径
     * */
    public static File getDiskCacheDir(Context context) {
        File cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getApplicationContext().getExternalCacheDir();
        } else {
            cachePath = context.getApplicationContext().getCacheDir();
        }
        if(cachePath != null && !cachePath.exists()){
            cachePath.mkdirs();
        }
        return cachePath;
    }

    //创建一个临时目录，用于复制临时文件，如assets目录下的离线资源文件
    public static String createTmpDir(Context context, String dir) {
        String tmpDir = Environment.getExternalStorageDirectory().toString() + "/" + dir;
        if (!makeDir(tmpDir)) {
            tmpDir = context.getExternalFilesDir(dir).getAbsolutePath();
            if (!makeDir(dir)) {
                throw new RuntimeException("create model resources dir failed :" + tmpDir);
            }
        }
        return tmpDir;
    }

    private static boolean makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    /**
     * 判断当前内存情况
     * */

    public static void getMemoryTrimmableRegistry(ActivityManager activityManager){

        long freeMemory = Runtime.getRuntime().freeMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long maxMemory = Runtime.getRuntime().maxMemory();

    }


    /**
     * 获取内存大小
     * */
    private static int getMaxCacheSize(ActivityManager activityManager){
        final int maxMemory = Math.min(activityManager.getMemoryClass() * ByteConstants.MB,Integer.MAX_VALUE);
        Log.i("maxMemory======",maxMemory/ByteConstants.MB+"");
        if (maxMemory < 32 * ByteConstants.MB) {
            return 4 * ByteConstants.MB;
        } else if (maxMemory < 64 * ByteConstants.MB) {
            return 6 * ByteConstants.MB;
        } else {
            // We don't want to use more ashmem on Gingerbread for now, since it doesn't respond well to
            // native memory pressure (doesn't throw exceptions, crashes app, crashes phone)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return 8 * ByteConstants.MB;
            } else {
                return maxMemory / 4;
            }
        }
    }

    /**
     * 获取手机内存剩余、总量等情况
     * */
    private static void getSystemMemory(ActivityManager activityManager){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long availMem = memoryInfo.availMem;
        long totalMem = memoryInfo.totalMem;
        long threshold = memoryInfo.threshold;
        boolean lowMemory = memoryInfo.lowMemory;
        Log.i("availMem======",availMem/ByteConstants.MB+"");
        Log.i("totalMem======",totalMem/ByteConstants.MB+"");
        Log.i("threshold======",threshold/ByteConstants.MB+"");
        Log.i("lowMemory======",lowMemory+"");
    }

}
