package com.ahzak.utils.spring;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReflectUtil;
import com.ahzak.utils.JwtUtil;
import com.ahzak.utils.TokenUtil;
import com.auth0.jwt.interfaces.Claim;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.time.Duration;
import java.util.*;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/10/28 11:42
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SpringMvcUtil {

    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HttpServletRequest request;


    @Value("${spring.application.name}")
    private String applicationName;

    private static HttpServletResponse RESPONSE;
    private static HttpServletRequest REQUEST;
    private static String APPLICATION_NAME;

    /**
     * 暂时没有想到好的办法获取当前mapping, 就先用aop切controller
     */
    static ThreadLocal<String> mappingThreadLocal = new ThreadLocal<>();


    @PostConstruct
    private void init() {
        REQUEST = request;
        RESPONSE = response;
        APPLICATION_NAME = applicationName.toUpperCase();
    }





    public static String getClientId(HttpServletRequest request) {
        return getClientId(getToken(request));
    }

    public static String getClientId(String token) {
        Map<String, Claim> claims = JwtUtil.getClaims(token);
        String clientId = Optional.ofNullable(claims)
                .map(c -> c.get("client_id"))
                .map(Claim::asString)
                .orElse(null);
        return clientId;
    }

    public static String getMethod(HttpServletRequest request) {
        return request.getMethod().toUpperCase();
    }

    public static void addCookie(String key, String value) {
        addCookie(key, value, "/");
    }

    public static void addCookie(String key, String value, String path) {
        addCookie(key, value, path, null);
    }

    public static void addCookie(String key, String value, String path, Integer maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        addCookie(cookie);
    }

    public static void addCookie(String key, String value, Integer maxAge) {
        addCookie(key, value, "/", maxAge);
    }

    public static void addCookie(String key, String value, Duration duration) {
        Integer maxAge = duration == null ? null : (int) (duration.toMillis() / 1000);
        addCookie(key, value, "/", maxAge);
    }

    public static void addCookie(Cookie cookie) {
        if (StringUtils.isBlank(cookie.getPath())) {
            cookie.setPath("/");
        }
        getResponse().addCookie(cookie);
    }

    public static String getCookie(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Cookie[] cookies = getRequest().getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void delCookie(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        Cookie[] cookies = getRequest().getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                cookie.setMaxAge(0);
                cookie.setValue(null);
                addCookie(cookie);
                return;
            }
        }
        return;
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
     * 获取当前接口的ContextPath
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:21
     */
    public static String currContextPath() {
        return getRequest().getContextPath();
    }

    /**
     * 获取当前接口的映射路径
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:22
     */
    public static String currMappingPath() {
        if (mappingThreadLocal.get() == null) {
            String mappingPattern = null;
            Map<String, DispatcherServlet> dispatcherServletMap = SpringUtil.getBeansOfType(DispatcherServlet.class);
            for (DispatcherServlet dispatcherServlet : dispatcherServletMap.values()) {
                List<HandlerMapping> handlerMappings = dispatcherServlet.getHandlerMappings();
                mappingPattern = fetchMappingPattern(handlerMappings, getRequest());
                if (mappingPattern != null) {
                    break;
                }
            }
            if (mappingPattern == null) {
                mappingPattern = currServletPath();
            }
            mappingThreadLocal.set(mappingPattern);
        }
        return mappingThreadLocal.get();
    }

    public static String getModuleMappingPath(HttpServletRequest request) {
        String mappingPattern = null;
        Map<String, DispatcherServlet> dispatcherServletMap = SpringUtil.getBeansOfType(DispatcherServlet.class);
        for (DispatcherServlet dispatcherServlet : dispatcherServletMap.values()) {
            List<HandlerMapping> handlerMappings = dispatcherServlet.getHandlerMappings();
            mappingPattern = fetchMappingPattern(handlerMappings, request);
            if (mappingPattern != null) {
                break;
            }
        }
        if (mappingPattern == null) {
            mappingPattern = currServletPath();
        }
        return currModulePrefix() + mappingPattern;
    }

    /**
     * 获取当前接口含模块名的映射路径
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:22
     */
    public static String currModuleMappingPath() {
        return currModulePrefix() + currMappingPath();
    }


    /**
     * 获取匹配的路径
     * (模拟dispatcherServlet)
     *
     * @param handlerMappings
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 14:16
     */
    private static String fetchMappingPattern(List<HandlerMapping> handlerMappings, HttpServletRequest request) {
        for (HandlerMapping handlerMapping : handlerMappings) {
            try {
                if (handlerMapping instanceof AbstractHandlerMethodMapping) {
                    AbstractHandlerMethodMapping ahm = (AbstractHandlerMethodMapping) handlerMapping;
                    String lookupPath = ahm.getUrlPathHelper().getLookupPathForRequest(request);
                    // AbstractHandlerMethodMapping.MappingRegistry
                    Object mappingRegistry = ReflectUtil.invoke(ahm, "getMappingRegistry");
                    List<RequestMappingInfo> mappingInfos = ReflectUtil.invoke(mappingRegistry, "getMappingsByUrl", lookupPath);
                    if (mappingInfos == null) {
                        Map<RequestMappingInfo, HandlerMethod> mappings = ReflectUtil.invoke(mappingRegistry, "getMappings");
                        Set<RequestMappingInfo> requestMappingInfos = mappings.keySet();
                        Iterator<RequestMappingInfo> iterator = requestMappingInfos.iterator();
                        while (iterator.hasNext()) {
                            RequestMappingInfo mappingInfo = iterator.next().getMatchingCondition(request);
                            String pattern = fetchPatternFromRequestMappingInfo(mappingInfo);
                            if (pattern != null) {
                                return pattern;
                            }
                        }
                    }
                    if (mappingInfos != null) {
                        for (RequestMappingInfo mappingInfo : mappingInfos) {
                            String pattern = fetchPatternFromRequestMappingInfo(mappingInfo);
                            if (pattern != null) {
                                return pattern;
                            }
                        }
                    }

                } else if (handlerMapping instanceof AbstractUrlHandlerMapping) {
                    AbstractUrlHandlerMapping urlHandlerMapping = (AbstractUrlHandlerMapping) handlerMapping;
                    String lookupPath = urlHandlerMapping.getUrlPathHelper().getLookupPathForRequest(request);
                    if (urlHandlerMapping.getHandlerMap().containsKey(lookupPath)) {
                        return lookupPath;
                    }
                    for (String registeredPattern : urlHandlerMapping.getHandlerMap().keySet()) {
                        if (urlHandlerMapping.getPathMatcher().match(registeredPattern, lookupPath)) {
                            return registeredPattern;
                        } else if (urlHandlerMapping.useTrailingSlashMatch()) {
                            if (!registeredPattern.endsWith("/") && urlHandlerMapping.getPathMatcher().match(registeredPattern + "/", lookupPath)) {
                                return registeredPattern;
                            }
                        }
                    }
                }
            } catch (Throwable ignore) {
            }
        }

        return null;
    }


    private static String fetchPatternFromRequestMappingInfo(RequestMappingInfo requestMappingInfo) {
        Set<String> patterns = Optional
                .ofNullable(requestMappingInfo)
                .map(RequestMappingInfo::getPatternsCondition)
                .map(PatternsRequestCondition::getPatterns)
                .orElse(Collections.emptySet());
        Iterator<String> iterator = patterns.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    /**
     * 获取当前访问的接口
     *
     * @param
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/11/26 15:16
     */
    public static String currServletPath() {
        String servletPath = getRequest().getServletPath();
        return servletPath;
    }

    /**
     * 获取当前访问的接口
     * 因为前端调用的接口要经过网关转发, 所以实际访问的接口前面有一个当前服务的名称
     * @param
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:23
     */
    public static String currModulePath() {
        return currModulePrefix() + currServletPath();
    }

    /**
     * 获取当前服务的前缀
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:23
     */
    public static String currModulePrefix() {
        return "/" + APPLICATION_NAME;
    }

    /**
     * 获取当前接口的调用方法
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:23
     */
    public static String currMethod() {
        return getMethod(getRequest());
    }

    /**
     * 获取当前的token
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:23
     */
    public static String currToken() {
        return getToken(getRequest());
    }

    /**
     * 获取当前的clientId
     * 这是指的Oauth2中的clientId
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:23
     */
    public static String currClientId() {
        return getClientId(getRequest());
    }


    /**
     * 从请求中获取token
     * @param request
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:24
     */
    public static String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        String authToken = null;

        try {
            if (StringUtils.isNotBlank(authorization)) {
                authToken = TokenUtil.getTokenFromAuthorization(authorization);
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


    /**
     * 获取当前请求的RequestBody
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/6/17 15:24
     */
    public static String currRequestBody() {
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
