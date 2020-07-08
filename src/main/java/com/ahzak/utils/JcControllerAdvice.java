package com.ahzak.utils;

import com.ahzak.utils.exception.GlobalException;
import com.ahzak.utils.spring.SpringMvcUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.List;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/10/31 17:15
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JcControllerAdvice {


    @ExceptionHandler(GlobalException.class)
    public JcResult jcGlobalException(GlobalException e) {
        log.info("全局异常处理", e);
        return JcResult.fail(e.getCode(), e.getMessage());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JcResult methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindExceptionMessage(bindingResult, e.getMessage());

        log.info("全局异常处理: {}", message);
        return JcResult.fail(message);
    }

    // WebExchangeBindException es ???;
    @ExceptionHandler(BindException.class)
    public JcResult bindExceptionHandler(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = bindExceptionMessage(bindingResult, e.getMessage());
        log.info("全局异常处理: {}", message);
        return JcResult.fail(message);
    }


    private String bindExceptionMessage(BindingResult bindingResult, String exceptionMsg) {
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String message;
        if (allErrors.isEmpty()) {
            message = exceptionMsg;
        } else {
            StringBuilder sb = new StringBuilder();
            for (ObjectError error : allErrors) {
                Object[] arguments = error.getArguments();
                if (arguments == null) {
                    continue;
                }
                for (Object argument : arguments) {
                    if (argument instanceof MessageSourceResolvable) {
                        sb.append(((MessageSourceResolvable) argument).getDefaultMessage()).append(": ");
                        break;
                    }
                }
                sb.append(error.getDefaultMessage()).append("; ");
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 2, sb.length());
            }

            message = sb.toString();
        }

        return message;
    }


    @ExceptionHandler(ValidationException.class)
    public JcResult validationExceptionHandler(ValidationException e) {
        String message = e.getMessage();
        return JcResult.fail(message);
    }


    @ExceptionHandler(ServletRequestBindingException.class)
    public JcResult ServletRequestBindingExceptionHandler(ValidationException e) {
        String message = e.getMessage();
        return JcResult.fail(message);
    }


    @ExceptionHandler(Throwable.class)
    public JcResult throwable(Throwable e) {
        String errorCode = SystemContextUtils.apiErrorCode();
        String servletPath = SpringMvcUtil.currServletPath();
        log.info("全局异常处理", e);
        if (servletPath.startsWith("/front/")) {
            return JcResult.fail(errorCode + " 错误，请联系客服。");
        }

        return JcResult.fail(errorCode + " 错误，请联系售后。");
    }
}
