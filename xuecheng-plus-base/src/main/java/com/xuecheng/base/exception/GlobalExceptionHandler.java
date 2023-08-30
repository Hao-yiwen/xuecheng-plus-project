package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//    对项目自定义异常处理
    @ExceptionHandler(XueChengPlusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(XueChengPlusException e) {

        log.error("系统异常{}",e.getErrMessage(),e);

        String errMessage = e.getErrMessage();
        RestErrorResponse response = new RestErrorResponse(errMessage);
        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(XueChengPlusException e) {

        log.error("系统异常{}",e.getErrMessage(),e);

        RestErrorResponse response = new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
        return response;
    }
}
