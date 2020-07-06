package com.ahzak.utils.exception;


import cn.hutool.core.util.StrUtil;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/11/1 13:51
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class GlobalException extends RuntimeException {

    protected int code = 0;

    public GlobalException() {
    }

    public GlobalException(String msg) {
        super(msg);
    }

    public GlobalException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public GlobalException(String format, Object... params) {
        super(StrUtil.format(format, params), mayThrowable(params));
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    private static Throwable mayThrowable(Object... params) {
        if (params.length > 0 && params[params.length - 1] instanceof Throwable) {
            return (Throwable) params[params.length - 1];
        }
        return null;
    }
}
