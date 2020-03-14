/*
 * @(#)Operation.java
 * Copyright (C) 2019 Neusoft Corporation All rights reserved.
 *
 * VERSION        DATE       BY              CHANGE/COMMENT
 * ----------------------------------------------------------------------------
 * @version 1.00  2019-03-29 Golconda          初版
 *
 */

package com.ahzak.utils.excel2.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zhu Kaixiao
 * @date 2020/3/14 13:40
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExport {

    /**
     * excel文件名
     * 不用加后缀名, 加了也会自动去掉
     */
    String name() default "export";

    /**
     * 字段在excel中的表头
     */
    String[] heads();

    /**
     * 需要生成excel的字段
     */
    String[] fields();
}
