package com.ahzak.utils.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/10/21 10:51
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static void publishEvent(ApplicationEvent event) {
        getApplicationContext().publishEvent(event);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.debug("注入applicationContext啦~~~");
        if (applicationContext.getParent() == SpringUtil.applicationContext
                || SpringUtil.applicationContext == null) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return getApplicationContext().getBean(requiredType);
    }

    public static <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
        return getApplicationContext().getBeansOfType(type);
    }

    public static Object getBean(String name) throws BeansException {
        return getApplicationContext().getBean(name);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        Map<String, Object> beansWithAnnotation = getApplicationContext().getBeansWithAnnotation(annotationType);
        return beansWithAnnotation;
    }

    /**
     * 获取当前运行项目的路径
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/11/18 9:20
     */
    public static String appHome() {
        ApplicationHome home = new ApplicationHome();
        return home.getDir().getAbsolutePath() + File.separator;
    }

}
