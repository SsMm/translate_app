package com.zgy.translate.activitys;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.zgy.translate.R;

import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.base.BaseResponseObject;
import com.zgy.translate.domains.RecogResult;
import com.zgy.translate.domains.response.TransResultResponse;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.global.GlobalKey;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.http.HttpGet;
import com.zgy.translate.managers.CacheManager;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.sing.SpeechAsrStartParamManager;
import com.zgy.translate.managers.sing.TransManager;
import com.zgy.translate.utils.AudioRecordUtil;
import com.zgy.translate.utils.BluetoothRecorder;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.StringUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceTranslateActivity extends BaseActivity implements EventListener, SpeechSynthesizerListener{

    private static final String UTTERANCE_ID = "appolo";

    private EventManager mAsr; //识别
    private SpeechSynthesizer mSpeechSynthesizer; //合成

    private String inputResult = "";
    private AudioManager mAudioManager;

    private File ttsFile;
    private FileOutputStream ttsFileOutputStream;
    private BufferedOutputStream ttsBufferedOutputStream;
    private File mediaRecorderPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_translate);
        ButterKnife.bind(this);
        super.init();
    }

    @Override
    public void initView() {
        baseInit();
    }

    @Override
    public void initEvent() {

    }

    @Override
    public void initData() {

    }

    /**初始化*/
    private void baseInit(){
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initSpeech();
        initTTs();
    }

    /**初始化语音识别*/
    private void initSpeech(){
        mAsr = EventManagerFactory.create(this, "asr");
        mAsr.registerListener(this);
    }

    /**初始化语音合成*/
    private void initTTs(){
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);
        mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        mSpeechSynthesizer.setAppId(GlobalKey.TTS_APP_ID);
        mSpeechSynthesizer.setApiKey(GlobalKey.TTS_APP_KEY, GlobalKey.TTS_SECURITY_KEY);
        mSpeechSynthesizer.auth(TtsMode.ONLINE); //在线混合
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        mSpeechSynthesizer.initTts(TtsMode.ONLINE); //初始化在线混合
    }

    /**
     * 语音识别回调
     * */
    @Override
    public void onEvent(String name, String params, byte[] bytes, int offset, int length) {
        switch (name){
            case SpeechConstant.CALLBACK_EVENT_ASR_READY: // 引擎准备就绪，可以开始说话
                ConfigUtil.showToask(VoiceTranslateActivity.this, "开始讲话。。。");
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_BEGIN: // 检测到用户的已经开始说话
                Log.i("speech--BEGIN", "开始说话");
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_END: // 检测到用户的已经停止说话
                Log.i("speech--END", "停止说话");
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL: // 临时识别结果, 长语音模式需要从此消息中取出结果
                RecogResult recogResult = RecogResult.parseJson(params);
                Log.i("speech--PARTIAL", params);
                String[] results = recogResult.getResultsRecognition();
                if(recogResult.isFinalResult()){
                    List<String> list = new ArrayList<>();
                    list.addAll(Arrays.asList(results));
                    if(list.size() > 0){
                        inputResult += list.get(0);
                    }
                }
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_FINISH: // 识别结束， 最终识别结果或可能的错误
                RecogResult recogResult2 = RecogResult.parseJson(params);
                Log.i("speech--FINISH", params);
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_LONG_SPEECH:
                Log.i("长语音结束", "长语音结束");
                Log.i("结束后录音结果", inputResult);
                if(!StringUtil.isEmpty(inputResult)){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String trans = HttpGet.get(TransManager.getInstance()
                                    .params(inputResult, GlobalConstants.CH, GlobalConstants.EN)
                                    .build());
                            if(StringUtil.isEmpty(trans)){
                                return;
                            }
                            Log.i("翻译结果", trans);
                            String tt = GsonManager.getInstance()
                                    .fromJson(trans, TransResultResponse.class)
                                    .getTrans_result()
                                    .get(0)
                                    .getDst();
                            String t;
                            try {
                                t = URLDecoder.decode(tt, "utf-8");
                                Log.i("合成文本", t);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSpeechSynthesizer.speak(t);
                                        //mSpeechSynthesizer.synthesize(t, UTTERANCE_ID);
                                    }
                                });
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }else{
                    Log.i("没有输入", "没有输入");
                }
                break;
            case SpeechConstant.CALLBACK_EVENT_ASR_VOLUME:
                Volume vol = parseVolumeJson(params);
                Log.i("音量", vol.volumePercent + "");
                break;
        }
    }

    private Volume parseVolumeJson(String jsonStr) {
        Volume vol = new Volume();
        vol.origalJson = jsonStr;
        try {
            JSONObject json = new JSONObject(jsonStr);
            vol.volumePercent = json.getInt("volume-percent");
            vol.volume = json.getInt("volume");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return vol;
    }

    private class Volume {
        private int volumePercent = -1;
        private int volume = -1;
        private String origalJson;
    }

    /**
     * 语音合成回调
     * */
    @Override
    public void onSynthesizeStart(String utteranceId) {
        //合成过程开始
        if(UTTERANCE_ID.equals(utteranceId)){
            Log.i("合成过程开始", "合成过程开始");
            try {
                ttsFileOutputStream = new FileOutputStream(getPathFile(true));
                ttsBufferedOutputStream = new BufferedOutputStream(ttsFileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSynthesizeDataArrived(String utteranceId, byte[] audioData, int progress) {
        //合成数据过程中的回调接口，返回合成数据和进度，分多次回调
        if(UTTERANCE_ID.equals(utteranceId)){
            Log.i("合成数据过程中的回调接口", "合成数据过程中的回调接口");
            try {
                ttsBufferedOutputStream.write(audioData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onSynthesizeFinish(String utteranceId) {
        //合成正常结束状态
        if(UTTERANCE_ID.equals(utteranceId)){
            Log.i("合成正常结束状态", "合成正常结束状态");
            //BluetoothRecorder.startPlaying(ttsFile.getAbsolutePath(), VoiceTranslateActivity.this, mAudioManager);
            AudioRecordUtil.startTrack(ttsFile, mAudioManager);
            close();
        }

    }

    @Override
    public void onError(String utteranceId, SpeechError speechError) {
        Log.i("合成正常结束状态", "合成出错");
    }

    private void close(){
        if(ttsBufferedOutputStream != null){
            try {
                ttsBufferedOutputStream.flush();
                ttsBufferedOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ttsBufferedOutputStream = null;
        }

        if(ttsFileOutputStream != null){
            try {
                ttsFileOutputStream.close();
                ttsFileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClick(R.id.start_speech) void startInput(){
        mediaRecorderPath = getPathFile(false);
        //BluetoothRecorder.startRecording(this, mAudioManager, GlobalParams.DEMO_PATH);
        /*AudioRecordUtil.startRecord(mediaRecorderPath, this, mAudioManager,
                new AudioRecordUtil.AudioRecordInterface() {
            @Override
            public void openBlueSCO() {
                toCNSpeech(false);
            }
        });*/
        BluetoothRecorder.startPlaying(GlobalParams.DEMO_PATH, this, mAudioManager);

    }

    @OnClick(R.id.stop_speech) void stopInput(){
        //BluetoothRecorder.stopRecording(this, mAudioManager);
        //AudioRecordUtil.stopRecord();
        //stopSpeech();
    }

    /**中文输入*/
    private void toCNSpeech(boolean flag){
        String  json;
        if(flag){
            json = new JSONObject(SpeechAsrStartParamManager.getInstance()
                    .createCN()
                    .createVoide()
                    .build()).toString();
            Log.i("cn-voide-josn", json);
        }else{
             json = new JSONObject(SpeechAsrStartParamManager.getInstance()
                    .createCN()
                    .createBlue(mediaRecorderPath.getAbsolutePath())
                    .build()).toString();
            Log.i("cn-blue-josn", json);
        }
        mAsr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    /**英文输入*/
    private void toENSpeech(boolean flag){
        String  json;
        if(flag){
            json = new JSONObject(SpeechAsrStartParamManager.getInstance()
                    .createEN()
                    .createVoide()
                    .build()).toString();
            Log.i("en-voide-josn", json);
        }else{
            json = new JSONObject(SpeechAsrStartParamManager.getInstance()
                    .createEN()
                    .createBlue(mediaRecorderPath.getAbsolutePath())
                    .build()).toString();
            Log.i("en-blue-josn", json);
        }
        mAsr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    private void stopSpeech(){
        mAsr.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }

    private File getPathFile(boolean flag){
        if(flag){ //true是蓝牙播放 false是从蓝牙录音
            ttsFile = new File(CacheManager.createTmpDir(this, GlobalParams.FILE_NAME), GlobalParams.PLAY_PATH);
            Log.i("ttfile", ttsFile.getAbsolutePath());
        }else {
            ttsFile = new File(CacheManager.createTmpDir(this, GlobalParams.FILE_NAME), GlobalParams.RECORDER_PATH);
            Log.i("ttfile", ttsFile.getAbsolutePath());
        }
        if(ttsFile.exists()){
            ttsFile.delete();
        }

        try {
            ttsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ttsFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAsr != null){
            mAsr.unregisterListener(this);
            mAsr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
            mAsr = null;
        }
        if(mSpeechSynthesizer != null){
            mSpeechSynthesizer.setSpeechSynthesizerListener(null);
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
        }
    }

    @Override
    public void onSpeechStart(String s) {

    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {

    }

    @Override
    public void onSpeechFinish(String s) {

    }
}
