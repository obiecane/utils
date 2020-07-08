package com.ahzak.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/8/28 15:13
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class JcResult<T> {

    public static final int CODE_ERROR = 500;

    private int code;
    private String message;
    private T data;



    public static <E> JcResult<E> ok(int code, String message) {
        return new JcResult<>(code, message, null);
    }

    public static <E> JcResult<E> ok(String message, E data) {
        return new JcResult<>(200, message, data);
    }

    public static <E> JcResult<E> okData(E data) {
        return ok("成功", data);
    }

    public static <E> JcResult<E> ok(String message) {
        return ok(message, null);
    }

    public static <E> JcResult<E> ok() {
        return okData(null);
    }


    public static <E> JcResult<E> fail(int code, String message) {
        return new JcResult<>(code, message, null);
    }

    public static <E> JcResult<E> fail(String message, E data) {
        return new JcResult<>(500, message, data);
    }

    public static <E> JcResult<E> failData(E data) {
        return fail("系统繁忙", data);
    }

    public static <E> JcResult<E> fail(String message) {
        return fail(message, null);
    }

    public static <E> JcResult<E> fail() {
        return failData(null);
    }


    public boolean success() {
        return getCode() == 1
                || (getCode() >= 200 && getCode() < 300);
    }

}
