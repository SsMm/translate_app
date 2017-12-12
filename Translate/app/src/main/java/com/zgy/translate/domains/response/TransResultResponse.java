package com.zgy.translate.domains.response;

import com.zgy.translate.domains.dtos.TransResultDTO;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/12/12.
 */

public class TransResultResponse{

    private String from;
    private String to;
    private List<TransResultDTO> trans_result; //翻译结果

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

    public List<TransResultDTO> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<TransResultDTO> trans_result) {
        this.trans_result = trans_result;
    }
}
