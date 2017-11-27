package com.zgy.translate.base;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.zgy.translate.utils.ConfigUtil;

import java.lang.ref.WeakReference;

/**
 * Created by zhou on 2017/6/16.
 */

public abstract class BaseController {

    private Bundle checkBundle = null;


    /**
     * 检查初始化
     * */
    public boolean initCheck(Context context,BaseResponseObject responseObject){
        if(checkObjectIsNull(context,responseObject)){
            return true;
        }else {
            checkToken(context,responseObject);
            return false;
        }
    }

    private boolean checkObjectIsNull(Context context,BaseResponseObject responseObject){
        if(responseObject == null || responseObject.getResult() == null){
            ConfigUtil.showToask(context,"数据异常");
            return true;
        }
        return false;
    }


    /**
     * token失效或者过期
     * */
    private void checkToken(Context context,BaseResponseObject responseObject){
        WeakReference<Context> contextWeakReference = new WeakReference<Context>(context);
    }

}
