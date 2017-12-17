package com.zgy.translate.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by zhouguangyue on 2017/12/15.
 */

public class AudioRecordUtil {

    private static AudioRecord mAudioRecord;
    private static AudioTrack mAudioTrack;

    private static short[] mAudioRecordData;
    private static short[] mAudioTrackData;
    private static ScheduledExecutorService executorService;

    public static void startRecord(File pathFile, Context context, AudioManager audioManager,
                                   AudioRecordInterface recordInterface){
        checkPoolState();
        int sampleRateInHz = 44100;//所有Android系统都支持的频率
        int recordBufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecordData = new short[recordBufferSizeInBytes];
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRateInHz, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, recordBufferSizeInBytes);

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
                    recordInterface.openBlueSCO();
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            doStart(pathFile);
                        }
                    });
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

    private static void doStart(File pathFile){
        mAudioRecord.startRecording();
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(pathFile)));
            while (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                int num = mAudioRecord.read(mAudioRecordData, 0, mAudioRecordData.length);
                for (int i = 0 ; i < num ; i++){
                    dataOutputStream.writeShort(mAudioRecordData[i]);
                }
                if(AudioRecord.ERROR_BAD_VALUE != num && AudioRecord.ERROR != num){
                    //Log.d("TAG", String.valueOf(num));
                }
            }
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopRecord(){
        if(mAudioRecord != null && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
        checkPoolState();
    }


    public static void startTrack(File pathFile, AudioManager audioManager){
        checkPoolState();
        int sampleRateInHz = 44100;//所有Android系统都支持的频率
        int trackBufferSizeInBytes = AudioRecord.getMinBufferSize(
                sampleRateInHz, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        mAudioTrackData = new short[trackBufferSizeInBytes];

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, trackBufferSizeInBytes,
                AudioTrack.MODE_STREAM);

        if(!audioManager.isBluetoothA2dpOn()){
            audioManager.setBluetoothA2dpOn(true);
        }

        audioManager.setStreamSolo(AudioManager.STREAM_MUSIC, true);
        audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_BLUETOOTH_A2DP, AudioManager.ROUTE_BLUETOOTH);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                doPlay(pathFile);
            }
        });

    }

    private static void doPlay(File pathFile){
        mAudioTrack.play();
        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(pathFile)));

            while (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING
                    && dataInputStream.available() > 0) {
                Log.i("track", "----");
                int i = 0;
                while (dataInputStream.available() > 0
                        && i < mAudioTrackData.length) {
                    mAudioTrackData[i] = dataInputStream.readShort();
                    i++;
                }
                //wipe(mAudioTrackData, 0, mAudioTrackData.length);
                // 然后将数据写入到AudioTrack中
                mAudioTrack.write(mAudioTrackData, 0, mAudioTrackData.length);
            }
            mAudioTrack.stop();
            dataInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopTrack(){
        if(mAudioTrack != null){
            mAudioTrack.release();
            mAudioTrack = null;
        }
        checkPoolState();
    }


    /**
     * 消除噪音
     */
    private static void wipe(short[] lin, int off, int len) {
        int i, j;
        for (i = 0; i < len; i++) {
            j = lin[i + off];
            lin[i + off] = (short) (j >> 2);
        }
    }


    /**
     * 查看是否有线程池存在执行任务，有就关闭，建立新线程池
     * */
    private static void checkPoolState(){
        if (executorService != null){
            executorService.shutdown();
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
    }


    public interface AudioRecordInterface{
        void openBlueSCO();
    }
}

