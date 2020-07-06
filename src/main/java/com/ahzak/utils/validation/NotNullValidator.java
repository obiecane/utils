package com.ahzak.utils.validation;

import cn.hutool.core.util.ArrayUtil;
import com.ahzak.utils.spring.SpringMvcUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/2 11:44
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class NotNullValidator implements ConstraintValidator<NotNull, Object> {

    private RequestMethod[] requestMethods;

    @Override
    public void initialize(NotNull anno) {
        requestMethods = anno.conditionalOnMethod();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (ArrayUtil.isNotEmpty(requestMethods)) {
            String method = SpringMvcUtil.currMethod();
            if (StringUtils.isNotBlank(method)) {
                RequestMethod crm = RequestMethod.valueOf(SpringMvcUtil.currMethod());
                if (!ArrayUtil.contains(requestMethods, crm)) {
                    return true;
                }
            }
        }
        return value != null;
    }
}
