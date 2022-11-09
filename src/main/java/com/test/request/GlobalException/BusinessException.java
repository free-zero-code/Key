package com.test.request.GlobalException;

public class BusinessException extends Exception {
    private String msg;

    @Override
    public String getMessage() {
        return msg;
    }

    public BusinessException(String msg) {
        this.msg = msg;
    }
}