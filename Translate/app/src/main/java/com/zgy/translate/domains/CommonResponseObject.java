package com.zgy.translate.domains;


import com.zgy.translate.base.BaseResponseObject;

/**
 * Created by zhouguangyue on 2017/7/27.
 */

public class CommonResponseObject<T> extends BaseResponseObject {

    private T responseData;

    public T getResponseData() {
        return responseData;
    }

    public void setResponseData(T responseData) {
        this.responseData = responseData;
    }
}
