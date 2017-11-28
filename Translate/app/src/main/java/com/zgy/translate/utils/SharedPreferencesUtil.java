package com.zgy.translate.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhouguangyue on 2017/7/26.
 */

public class SharedPreferencesUtil {

    /**
     * 保存数据
     * */
    public static void saveShare(Context context, String key, String value , String user){
        SharedPreferences shared = context.getApplicationContext().getSharedPreferences(user, 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取数据
     * */
    public static String readShare(Context context,String key ,String user){
        String result;
        SharedPreferences shared = context.getApplicationContext().getSharedPreferences(user, 0);
        result = shared.getString(key, null);
        return result;
    }

    /**
     * 删除数据
     * */
    public static void deleteShare(Context context,String user,String key){
        SharedPreferences shared = context.getApplicationContext().getSharedPreferences(user,0);
        SharedPreferences.Editor editor = shared.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * 清空数据
     * */
    public static void clearShare(Context context,String user){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(user,0);
        sharedPreferences.edit().clear().apply();
    }

}
