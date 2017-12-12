package com.zgy.translate.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by zhouguangyue on 2017/7/25.
 */

public class GsonManager {

    private static Gson gson = null;

    public static Gson getInstance(){
        if(gson == null){
            synchronized (GsonManager.class){
                if(gson == null){
                    gson =  new GsonBuilder().serializeNulls().create();
                }
            }
        }
        return gson;
    }

}
