package com.plume.plrtime.exception;

import com.plume.plrtime.common.ErrorEnum;
import lombok.Data;

@Data
public class BusinessException extends RuntimeException {

    private Integer errCode;

    private String errorMsg;

    public BusinessException() {
        super();
    }

    public BusinessException(Integer errorCode, String errorMsg) {
        super(errorMsg);
        this.errCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum.getErrorMsg());
        this.errCode = errorEnum.getErrorCode();
        this.errorMsg = errorEnum.getErrorMsg();
    }
}