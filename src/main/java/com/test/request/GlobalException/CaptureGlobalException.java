package com.test.request.GlobalException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class CaptureGlobalException {
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler(Exception e) {
        if (e instanceof javax.crypto.BadPaddingException) {
            return "解密失败【aesKey】错误";
        } else if (e instanceof com.test.request.GlobalException.BusinessException) {
            return e.getMessage();
        } else {
            return "系统错误-" + e.getMessage();
        }
    }
}
