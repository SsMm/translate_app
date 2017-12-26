package com.zgy.translate.base;

/**
 * Created by zhou on 2017/5/24.
 */

public class BaseResponseObject {


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
