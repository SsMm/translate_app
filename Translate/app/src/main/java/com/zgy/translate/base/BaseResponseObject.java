package com.zgy.translate.base;

/**
 * Created by zhou on 2017/5/24.
 */

public class BaseResponseObject {

    public final static String SUCCESS = "0x00001"; //处理成功
    public final static String FAILURE = "0x00002"; //处理失败
    public final static String OFFLINE = "0x00004"; //强制下线
    public final static String LOGIN_OUT = "0x00005"; //退出登录
    public final static String TOKEN_ERROR = "0x00006"; //登陆的token错误
    public final static String PARAM_ERROR = "0x00007"; //参数错误



    private String result;
    private String errorInfo;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    @Override
    public String toString() {
        return "BaseResponseObject{" +
                "result='" + result + '\'' +
                ", errorInfo='" + errorInfo + '\'' +
                '}';
    }
}
