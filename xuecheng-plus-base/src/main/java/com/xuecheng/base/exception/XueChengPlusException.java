package com.xuecheng.base.exception;

public class XueChengPlusException extends RuntimeException {
    private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public XueChengPlusException() {
    }

    public XueChengPlusException(String message) {
        super(message);
        this.errMessage = message;
    }

    public static void cast(String message) {
        throw new XueChengPlusException(message);
    }

    public static void cast(CommonError error) {
        throw new XueChengPlusException(error.getErrMessage());
    }
}
