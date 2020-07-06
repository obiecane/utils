package com.ahzak.utils.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/17 14:36
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Configuration
@ConditionalOnBean(SpringMvcUtil.class)
class ClearUpFilterConfig {

    @Bean
    public FilterRegistrationBean<SpringMvcUtilClearUpFilter> registerLoginCheckFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new SpringMvcUtilClearUpFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("springMvcUtilClearUpFilter");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
