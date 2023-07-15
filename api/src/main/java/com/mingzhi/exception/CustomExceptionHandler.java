package com.mingzhi.exception;

import com.mingzhi.utils.MingzhiJSONResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 自定义异常捕获
 */
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public MingzhiJSONResult handlerMaxUploadFile(MaxUploadSizeExceededException ex) {
        return MingzhiJSONResult.errorMsg("文件上传大小太大了");
    }
}
