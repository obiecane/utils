package com.ahzak.utils.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/20 17:11
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 只做手机号格式校验, 非空校验可以再加@NotBlank
        if (StringUtils.isNotBlank(value)) {
            return value.matches("^1\\d{10}$");
        }
        return true;
    }
}
