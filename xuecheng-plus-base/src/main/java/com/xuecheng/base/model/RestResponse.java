package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RestResponse<T> {
    private int code;
    private String msg;

    private T result;

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RestResponse() {
        this(0,"success");
    }

    /**
     * 错误信息的封装
     */
    public static <T> RestResponse<T> validfail() {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        return response;
    }

    public static <T> RestResponse<T> validfail(String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        response.setMsg(msg);
        return response;
    }

    public static <T> RestResponse<T> validfail(String msg, T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        response.setMsg(msg);
        response.setResult(result);
        return response;
    }

    /**
     * 正常信息的封装
     */
    public static <T> RestResponse<T> success() {
        return new RestResponse<>();
    }

    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        return response;
    }

    public static <T> RestResponse<T> success(String msg, T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setMsg(msg);
        response.setResult(result);
        return response;
    }
}
