package com.zgy.translate.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by zhouguangyue on 2017/11/9.
 */

public class BluetoothRecorder {

    //private static String mFileName;
    private static MediaRecorder mRecorder;
    private static MediaPlayer mPlayer;



    /**开启录音*/
    public static void startRecording(Context context, AudioManager audioManager, String fileName){

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare(); //如果文件打开失败，此步将会出错
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("蓝牙录音打开失败", e.toString());
            ConfigUtil.showToask(context, "蓝牙录音打开失败");
            doEro();
        }

        if(!audioManager.isBluetoothScoAvailableOffCall()){
            ConfigUtil.showToask(context, "系统不支持蓝牙录音");
            return;
        }

        audioManager.startBluetoothSco();//蓝牙录音的关键，启动SCO连接，耳机话筒才起作用

        //蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。
        //也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                Log.i("state", state+"");
                if(AudioManager.SCO_AUDIO_STATE_CONNECTED == state){
                    audioManager.setBluetoothScoOn(true); //打开SCO
                    mRecorder.start();//开始录音
                    Log.i("开始录音", "开始录音");
                    ConfigUtil.showToask(context, "开始录音");
                    context.unregisterReceiver(this);
                }else{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    audioManager.startBluetoothSco();
                }
            }
        },new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));

    }

    private static void doEro(){
        if(mRecorder != null){
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**停止录音*/
    public static void stopRecording(Context context, AudioManager audioManager){
        Log.i("停止录音", "停止录音");
        ConfigUtil.showToask(context, "停止录音");
        if(mRecorder != null){
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
        }
        if(audioManager.isBluetoothScoOn()){
            audioManager.setBluetoothScoOn(false);
            audioManager.stopBluetoothSco();
        }
    }

    /**播放录音到蓝牙耳机*/
    public static void startPlaying(String fileName, Context context, AudioManager audioManager){
        Log.i("filename", fileName);
        mPlayer = new MediaPlayer();
        if(!audioManager.isBluetoothA2dpOn()){
            audioManager.setBluetoothA2dpOn(true); //如果A2DP没建立，则建立A2DP连接
        }

        audioManager.stopBluetoothSco();

        try {
            Thread.sleep(5 * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        audioManager.setStreamSolo(AudioManager.STREAM_MUSIC, true);
        audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_BLUETOOTH_A2DP, AudioManager.ROUTE_BLUETOOTH); //让声音路由到蓝牙A2DP。此方法虽已弃用，但就它比较直接、好用。
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
            ConfigUtil.showToask(context, "播放录音");
        } catch (IOException e) {
            e.printStackTrace();
            stopPlaying(context, audioManager);
        }
    }


    /**停止播放*/
    public static void stopPlaying(Context context, AudioManager audioManager){
        ConfigUtil.showToask(context, "停止播放");
        if(mPlayer != null){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        //audioManager.setStreamSolo(AudioManager.STREAM_MUSIC, false);
        if(audioManager.isBluetoothScoOn()){
            audioManager.setBluetoothScoOn(false);
            audioManager.stopBluetoothSco();
        }
        if(audioManager.isBluetoothA2dpOn()){
            audioManager.setBluetoothA2dpOn(false);
        }
    }


    public static class BluetoothScoReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

        }

    }

    public static IntentFilter scoIntentFilter(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        return intentFilter;
    }


}
