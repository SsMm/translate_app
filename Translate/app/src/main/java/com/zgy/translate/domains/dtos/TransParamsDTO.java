package com.zgy.translate.domains.dtos;

import com.zgy.translate.base.BaseDomain;

/**
 * Created by zhouguangyue on 2017/12/11.
 */

public class TransParamsDTO extends BaseDomain{
    private String q;
    private String from;
    private String to;
    private String appid;
    private String salt;
    private String sign;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
