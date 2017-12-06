package com.zgy.translate.managers;


import android.content.Context;
import android.media.AudioManager;
import android.widget.ImageView;

import com.zgy.translate.utils.MediaRecorderUtil;

import java.io.File;
import java.lang.ref.WeakReference;



/**
 * Created by zhou on 2017/5/22.
 * 录音、播放控制
 */

public class MediaRecorderManager {

    private MediaRecorderManager(){
    }

    /**
     * 开启录音
     * */
    public static void startRecorder(String file,String fileName) {
        MediaRecorderUtil.startRecorder(file,fileName);
    }

    /**
     * 停止录音
     * */
    public static void stopRecorder(){
        MediaRecorderUtil.stopRecorder();
    }

    /**
     * 播放录音
     * */
    public static void playRecord(File file,ImageView view,AudioManager audioManager){
       MediaRecorderUtil.playRecord(file,view,audioManager,true);
    }

    /**
     * 停止播放
     * */
    public static void stopPlay(){
        MediaRecorderUtil.stopPlay();
    }

    /**
     * 转换音频格式
     * */
    /*public static void convertTo(Context context, File audioPath, IConvertCallback callback){
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
        AndroidAudioConverter.with(contextWeakReference.get())
                // Your current audio file
                .setFile(audioPath)
                // Your desired audio format
                .setFormat(AudioFormat.MP3)
                // An callback to know when conversion is finished
                .setCallback(callback)
                // Start conversion
                .convert();
    }*/

}
