package com.zgy.translate.domains.dtos;

import com.zgy.translate.base.BaseDomain;

/**
 * Created by zhouguangyue on 2017/12/11.
 */

public class TransResultDTO extends BaseDomain{
    private String from;
    private String to;
    private String trans_result; //翻译结果
    private String src; //原文
    private String dst; //译文

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

    public String getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(String trans_result) {
        this.trans_result = trans_result;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDst() {
        return dst;
    }

    public void setDst(String dst) {
        this.dst = dst;
    }
}
