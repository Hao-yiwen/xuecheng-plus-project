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
}
