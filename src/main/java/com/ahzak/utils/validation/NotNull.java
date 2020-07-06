package com.ahzak.utils.validation;

import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/30 10:37
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Documented
@Constraint(validatedBy = {NotNullValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface NotNull {

    /**
     * 指定在那种http 方法下启用当前校验
     * 如果未配置,则在所有方法下都启用
     */
    RequestMethod[] conditionalOnMethod() default {};

    String message() default "{javax.validation.constraints.NotNull.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
