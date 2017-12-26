package com.zgy.translate.domains.dtos;

import com.zgy.translate.base.BaseDomain;

/**
 * Created by zhouguangyue on 2017/12/26.
 */

public class ErrorsDTO extends BaseDomain {
    private String Code;
    private String Message;


    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
