package com.zgy.translate.domains.dtos;


/**
 * Created by zhouguangyue on 2017/12/12.
 */

public class VoiceTransDTO {

    private String lagType; //语言类型
    private String lanImg; //语言图片
    private String lanSrc; //源语言文本
    private String lanDst; //翻译语言文本

    public void setLagType(String lagType) {
        this.lagType = lagType;
    }

    public String getLagType() {
        return lagType;
    }

    public String getLanImg() {
        return lanImg;
    }

    public void setLanImg(String lanImg) {
        this.lanImg = lanImg;
    }

    public String getLanSrc() {
        return lanSrc;
    }

    public void setLanSrc(String lanSrc) {
        this.lanSrc = lanSrc;
    }

    public String getLanDst() {
        return lanDst;
    }

    public void setLanDst(String lanDst) {
        this.lanDst = lanDst;
    }
}
