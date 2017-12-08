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
import com.zgy.translate.utils.ConfigUtil;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceTranslateActivity extends BaseActivity {

    private EventManager mAsr; //识别
    private EventListener asrEventListener;


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

    @OnClick(R.id.start_speech) void startSpeech(){
        mAsr.send(SpeechConstant.ASR_START, "{}", null, 0, 0);
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
                        Log.i("speech--PARTIAL", params);
                        break;
                    case SpeechConstant.CALLBACK_EVENT_ASR_FINISH: // 识别结束， 最终识别结果或可能的错误
                        Log.i("speech--FINISH", params);
                        break;
                }
            }

        };
        mAsr.registerListener(asrEventListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAsr != null){
            mAsr.unregisterListener(asrEventListener);
        }
    }
}
