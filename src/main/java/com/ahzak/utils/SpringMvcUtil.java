package com.ahzak.utils;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/10/28 11:42
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@Component
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SpringMvcUtil {

    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpServletRequest request;
    @Autowired(required = false)
    private ApiFinder apiFinder;

    @Value("${spring.application.name}")
    private String applicationName;

    private static HttpServletResponse RESPONSE;
    private static HttpServletRequest REQUEST;
    private static ApiFinder APIFINDER;
    private static String APPLICATION_NAME;


    @PostConstruct
    private void init() {
        REQUEST = request;
        RESPONSE = response;
        APIFINDER = apiFinder;
        APPLICATION_NAME = applicationName.toUpperCase();
    }

    /**
     * 获取当前的request对象
     * 要注意的是在调用该方法的线程如果和controller执行线程不是同一个线程的话
     * 获取的request是无效的
     *
     * @param
     * @return javax.servlet.http.HttpServletRequest
     * @author Zhu Kaixiao
     * @date 2019/10/28 11:37
     */
    public static HttpServletRequest getRequest() {
        return REQUEST;
    }


    /**
     * 获取当前的response对象
     * 要注意的是在调用该方法的线程如果和controller执行线程不是同一个线程的话
     * 获取的response是无效的
     *
     * @param
     * @return javax.servlet.http.HttpServletRequest
     * @author Zhu Kaixiao
     * @date 2019/10/28 11:37
     */
    public static HttpServletResponse getResponse() {
        return RESPONSE;
    }


    /**
     * 获取当前访问的接口  因为前端调用的接口要经过网关转发, 所以实际访问的接口前面有一个当前服务的名称
     *
     * @param
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/11/26 15:16
     */
    public static String currentPath() {
        String servletPath = getRequest().getServletPath();
        return "/" + APPLICATION_NAME + servletPath;
    }

    public static String currentMethod() {
        return getRequest().getMethod().toUpperCase();
    }

    public static String getToken() {
        return getToken(getRequest());
    }

    public static String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String authToken = null;

        try {
            if (StringUtils.isNotBlank(authorization)) {
                authToken = StringUtils.substringAfter(authorization, " ").trim();
            }
            if (StringUtils.isBlank(authToken)) {
                String access_token = request.getParameter("access_token");
                if (StringUtils.isNotBlank(access_token)) {
                    authToken = access_token;
                }
            }
        } catch (Exception e) {
            log.debug("从请求中取token失败", e);
        }

        return authToken;
    }


    public static String currentRequestBody() {
        return fetchRequestBody(getRequest());
    }

    public static String fetchRequestBody(HttpServletRequest request) {
        String body = null;
        try {
//            String str = httpServletRequest.getQueryString();
            BufferedReader bufferedReader = request.getReader();
            body = IoUtil.read(bufferedReader);
        } catch (Exception e) {
        }
        return body;
    }

    public static void print() {
        try {
            HttpServletRequest httpServletRequest = getRequest();

            String str = httpServletRequest.getQueryString();
            BufferedReader bufferedReader = httpServletRequest.getReader();
            String bodyStr = IoUtil.read(bufferedReader);
            System.out.println("bodyStr = " + bodyStr);
        } catch (Exception e) {
            log.info("请求参数不合法");
            e.printStackTrace();
        }
    }
}
