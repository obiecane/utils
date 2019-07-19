package com.ahzak.utils.jcspider.context;

import org.slf4j.Logger;

import java.util.Map;
import java.util.TreeMap;

public class FastCollectWeChatTemplate {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(FastCollectWeChatTemplate.class);

    //快速采集 - 微信公众号
    public final static String REQUEST_PATH_URL_WE_CHAT = "https://weixin.sogou.com/weixin?type=1&s_from=input&ie=utf8&_sug_=n&_sug_type_=&query=";

    public final static String WE_CHAT_WEB_DOMAIN = "weixin.sogou.com";

    public static String WE_CHAT_WEB_SNUID = "DADD70295B59D6B00D4D4B5A5CD6D4DC";
    public static String WE_CHAT_WEB_SUID = "86812B75293B900A000000005D2FD87C";
    public static String WE_CHAT_WEB_SUV = "009A4137752B81865D2FD87CA02CD175";
    public static String WE_CHAT_WEB_IPLOC = "CN3601";
    public final static String WE_CHAT_WEB_BROWER_V = "3";
    public final static String WE_CHAT_WEB_OS_V = "1";
    public final static String WE_CHAT_WEB_REWARDSN = "";
    public final static String WE_CHAT_WEB_WXTOKENKEY = "777";
    public final static String GET_NEW_COOKIE_URL = "https://news.sogou.com/news?ie=utf8&p=40230447&query=java";
    public final static String REQUEST_PATH_URL_WE_CHAT_CONTENT_XPATH = "//*[@id=\"sogou_vr_11002301_box_0\"]/div/div[2]/p[1]/a";

    public final static Map<String,String> RESPONSE_XPATH_MAP = new TreeMap<>();
    static {
        RESPONSE_XPATH_MAP.put("title","//*[@id=\"history\"]/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[2]/div[1]/div/h4/text()");
        RESPONSE_XPATH_MAP.put("url","//*[@id=\"history\"]/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[2]/div[1]/div/h4/@hrefs");
        RESPONSE_XPATH_MAP.put("time","//*[@id=\"history\"]/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[2]/div[1]/div/p[2]/text()");
        RESPONSE_XPATH_MAP.put("description","//*[@id=\"history\"]/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[2]/div[1]/div/p[1]/text()");
    }
    public final static String RESPONSE_PATH_URL_WE_CHAT_CONTENT_XPATH = "//*[@id=\"js_content\"]";

}
