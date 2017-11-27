package com.zgy.translate.base;

/**
 * Created by zhou on 2017/5/24.
 */

public interface BaseInterface {

    void onResponseFail(String fail); //接口调取失败返回数据
    void onFailure(String failure); //http请求失败返回数据

}
