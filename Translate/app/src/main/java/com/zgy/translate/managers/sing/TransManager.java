package com.zgy.translate.managers.sing;

import com.zgy.translate.domains.dtos.TransParamsDTO;
import com.zgy.translate.global.GlobalKey;
import com.zgy.translate.utils.MD5;


/**
 * Created by zhouguangyue on 2017/12/11.
 */

public class TransManager {

    private static TransManager transManager;
    private static TransParamsDTO params = new TransParamsDTO();


    public static TransManager getInstance() {
        if(transManager == null){
            synchronized (TransManager.class){
                if(transManager == null){
                    transManager = new TransManager();
                }
            }
        }
        return transManager;
    }

    /**拼接翻译参数*/
    public TransManager params(String query, String from, String to){
        params.setQ(query);
        params.setFrom(from);
        params.setTo(to);
        params.setAppid(GlobalKey.TRANS_APP_ID);
        String salt = String.valueOf(System.currentTimeMillis());
        params.setSalt(salt);
        String src = GlobalKey.TRANS_APP_ID + query + salt + GlobalKey.TRANS_SECURITY_KEY;
        params.setSign(MD5.md5(src));
        return transManager;
    }

    public TransParamsDTO build(){
        return params;
    }



}
