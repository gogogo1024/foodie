package com.mingzhi.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PojoUtils {
    /*
    设置Class实例属性为空
    支持类型为String,Date的属性
    */
    public static <E> void setPojoNullProperty(E pojo, String[] nullProperty) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Set<String> nullPropertySet = new HashSet<String>(Arrays.asList(nullProperty));
        Field[] f = pojo.getClass().getDeclaredFields();
        for (Field field : f) {
            for (String property : nullPropertySet) {
                String propertyName = field.getName();
                if (propertyName.equals(property)) {
                    String methodName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                    Class<?> fileType = field.getType();
                    Method setMethod = pojo.getClass().getMethod("set" + methodName, fileType);
                    if (fileType == Date.class) { // 日期
                        setMethod.invoke(pojo, new Date());
                    } else if (field.getType() == String.class) { // 字符串
                        setMethod.invoke(pojo, (Object) null);

                    }

                }
            }
        }
    }

}
