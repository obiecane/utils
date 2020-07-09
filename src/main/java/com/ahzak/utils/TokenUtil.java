package com.ahzak.utils;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.ahzak.utils.constants.Constant;
import com.ahzak.utils.spring.SpringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.ahzak.utils.constants.Constant.MODULE_USER;


/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/10/30 9:06
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class TokenUtil {


    private TokenUtil() {
    }

    private static Map<String, Object> system_token_map;
    private static volatile long expiration = 0;
    private static volatile long refreshTime = 0;

    private static volatile boolean isFirst = true;

    public static String getSystemToken() {

        if (needRefresh()) {
            refreshMap();
        }

        String access_token = system_token_map == null ? null : (String) system_token_map.get("access_token");
        return access_token;
    }

    public static String getTokenType() {
        if (needRefresh()) {
            refreshMap();
        }

        String type = system_token_map == null ? null : (String) system_token_map.get("token_type");
        return type;
    }


    private static synchronized void refreshMap() {
        if (!needRefresh()) {
            return;
        }
        log.debug("刷新内部转发token, 原refreshTime: {} 原expiration: {}", refreshTime, expiration);
        String ipAddr = null;
        Integer port = null;
        refreshTime = System.currentTimeMillis();
        try {
            // 通过注册中心获取授权服务器的地址
            Object eurekaClientConfigBean = SpringUtil.getBean("eurekaClientConfigBean");
            Method getServiceUrl = ReflectUtil.getMethodByName(eurekaClientConfigBean.getClass(), "getServiceUrl");
            Map<String, String> serviceUrl = ReflectUtil.invoke(eurekaClientConfigBean, getServiceUrl);
            String defaultZone = serviceUrl.get("defaultZone");

            HttpRequest appsGet = HttpUtil.createGet(defaultZone + "/apps");
            appsGet.header("Content-Type", "application/json");
            appsGet.header("Accept", "application/json");
            HttpResponse appsResponse = appsGet.execute();
            String appsBody = appsResponse.body();
            Map apps = JSONObject.parseObject(appsBody, HashMap.class);
            JSONObject applications = (JSONObject) apps.get("applications");
            JSONArray applicationArr = (JSONArray) applications.get("application");
            for (Object o : applicationArr) {
                JSONObject jo = (JSONObject) o;
                if (MODULE_USER.equals(jo.getString("name"))) {
                    JSONArray instanceArr = (JSONArray) jo.get("instance");
                    JSONObject instance = (JSONObject) instanceArr.get(0);
                    ipAddr = instance.getString("ipAddr");
                    JSONObject portObj = (JSONObject) instance.get("port");
                    port = portObj.getInteger("$");
                    break;
                }
            }

            if (ipAddr == null || port == null) {
                log.error("未发现[{}]服务", MODULE_USER);
            }


            HttpRequest get = HttpUtil.createGet(String.format("%s:%d/oauth/token?grant_type=client_credentials", ipAddr, port));
            get.basicAuth(Constant.SYSTEM_USER, Constant.SYSTEM_GARBLED);
            HttpResponse execute = get.execute();
            String body = execute.body();

            system_token_map = JSONObject.parseObject(body, HashMap.class);
            expiration = (long) system_token_map.get("expiration");
        } catch (Throwable throwable) {
            refreshTime = 0;
            expiration = 0;
            if (isFirst) {
                log.error("获取内部访问token失败");
            } else {
                log.error("获取内部访问token失败", throwable);
            }
        }

    }

    private static boolean needRefresh() {
        return System.currentTimeMillis() >= expiration;
    }


    public static void main(String[] args) {
        HttpRequest get = HttpUtil.createGet("127.0.0.1:8005/oauth/token?grant_type=client_credentials");
        get.basicAuth(Constant.SYSTEM_USER, Constant.SYSTEM_GARBLED);
        HttpResponse execute = get.execute();
        String body = execute.body();
        System.out.println(body);
    }

    public static String getTokenFromAuthorization(String authorization) {
        return StringUtils.substringAfter(authorization, " ").trim();
    }
}
