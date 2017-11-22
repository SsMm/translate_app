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

    private static String mFileName;
    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private AudioManager mAudioManager;
    private Context mContext;




    /**开启录音*/
    public void startRecording(Context context, AudioManager audioManager){
        mAudioManager = audioManager;
        mContext = context.getApplicationContext();
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/btrecorder" + System.currentTimeMillis() + ".3gp";

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare(); //如果文件打开失败，此步将会出错
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("蓝牙录音打开失败", e.toString());
            Toast.makeText(context.getApplicationContext(), "蓝牙录音打开失败", Toast.LENGTH_SHORT).show();
        }

        if(!mAudioManager.isBluetoothScoAvailableOffCall()){
            Toast.makeText(context.getApplicationContext(), "系统不支持蓝牙录音", Toast.LENGTH_SHORT).show();
            return;
        }

        mAudioManager.startBluetoothSco();//蓝牙录音的关键，启动SCO连接，耳机话筒才起作用

        //蓝牙SCO连接建立需要时间，连接建立后会发出ACTION_SCO_AUDIO_STATE_CHANGED消息，通过接收该消息而进入后续逻辑。
        //也有可能此时SCO已经建立，则不会收到上述消息，可以startBluetoothSco()前先stopBluetoothSco()

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, -1);
                Log.i("state", state+"");
                if(AudioManager.SCO_AUDIO_STATE_CONNECTED == state){
                    mAudioManager.setBluetoothScoOn(true); //打开SCO
                    mRecorder.start();//开始录音
                    Log.i("开始录音", "开始录音");
                    Toast.makeText(context.getApplicationContext(), "开始录音", Toast.LENGTH_SHORT).show();
                    context.unregisterReceiver(this);
                }else{
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mAudioManager.startBluetoothSco();
                }
            }
        },new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));

    }


    /**停止录音*/
    public void stopRecording(){
        Log.i("停止录音", "停止录音");
        Toast.makeText(mContext, "停止录音", Toast.LENGTH_SHORT).show();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
        if(mAudioManager.isBluetoothScoOn()){
            mAudioManager.setBluetoothScoOn(false);
            mAudioManager.stopBluetoothSco();
        }
    }

    /**播放录音到蓝牙耳机*/
    public void startPlaying(){
        mPlayer = new MediaPlayer();
        if(!mAudioManager.isBluetoothA2dpOn()){
            mAudioManager.setBluetoothA2dpOn(true); //如果A2DP没建立，则建立A2DP连接
        }

        mAudioManager.stopBluetoothSco(); //如果SCO没有断开，由于SCO优先级高于A2DP，A2DP可能无声音

        try {
            Thread.sleep(5 * 100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mAudioManager.setStreamSolo(AudioManager.STREAM_MUSIC, true);
        mAudioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_BLUETOOTH_A2DP, AudioManager.ROUTE_BLUETOOTH); //让声音路由到蓝牙A2DP。此方法虽已弃用，但就它比较直接、好用。
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            Toast.makeText(mContext, "播放录音", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**停止播放*/
    public void stopPlaying(){
        Toast.makeText(mContext, "停止播放", Toast.LENGTH_SHORT).show();
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        mAudioManager.setStreamSolo(AudioManager.STREAM_MUSIC, false);
    }


}
