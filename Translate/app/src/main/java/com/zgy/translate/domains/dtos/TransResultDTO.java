package com.zgy.translate.domains.dtos;

import com.zgy.translate.base.BaseDomain;

/**
 * Created by zhouguangyue on 2017/12/11.
 */

public class TransResultDTO extends BaseDomain{

    private String src; //原文
    private String dst; //译文

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
