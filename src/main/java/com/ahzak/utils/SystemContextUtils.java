//package com.ahzak.utils;
//
//import com.auth0.jwt.interfaces.Claim;
//import com.jeecms.jspgouexclusive.exception.GlobalException;
//import com.jeecms.jspgouexclusive.properties.JspgouProperties;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.security.Principal;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author Zhu Kaixiao
// * @version 1.0
// * @date 2019/9/5 9:28
// * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
// * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
// */
//@Slf4j
//@Component
//public class SystemContextUtils {
//
//    @Autowired
//    private JspgouProperties jspgouProperties;
//
//    @Autowired(required = false)
//    private ApiFinder apiFinder;
//
//    @Autowired
//    private ConfigurableEnvironment configurableEnvironment;
//
//    private static JspgouProperties JSPGOUPROPERTIES;
//    private static ApiFinder APIFINDER;
//    private static ConfigurableEnvironment CONFIGURABLEENVIRONMENT;
//    private static String APPLICATION_NAME;
//
//    @PostConstruct
//    private void init() {
//        CONFIGURABLEENVIRONMENT = configurableEnvironment;
//        JSPGOUPROPERTIES = jspgouProperties;
//        APIFINDER = apiFinder;
//        APPLICATION_NAME = CONFIGURABLEENVIRONMENT.getProperty("spring.application.name").toUpperCase();
//    }
//
//
//    /**
//     * 获取当前报错的错误码
//     * 其实就是当前接口的id
//     *
//     * @param
//     * @return java.lang.String
//     * @author Zhu Kaixiao
//     * @date 2019/11/25 17:45
//     */
//    public static String apiErrorCode() {
//        if (APIFINDER == null) {
//            log.info("未注入api查找器");
//            return "-400501";
//        }
//        try {
//            String contextPath = SpringMvcUtil.currentPath();
//            String method = SpringMvcUtil.getRequest().getMethod();
//            Long apiId = APIFINDER.getApiId(method, contextPath);
//            return Optional.ofNullable(apiId).map(id -> "-" + id).orElse("-400502");
//        } catch (Throwable throwable) {
//            log.error("获取接口错误码时出错", throwable);
//            return "-400503";
//        }
//    }
//
//
//    /**
//     * 当前登录的用户名
//     *
//     * @return java.lang.String
//     * @author Zhu Kaixiao
//     * @date 2019/9/5 9:37
//     **/
//    public static String currentUsername() {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = Optional.ofNullable(authentication).map(Principal::getName).orElse("system_internal");
//        return username;
//    }
//
//    /**
//     * 当前登录用户的角色名称
//     *
//     * @param
//     * @return java.util.List<java.lang.String>
//     * @author Zhu Kaixiao
//     * @date 2019/10/25 11:47
//     */
//    public static List<String> currentRoles() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        if (CollectionUtil.isEmpty(authorities)) {
//            return Collections.emptyList();
//        }
//        List<String> roleList = authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .map(str -> str.substring(5))
//                .collect(Collectors.toList());
//        return roleList;
//    }
//
//    /**
//     * 获取管理平台当前登录用户的id
//     *
//     * @return java.lang.Long
//     * @author Zhu Kaixiao
//     * @date 2019/11/22 9:29
//     */
//    public static Long currentUserId() {
//        String token = SpringMvcUtil.getToken();
//        Long id = JwtUtil.getId(token);
//        if (id == null) {
//            throw new GlobalException("登录失效");
//        }
//        return id;
//    }
//
//    public static IdUser currentUser() {
//        String token = SpringMvcUtil.getToken();
//        Map<String, Claim> claims = JwtUtil.getClaims(token);
//        Long id = claims.get("id").asLong();
//        String username = claims.get("user_name").asString();
//        IdUser idUser = new IdUser(id, username);
//        return idUser;
//    }
//
//
//    @Data
//    @AllArgsConstructor
//    public static class IdUser {
//        private Long id;
//        private String username;
//    }
//
//
//    /**
//     * 获取当前登录的用户类型
//     *
//     * @param
//     * @return com.jeecms.jspgouexclusive.utils.SystemContextUtils.UserType
//     * @author Zhu Kaixiao
//     * @date 2019/11/22 9:30
//     */
//    public static UserType currentUserType() {
//        String token = SpringMvcUtil.getToken();
//        return userType(token);
//    }
//
//    private static UserType userType(String token) {
//        String clientId = JwtUtil.getClientId(token);
//        if ("jspgou_admin".equals(clientId)) {
//            return UserType.ADMIN;
//        } else {
//            return UserType.MEMBER;
//        }
//
//    }
//
//
//    public enum UserType {
//        /**
//         * 管理平台用户
//         */
//        ADMIN,
//        /**
//         * 前台用户
//         */
//        MEMBER
//    }
//
//
//    /**
//     * 获取分布式服务的clientId
//     *
//     * @return java.lang.String
//     * @author Zhu Kaixiao
//     * @date 2019/11/14 16:06
//     */
//    public static String getClientId() {
//        return JSPGOUPROPERTIES.getClientId() == null
//                ? getApplicationName() + "_1"
//                : JSPGOUPROPERTIES.getClientId();
//    }
//
//    public static String getApplicationName() {
//        return APPLICATION_NAME;
//    }
//
//}
