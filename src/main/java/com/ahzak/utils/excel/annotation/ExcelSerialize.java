package com.ahzak.utils.excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/6/4 11:19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSerialize {
    /**
     * 获取注解类
     */
    Class clazz();

    /**
     * 需要生成excel的字段
     */
    String[] fields();

    /**
     * 字段在excel中的表头
     */
    String[] heads();
}
