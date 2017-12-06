package com.zgy.translate.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.widget.ImageView;

import com.zgy.translate.domains.eventbuses.FinishRecorderEB;
import com.zgy.translate.domains.eventbuses.MonitorRecordAmplitudeEB;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouguangyue on 2017/7/12.
 */

public class MediaRecorderUtil {

    //MediaRecorder.getMaxAmplitude 返回最大值32767
    private static final int MAX_AMPLITUDE = 32767;
    private static final int MAX_LEVEL = 5;

    //private ExecutorService executorService;
    private static ScheduledExecutorService executorService;
    private static MediaRecorder mediaRecorder;
    private static File mediaFile;
    //private Handler mMainHandlerThread;
    private static Long startTime , stopTime;
    private static int time;//录音时间
    private static volatile boolean playFlag = false;
    private static volatile boolean recordFlag = false;
    private static MediaPlayer mediaPlayer;
    private static Random random;
    private static FinishRecorderEB finishRecorder;
    private static MonitorRecordAmplitudeEB recordAmplitudeEB; //音量等级

    /**
     * 开启录音
     * */
    public static void startRecorder(final String file, final String fileName) {
        try {
            checkPoolState();
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    releaseRecorder();
                    if(!doStart(file,fileName)){
                        recorderFail();
                    }
                }
            });

            //提交后台获取音量大小的任务
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    recordFlag = true;
                    monitorRecordAmplitude();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开始录音
     * */
    private static boolean doStart(String file,String fileName) {
        mediaRecorder = new MediaRecorder();
        //TODO
        mediaFile = new File(file +"/"+ fileName+"/"+System.currentTimeMillis()+".m4a");
        mediaFile.getParentFile().mkdirs();
        try {
            mediaFile.createNewFile();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioSamplingRate(44100);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setAudioEncodingBitRate(86000);
            mediaRecorder.setOutputFile(mediaFile.getAbsolutePath());

            mediaRecorder.prepare();
            mediaRecorder.start();
            startTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 停止录音
     * */
    public static void stopRecorder(){
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                if(!doStop()){
                    recorderFail();
                }
                releaseRecorder();
            }
        });

    }

    /**
     * 停止录音
     * */
    private static boolean doStop(){

        try {
            mediaRecorder.stop();
            stopTime = System.currentTimeMillis();

            time = (int) ((stopTime - startTime)/1000);
            if(time > 3){
                finishRecorder = new FinishRecorderEB(FinishRecorderEB.RECORDER_OK,"");
                finishRecorder.setFilePath(mediaFile.getAbsolutePath());
                finishRecorder.setRecorderTime(time);
            }else{
                finishRecorder = new FinishRecorderEB(FinishRecorderEB.RECORDER_NO_TIME,"");
            }
            EventBus.getDefault().post(finishRecorder);
        }catch (RuntimeException e){
            return false;
        }

        return true;
    }

    /**
     * 释放录音
     * */
    private static void releaseRecorder(){
        if(mediaRecorder != null){
            mediaRecorder.release();
            mediaRecorder = null;
            recordFlag = false;
            finishRecorder = null;
            recordAmplitudeEB = null;
            executorService.shutdown();
        }
    }

    /**
     * 录音失败
     * */
    private static void recorderFail(){
        finishRecorder = new FinishRecorderEB(FinishRecorderEB.RECORDER_FAIL,"");
        EventBus.getDefault().post(finishRecorder);
        releaseRecorder();
    }



    /**
     * 播放录音
     * */
    public static void playRecord(final File file, final ImageView view, final AudioManager audioManager, final boolean modeFlag){
        //坚持当前状态，防止循环播放
        checkPoolState();
        if(file != null && !playFlag){
            //设置当前播放
            playFlag = true;

            try {
                //提交后台任务
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        doPlay(file,view,audioManager,modeFlag);
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }


    /**
     * 实际播放
     * */
    private static void doPlay(File mediaFile, final ImageView view, AudioManager audioManager, boolean modeFlag){
        mediaPlayer = new MediaPlayer();
        if(modeFlag){ //扬声器
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        }else{
            audioManager.setSpeakerphoneOn(false); //关闭扬声器
            audioManager.setMode(AudioManager.MODE_IN_CALL); // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }

        try {
            mediaPlayer.setDataSource(mediaFile.getAbsolutePath());
            //播放完成回掉
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finishRecorder = new FinishRecorderEB("",FinishRecorderEB.PLAYER_FINISH);
                    finishRecorder.setView(view);
                    EventBus.getDefault().post(finishRecorder);
                    stopPlay();
                }
            });
            //播放出错回掉
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    playFail();
                    stopPlay();
                    return true;
                }
            });
            //配置音量，是否循环
            mediaPlayer.setVolume(1,1);
            mediaPlayer.setLooping(false);

            //准备，开始
            mediaPlayer.prepare();//准备，状态，不然直接开始报错
            mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 播放错误提示
     * */
    private static void playFail(){
        finishRecorder = new FinishRecorderEB("",FinishRecorderEB.PLAYER_FAIL);
        EventBus.getDefault().post(finishRecorder);
    }


    /**
     * 停止播放
     * */
    public static void stopPlay(){
        playFlag = false;
        if(mediaPlayer != null){
            //重设监听
            mediaPlayer.setOnCompletionListener(null);
            mediaPlayer.setOnErrorListener(null);
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            finishRecorder = null;
            executorService.shutdown();
        }
    }

    /**
     * 定期获取录音的音量
     * */
    private static void monitorRecordAmplitude(){
        if(mediaRecorder == null){
            return;
        }
        int amplitude;
        try {
            //获取音量大小
            amplitude = mediaRecorder.getMaxAmplitude();
        }catch (Exception e){
            e.printStackTrace();
            //用一个随机数代表当前的音量大小
            amplitude = random.nextInt(MAX_AMPLITUDE);
        }
        //把音量归一化分5个等级
        int level = amplitude/(MAX_AMPLITUDE/MAX_LEVEL);
        //等级显示到ui上
        recordAmplitudeEB = new MonitorRecordAmplitudeEB();
        recordAmplitudeEB.setLevel(level);
        EventBus.getDefault().post(recordAmplitudeEB);
        //如果仍在录音，50ms以后，再次获取音量大小
        if(recordFlag){
            executorService.schedule(new Runnable() {
                @Override
                public void run() {
                    monitorRecordAmplitude();
                }
            },50, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 查看是否有线程池存在执行任务，有就关闭，建立新线程池
     * */
    private static void checkPoolState(){
        if (executorService != null){
            executorService.shutdown();
        }
        if(playFlag){
            stopPlay();
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

}
