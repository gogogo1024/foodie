package com.mingzhi.utils;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 校验工具
 */
public class ValidationUtil {
    /**
     * 以field为键，defaultMessage为值的Map
     *
     * @param result BindingResult错误
     * @return 键值对错误
     */
    public static Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> list = result.getFieldErrors();
        for (FieldError fe : list) {
            String field = fe.getField();
            String errorMsg = fe.getDefaultMessage();
            map.put(field, errorMsg);
        }
        return map;
    }
}
