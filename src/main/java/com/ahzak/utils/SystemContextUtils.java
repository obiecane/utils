package com.ahzak.utils;

import com.ahzak.utils.constants.Constant;
import com.ahzak.utils.exception.GlobalException;
import com.ahzak.utils.spring.SpringMvcUtil;
import com.auth0.jwt.interfaces.Claim;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/9/5 9:28
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@Component
public class SystemContextUtils {


    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    private static ConfigurableEnvironment CONFIGURABLEENVIRONMENT;
    private static String APPLICATION_NAME;

    @PostConstruct
    private void init() {
        CONFIGURABLEENVIRONMENT = configurableEnvironment;
        APPLICATION_NAME = CONFIGURABLEENVIRONMENT.getProperty("spring.application.name").toUpperCase();
    }


    /**
     * 获取当前报错的错误码
     * 其实就是当前接口的id
     *
     * @param
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/11/25 17:45
     */
    public static String apiErrorCode() {
//        if (APIFINDER == null) {
//            log.info("未注入api查找器");
//            return "-400501";
//        }
//        try {
//            String currMapping = SpringMvcUtil.currMappingPath();
//            String method = SpringMvcUtil.getRequest().getMethod();
//            Long apiId = APIFINDER.getApiId(method, currMapping);
//            return Optional.ofNullable(apiId).map(id -> "-" + id).orElse("-400502");
//        } catch (Throwable throwable) {
//            log.error("获取接口错误码时出错", throwable);
//            return "-400503";
//        }
        return "";
    }


    /**
     * 当前登录的用户名
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/9/5 9:37
     **/
    public static String currentUsername() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = Optional.ofNullable(authentication).map(Principal::getName).orElse(Constant.SYSTEM_USER);
        return username;
    }

    /**
     * 当前登录用户的角色名称
     *
     * @param
     * @return java.util.List<java.lang.String>
     * @author Zhu Kaixiao
     * @date 2019/10/25 11:47
     */
    public static List<String> currentRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (CollectionUtil.isEmpty(authorities)) {
            return Collections.emptyList();
        }
        List<String> roleList = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(str -> str.substring(5))
                .collect(Collectors.toList());
        return roleList;
    }

    /**
     * 获取当前登录用户的id
     * 若当前没有用户登录, 将抛出异常
     * @return java.lang.Long
     * @author Zhu Kaixiao
     * @date 2019/11/22 9:29
     */
    public static long currentUserId() {
        String token = SpringMvcUtil.currToken();
        Long id = JwtUtil.getId(token);
        if (id == null) {
            throw new GlobalException("登录失效");
        }
        return id;
    }

    /**
     * 返回当前登录的用户id
     * 若当前没有用户登录 返回-1
     *
     * @param
     * @return long
     * @author Zhu Kaixiao
     * @date 2020/7/4 16:28
     */
    public static long currentUserIdNoException() {
        String token = SpringMvcUtil.currToken();
        Long id = JwtUtil.getId(token);
        if (id == null) {
            return -1;
        }
        return id;
    }

    public static IdUser currentUser() {
        String token = SpringMvcUtil.currToken();
        Map<String, Claim> claims = JwtUtil.getClaims(token);
        Long id = claims.get("id").asLong();
        String username = claims.get("user_name").asString();
        IdUser idUser = new IdUser(id, username);
        return idUser;
    }


    @Data
    @AllArgsConstructor
    public static class IdUser {
        private Long id;
        private String username;
    }


    /**
     * 获取当前登录的用户类型
     *
     * @param
     * @return com.jeecms.jspgouexclusive.utils.SystemContextUtils.UserType
     * @author Zhu Kaixiao
     * @date 2019/11/22 9:30
     */
    public static UserType currentUserType() {
        String token = SpringMvcUtil.currToken();
        return userType(token);
    }

    private static UserType userType(String token) {
        String clientId = JwtUtil.getClientId(token);
        if ("jspgou_admin".equals(clientId)) {
            return UserType.ADMIN;
        } else {
            return UserType.MEMBER;
        }

    }


    public enum UserType {
        /**
         * 管理平台用户
         */
        ADMIN,
        /**
         * 前台用户
         */
        MEMBER
    }


    /**
     * 获取分布式服务的clientId
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/11/14 16:06
     */
    public static String getClientId() {
        // todo 修改分布式client id 获取方式
        return getApplicationName() + "_1";
    }

    public static String getApplicationName() {
        return APPLICATION_NAME;
    }

    public static boolean hasLogin() {
        return StringUtils.isNotBlank(SpringMvcUtil.currToken());
    }

}
