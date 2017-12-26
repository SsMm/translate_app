package com.zgy.translate.domains.response;


import com.zgy.translate.base.BaseDomain;
import com.zgy.translate.domains.dtos.ErrorsDTO;
import com.zgy.translate.domains.request.CommonRequest;

import java.util.List;

/**
 * Created by zhouguangyue on 2017/7/27.
 */

public class CommonResponse extends CommonRequest{

    private List<ErrorsDTO> Errors;

    public void setErrors(List<ErrorsDTO> errors) {
        Errors = errors;
    }

    public List<ErrorsDTO> getErrors() {
        return Errors;
    }
}
