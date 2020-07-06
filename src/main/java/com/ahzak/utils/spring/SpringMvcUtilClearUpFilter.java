package com.ahzak.utils.spring;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/17 14:25
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class SpringMvcUtilClearUpFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        SpringMvcUtil.mappingThreadLocal.remove();
    }

    @Override
    public void destroy() {

    }
}
