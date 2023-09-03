package com.xuecheng.base.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

        log.error("系统异常{}",e.getMessage(),e);

        RestErrorResponse response = new RestErrorResponse(CommonError.UNKNOWN_ERROR.getErrMessage());
        return response;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodExpection(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        List<String> errors = new ArrayList<>();
        bindingResult.getFieldErrors().stream().forEach(item ->{
            errors.add(item.getDefaultMessage());
        });

//        将list信息拼接起来
        String errMessage = StringUtils.join(errors, ",");

        log.error("系统异常{}",e.getMessage(),e);

        RestErrorResponse response = new RestErrorResponse(errMessage);
        return response;
    }
}
