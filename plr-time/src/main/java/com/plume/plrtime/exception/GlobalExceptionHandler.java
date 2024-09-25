package com.plume.plrtime.exception;

import com.plume.plrtime.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = BusinessException.class)
    public Result businessExceptionHandler(BusinessException e) {
        log.info("business exception! reason is {}",e.getErrorMsg(),e);
        return Result.error(e.getErrCode().toString(), e.getErrorMsg());
    }
}