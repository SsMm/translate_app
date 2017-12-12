package com.zgy.translate.activitys;

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
import com.zgy.translate.http.HttpGet;
import com.zgy.translate.managers.GsonManager;
import com.zgy.translate.managers.sing.SpeechAsrStartParamManager;
import com.zgy.translate.managers.sing.TransManager;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.StringUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceTranslateActivity extends BaseActivity {

    protected String appId = "8535996";

    protected String appKey = "MxPpf3nF5QX0pndKKhS7IXcB";

    protected String secretKey = "7226e84664474aa098296da5eb2aa434";

    private EventManager mAsr; //识别
    private EventListener mAsrEventListener;

    private SpeechSynthesizer mSpeechSynthesizer; //合成
    private SpeechSynthesizerListener mSpeechSynthesizerListener;

    private String inputResult = "";

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
        initSpeech();
        initTTs();
    }

    /**初始化语音识别*/
    private void initSpeech(){
        mAsr = EventManagerFactory.create(this, "asr");
        mAsrEventListener =  new EventListener() {
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

        };
        mAsr.registerListener(mAsrEventListener);
    }

    /**初始化语音合成*/
    private void initTTs(){
        mSpeechSynthesizerListener = new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String utteranceId) {
                //合成过程开始
                Log.i("合成过程开始", "合成过程开始");
            }

            @Override
            public void onSynthesizeDataArrived(String utteranceId, byte[] audioData, int progress) {
                //合成数据过程中的回调接口，返回合成数据和进度，分多次回调
                Log.i("合成数据过程中的回调接口", "合成数据过程中的回调接口");
            }

            @Override
            public void onSynthesizeFinish(String utteranceId) {
                //合成正常结束状态
                Log.i("合成正常结束状态", "合成正常结束状态");
            }

            @Override
            public void onSpeechStart(String utteranceId) {
                //SDK开始控制播放器播放合成的声音。如果使用speak方法会有此回调，使用synthesize没有。

            }

            @Override
            public void onSpeechProgressChanged(String utteranceId, int progress) {
                //播放数据过程中的回调接口，分多次回调。如果使用speak方法会有此回调，使用synthesize没有。

            }

            @Override
            public void onSpeechFinish(String utteranceId) {
                //播放正常结束状态时的回调方法，如果过程中出错，则回调onError，不再回调此接口。

            }

            @Override
            public void onError(String utteranceId, SpeechError speechError) {

            }
        };
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);
        mSpeechSynthesizer.setSpeechSynthesizerListener(mSpeechSynthesizerListener);
        mSpeechSynthesizer.setAppId(appId);
        mSpeechSynthesizer.setApiKey(appKey, secretKey);
        mSpeechSynthesizer.auth(TtsMode.ONLINE); //在线混合
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        mSpeechSynthesizer.initTts(TtsMode.ONLINE); //初始化在线混合
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

    @OnClick(R.id.start_speech) void startSpeech(){
        String json = new JSONObject(SpeechAsrStartParamManager.getInstance()
                .createCN()
                .createVoide()
                .build()).toString();
        Log.i("josn", json);
        mAsr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }

    @OnClick(R.id.stop_speech) void stopSpeech(){
        mAsr.send(SpeechConstant.ASR_STOP, "{}", null, 0, 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAsr != null){
            mAsr.unregisterListener(mAsrEventListener);
            mAsr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
            mAsr = null;
            mAsrEventListener = null;
        }
        if(mSpeechSynthesizer != null){
            mSpeechSynthesizer.setSpeechSynthesizerListener(null);
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            mSpeechSynthesizerListener = null;
        }
    }
}
