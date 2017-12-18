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

    private static ScheduledExecutorService executorService;

    public static void startRecord(File pathFile, Context context, AudioManager audioManager){
        checkPoolState();

        if(!audioManager.isBluetoothScoAvailableOffCall()){
            ConfigUtil.showToask(context, "系统不支持蓝牙录音");
            return;
        }

        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
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
                    audioManager.setSpeakerphoneOn(false);
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
        short[] mAudioRecordData;
        //int sampleRateInHz = 44100;//所有Android系统都支持的频率
        int sampleRateInHz = 16000;
        int recordBufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecordData = new short[recordBufferSizeInBytes];
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                sampleRateInHz, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, recordBufferSizeInBytes);

        try {
            DataOutputStream dataOutputStream = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(pathFile)));
            mAudioRecord.startRecording();
            while (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING){
                int num = mAudioRecord.read(mAudioRecordData, 0, recordBufferSizeInBytes);
                for (int i = 0 ; i < num ; i++){
                    dataOutputStream.writeShort(mAudioRecordData[i]);
                }

                long v = 0;

                for (int j = 0 ; j < mAudioRecordData.length ; j++){
                    v += mAudioRecordData[j] * mAudioRecordData[j];
                }
                double mean = v / num;
                double volume = 10 * Math.log10(mean);
                Log.i("volume", volume +"");
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
        Log.i("pathfile", pathFile.length() + "");
        checkPoolState();
        //int sampleRateInHz = 44100;//所有Android系统都支持的频率

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if(!audioManager.isBluetoothA2dpOn()){
                    audioManager.setBluetoothA2dpOn(true);
                    audioManager.setSpeakerphoneOn(false);
                }

                audioManager.stopBluetoothSco();

                try {
                    Thread.sleep(5 * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setStreamSolo(AudioManager.STREAM_MUSIC, true);
                audioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_BLUETOOTH_A2DP, AudioManager.ROUTE_BLUETOOTH);
                doPlay(pathFile);
            }
        });

    }

    private static void doPlay(File pathFile){
        short[] mAudioTrackData;
        //int sampleRateInHz = 44100;//所有Android系统都支持的频率
        int sampleRateInHz = 16000;//所有Android系统都支持的频率

        /*int trackBufferSizeInBytes = AudioRecord.getMinBufferSize(
                sampleRateInHz, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);*/

        int musicLength = (int) (pathFile.length() / 2);
        mAudioTrackData = new short[musicLength];

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, musicLength * 2,
                AudioTrack.MODE_STREAM);

        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(pathFile)));

           /* while (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING
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
            }*/

            int i = 0;
            while (dataInputStream.available() > 0){
                mAudioTrackData[i] = dataInputStream.readShort();
                i++;
            }
            dataInputStream.close();
            wipe(mAudioTrackData, 0, mAudioTrackData.length);
            mAudioTrack.play();
            mAudioTrack.write(mAudioTrackData, 0, musicLength);
            mAudioTrack.stop();
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

