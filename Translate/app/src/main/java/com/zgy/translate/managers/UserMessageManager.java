package com.zgy.translate.managers;

import android.content.Context;
import android.text.TextUtils;

import com.zgy.translate.controllers.ActivityController;
import com.zgy.translate.domains.dtos.UserInfoDTO;
import com.zgy.translate.utils.SharedPreferencesUtil;

/**
 * Created by zhou on 2017/5/23.
 */

public class UserMessageManager {
    private static final String USER = "user"; // 存放当前用户
    private static final String USER_ID = "userId"; //当前用户idkey

    private static final String USER_INFO = "userInfo"; //用户信息key

    private UserMessageManager(){}

    /**
     * 保存当前登录用户
     * */
    public static void saveLoginUser(Context context, String data){
        SharedPreferencesUtil.saveShare(context, USER_ID, data, USER);
    }

    /**
     * 读取当前登录者用户名字
     * */
    public static String readLoginUser(Context context){
        String result = SharedPreferencesUtil.readShare(context, USER_ID, USER);
        if(TextUtils.isEmpty(result)){
            return null;
        }else{
            return result;
        }
    }

    /**
     * 保存用户信息
     * */
    public static void saveUserInfo(Context context, String shareName, String data){
        SharedPreferencesUtil.saveShare(context, USER_INFO, data, shareName);
    }

    /**
     * 判断当前用户信息
     * */
    public static boolean isUserInfo(Context context, String name){
        String result = SharedPreferencesUtil.readShare(context, USER_INFO, name);
        if(TextUtils.isEmpty(result)){
            return false;  //没有用户信息
        }else{
            return true;  //有用户信息
        }
    }

    /**
     * 返回用户信息---字符串形式
     * */
    public static String getUserInfoToString(Context context, String shareName){
        return SharedPreferencesUtil.readShare(context, USER_INFO, shareName);
    }

    /**
     * 返回用户信息---对象
     * */
    public static UserInfoDTO getUserInfo(Context context, String shareName){
        String result = SharedPreferencesUtil.readShare(context, USER_INFO, shareName);
        return GsonManager.getInstance().fromJson(result, UserInfoDTO.class);
    }

    /**
     * 直接获取当前登录者信息
     * */
    public static UserInfoDTO quickGetUserInfo(Context context){
        if(readLoginUser(context) != null){
            return getUserInfo(context, readLoginUser(context));
        }else{
            return null;
        }
    }

    /**
     * 删除用户信息
     * */
    public static void deleteUserInfo(Context context,String shareName){
        SharedPreferencesUtil.deleteShare(context,shareName,USER_INFO);
    }

    /**
     * 用户退出
     * */
    public static void exitUser(Context context){
        ActivityController.finishActivity();
        SharedPreferencesUtil.deleteShare(context, USER, USER_ID);
    }

}