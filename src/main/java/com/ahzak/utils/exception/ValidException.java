package com.ahzak.utils.exception;


/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/12/10 16:57
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class ValidException extends GlobalException {

    public ValidException() {
    }

    public ValidException(String msg) {
        super(msg);
    }

    public ValidException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public ValidException(String format, Object... params) {
        super(format, params);
    }
}
