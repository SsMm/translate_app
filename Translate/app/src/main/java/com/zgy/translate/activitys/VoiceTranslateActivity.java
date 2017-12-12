package com.zgy.translate.activitys;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.zgy.translate.R;

import com.zgy.translate.base.BaseActivity;
import com.zgy.translate.base.BaseResponseObject;
import com.zgy.translate.domains.RecogResult;
import com.zgy.translate.global.GlobalConstants;
import com.zgy.translate.http.HttpGet;
import com.zgy.translate.managers.sing.SpeechAsrStartParamManager;
import com.zgy.translate.managers.sing.TransManager;
import com.zgy.translate.utils.ConfigUtil;
import com.zgy.translate.utils.StringUtil;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceTranslateActivity extends BaseActivity {

    private EventManager mAsr; //识别
    private EventListener asrEventListener;

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
        mAsr = EventManagerFactory.create(this, "asr");
        asrEventListener =  new EventListener() {
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
                                            .params(inputResult, GlobalConstants.ZH, GlobalConstants.EN)
                                            .build());
                                    Log.i("翻译结果", trans);
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
        mAsr.registerListener(asrEventListener);
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
            mAsr.unregisterListener(asrEventListener);
            mAsr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
            mAsr = null;
        }
    }
}
