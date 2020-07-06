package com.ahzak.utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/3/24 13:57
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class NetUtil extends cn.hutool.core.net.NetUtil {

    /**
     * 判断ip是否内网ip
     *
     * @param ip
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/3/24 13:58
     */
    public static boolean isInnerIP(String ip) {
        if ("localhost".equalsIgnoreCase(ip)) {
            return true;
        }
        // 后续再加上ipv6相关工具  目前就直接硬编码判断一下
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            return true;
        }
        return cn.hutool.core.net.NetUtil.isInnerIP(ip);
    }

    /**
     * 判断目标主机是否可以到达
     *
     * @param target  目标  可以是ip, 也可以是域名
     * @param timeout 超时时长  单位: ms
     * @return boolean 可以ping通返回true  否则返回false
     * @author Zhu Kaixiao
     * @date 2020/3/23 17:50
     */
    public static boolean ping(String target, int timeout) {
        boolean status = false;
        if (target != null) {
            try {
                status = InetAddress.getByName(target).isReachable(timeout);
            } catch (Exception e) {
            }
        }
        return status;
    }

    private static final Pattern IP_PATTERN = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");

    /**
     * 获取当前的外网ip地址
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/3/23 14:48
     */
    public static String getInternetIp() {
        try {
            //爬取的网站是百度搜索ip时排名第一的那个
            final int year = LocalDateTime.now().getYear();
            URL url = new URL("http://" + year + ".ip138.com");
            URLConnection urlconn = url.openConnection();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(urlconn.getInputStream()))) {
                String buf;
                StringBuilder sb = new StringBuilder();
                while ((buf = br.readLine()) != null) {
                    sb.append(buf);
                }
                final Matcher matcher = IP_PATTERN.matcher(sb);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        } catch (Exception e) {
            log.debug("获取外网ip失败", e);
        }
        // 可能当前断网, 或者网站无法访问, 或者网页信息格式改变
        return null;
    }


    /**
     * 根据ip地址获取定位信息
     *
     * @param ip
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/4/30 10:23
     */
    public static String getIpLocation(String ip) {
        try {
            String body = HttpRequest
                    .get(String.format("https://apis.map.qq.com/ws/location/v1/ip?ip=%s&key=OB4BZ-D4W3U-B7VVO-4PJWW-6TKDJ-WPB77", ip))
                    .header("Referer", "https://lbs.qq.com/service/webService/webServiceGuide/webServiceIp")
                    .execute()
                    .body();
            final JSONObject jo = JSONObject.parseObject(body);
            if (jo.getIntValue("status") == 0) {
                final JSONObject result = jo.getJSONObject("result");
                final JSONObject adInfo = result.getJSONObject("ad_info");
                final String location = Arrays.asList(
                        adInfo.getString("nation"),
                        adInfo.getString("province"),
                        adInfo.getString("city"),
                        adInfo.getString("district")
                ).stream()
                        .distinct()
                        .filter(StringUtils::isNotBlank)
                        .filter(s -> !"中国".equals(s))
                        .collect(Collectors.joining());
                return location;
            }
        } catch (Exception e) {
        }
        return "";
    }
}
