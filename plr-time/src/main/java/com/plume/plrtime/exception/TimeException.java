package com.plume.plrtime.exception;

import com.plume.plrtime.common.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TimeException implements ErrorEnum {
    ACTIVITY_TID_ERR(5001, "当前活动tID错误"),
    ACTIVITY_RUNNING(5002,"其他活动正在运行")
    ;

    private Integer code;

    private String msg;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorMsg() {
        return msg;
    }
}