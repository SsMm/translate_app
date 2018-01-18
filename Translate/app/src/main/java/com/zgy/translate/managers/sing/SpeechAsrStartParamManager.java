package com.zgy.translate.managers.sing;


import com.zgy.translate.global.GlobalInit;
import com.zgy.translate.global.GlobalParams;
import com.zgy.translate.managers.CacheManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouguangyue on 2017/12/11.
 */

public class SpeechAsrStartParamManager {
    
    private static Map<String, Object> map;

   /* static {
        map = new HashMap<>();
        map.put(SpeechConstant.ACCEPT_AUDIO_DATA, false);
        map.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, true); //音量
        map.put(SpeechConstant.VAD_ENDPOINT_TIMEOUT, 0);
        map.put(SpeechConstant.VAD, "dnn");
        map.put(SpeechConstant.DISABLE_PUNCTUATION, true); //标点
    }*/

    private static SpeechAsrStartParamManager manager;

    public static SpeechAsrStartParamManager getInstance() {
        if(manager == null){
            synchronized (SpeechAsrStartParamManager.class){
                if(manager == null){
                    manager =  new SpeechAsrStartParamManager();
                }
            }
        }
        return manager;
    }

    /**中文输入*/
    public SpeechAsrStartParamManager createCN(){
        //map.put(SpeechConstant.PID, "1537");
        return manager;
    }

    /**英文输入*/
    public SpeechAsrStartParamManager createEN(){
        //map.put(SpeechConstant.PID, "1737");
        return manager;
    }

    /**麦克风输入源*/
    public SpeechAsrStartParamManager createVoide(){
        /*if(map.containsKey(SpeechConstant.IN_FILE)){
            map.remove(SpeechConstant.IN_FILE);
        }*/
        return manager;
    }

    /**耳机输入源*/
    public SpeechAsrStartParamManager createBlue(String path){
        //map.put(SpeechConstant.IN_FILE, path);
        return manager;
    }

    public Map<String, Object> build(){
        return map;
    }
    
    
}
