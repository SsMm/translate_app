package com.zgy.translate.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.print.PageRange;
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
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by zhouguangyue on 2017/12/15.
 */

public class AudioRecordUtil {

    private static final int BUFFER_SIZE = 2048;

    private static AudioRecord mAudioRecord;
    private static AudioTrack mAudioTrack;

    private static FileInputStream fileInputStream;
    private static FileOutputStream fileOutputStream;

    private static byte[] mBuffer = new byte[BUFFER_SIZE];

    private static ScheduledExecutorService executorService;
    private static volatile boolean mIsRecording = false;

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
                            doStart2(pathFile);
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
        //mAudioRecordData = new short[recordBufferSizeInBytes];
        mBuffer = new byte[BUFFER_SIZE];
        mIsRecording = true;
        int sampleRate = 44100;//所有Android系统都支持的频率
        //int sampleRate = 16000;
        int audioSource = MediaRecorder.AudioSource.DEFAULT;
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        mAudioRecord = new AudioRecord(audioSource,
                sampleRate, channelConfig, audioFormat, Math.max(minBufferSize, BUFFER_SIZE));

        try {
            /*DataOutputStream dataOutputStream = new DataOutputStream(
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
            dataOutputStream.close();*/

            fileOutputStream = new FileOutputStream(pathFile);
            while (mIsRecording){
                int read = mAudioRecord.read(mBuffer, 0, BUFFER_SIZE);
                if(read > 0){
                    fileOutputStream.write(mBuffer, 0, read);
                }else{
                    //失败
                    stopRecord();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            stopRecord();
        }finally {
            stopRecord();
        }
    }

    private static void doStart2(File pathFile){
        mBuffer = new byte[BUFFER_SIZE];
        mIsRecording = true;
        int sampleRate = 8000;//所有Android系统都支持的频率
        int audioSource = MediaRecorder.AudioSource.VOICE_CALL;
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        mAudioRecord = new AudioRecord(audioSource,
                sampleRate, channelConfig, audioFormat, Math.max(minBufferSize, BUFFER_SIZE));

        BufferedOutputStream bufferedOutputStream = null;

        try {

            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(pathFile));
            while (mIsRecording){
                int read = mAudioRecord.read(mBuffer, 0, BUFFER_SIZE);
                if(read > 0){
                    bufferedOutputStream.write(mBuffer);
                }else{
                    stopRecord();
                }

                long v = 0;
                for(int i = 0 ; i <mBuffer.length ; i++){
                    v += mBuffer[i] * mBuffer[i];
                }
                double mean = v / read;
                double volume = 10 * Math.log10(mean);
                int vol = (int) volume;
                Log.i("volume", vol +"");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(bufferedOutputStream != null){
                try {
                    bufferedOutputStream.flush();
                    bufferedOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            stopRecord();
        }
    }

    public static void stopRecord(){
        mIsRecording = false;
        if(mAudioRecord != null){
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
        if(fileOutputStream != null){
            try {
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        checkPoolState();
    }


    public static void startTrack(Context context, File pathFile, AudioManager audioManager){
        checkPoolState();

        if(!audioManager.isBluetoothScoAvailableOffCall()){
            ConfigUtil.showToask(context, "系统不支持蓝牙录音");
            return;
        }

        executorService.submit(new Runnable() {
            @Override
            public void run() {
               /* if(!audioManager.isBluetoothA2dpOn()){
                    audioManager.setBluetoothA2dpOn(true);
                    audioManager.setSpeakerphoneOn(false);
                }

                audioManager.stopBluetoothSco();

                try {
                    Thread.sleep(5 * 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                audioManager.setStreamSolo(AudioManager.STREAM_MUSIC, true);
                audioManager.setRouting(AudioManager.MODE_IN_COMMUNICATION, AudioManager.ROUTE_BLUETOOTH_A2DP, AudioManager.ROUTE_BLUETOOTH);*/

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
                            ConfigUtil.showToask(context, "开始播放");
                            doPlay2(pathFile);
                            context.unregisterReceiver(this);
                        }else if(AudioManager.SCO_AUDIO_STATE_DISCONNECTED == state){
                            ConfigUtil.showToask(context, "蓝牙播放失败");
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
        });

    }

    private static void doPlay(File pathFile){
        short[] mAudioTrackData;
        mBuffer = new byte[BUFFER_SIZE];
        int sampleRate = 44100;//所有Android系统都支持的频率
        //int sampleRate = 16000;//所有Android系统都支持的频率
        int streamType = AudioManager.STREAM_VOICE_CALL;
        int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int mode = AudioTrack.MODE_STREAM;

        int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        //AudioAttributes audioAttributes = new AudioAttributes()

        mAudioTrack = new AudioTrack(streamType, sampleRate, channelConfig, audioFormat,
                Math.max(minBufferSize, BUFFER_SIZE), mode);

       /* int musicLength = (int) (pathFile.length() / 2);
        mAudioTrackData = new short[musicLength];

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, musicLength * 2,
                AudioTrack.MODE_STREAM);*/

        try {

            fileInputStream = new FileInputStream(pathFile);
            int read;
            mAudioTrack.play();
            while ((read = fileInputStream.read(mBuffer)) > 0){
                Log.i("read--", read + "");
                int ret = mAudioTrack.write(mBuffer, 0, read);
                Log.i("ret---", ret+"");
                switch (ret){
                    case AudioTrack.ERROR_BAD_VALUE:
                    case AudioTrack.ERROR_DEAD_OBJECT:
                    case AudioTrack.ERROR_INVALID_OPERATION:
                        stopTrack();
                        return;
                    default:
                        break;
                }
            }

            //DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(pathFile)));

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

            /*int i = 0;
            while (dataInputStream.available() > 0){
                mAudioTrackData[i] = dataInputStream.readShort();
                i++;
            }
            dataInputStream.close();
            wipe(mAudioTrackData, 0, mAudioTrackData.length);
            mAudioTrack.play();
            mAudioTrack.write(mAudioTrackData, 0, musicLength);
            mAudioTrack.stop();*/
        } catch (Exception e) {
            e.printStackTrace();
            stopTrack();
        }finally {
            stopTrack();
        }
    }

    private static void stopTrack(){
        if(mAudioTrack != null){
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
        if(fileInputStream != null){
            try {
                fileInputStream.close();
                fileInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        checkPoolState();
    }

    private static void doPlay2(File pathFile){

        mBuffer = new byte[BUFFER_SIZE];
        //int sampleRate = 44100;//所有Android系统都支持的频率
        int sampleRate = 8000;//所有Android系统都支持的频率
        int streamType = AudioManager.STREAM_VOICE_CALL;
        int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int mode = AudioTrack.MODE_STREAM;

        int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        //AudioAttributes audioAttributes = new AudioAttributes()

        mAudioTrack = new AudioTrack(streamType, sampleRate, channelConfig, audioFormat,
                Math.max(minBufferSize, BUFFER_SIZE), mode);

        BufferedInputStream bufferedInputStream = null;

        try {

            bufferedInputStream = new BufferedInputStream(new FileInputStream(pathFile));

            mAudioTrack.play();
            while (bufferedInputStream.available() > 0){
                int size = bufferedInputStream.read(mBuffer, 0, BUFFER_SIZE);
                mAudioTrack.write(mBuffer, 0, BUFFER_SIZE);
            }
            mAudioTrack.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(bufferedInputStream != null){
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            stopTrack();
        }

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

