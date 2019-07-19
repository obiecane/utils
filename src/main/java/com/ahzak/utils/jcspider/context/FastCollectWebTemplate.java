package com.ahzak.utils.jcspider.context;

public class FastCollectWebTemplate {
    //快速采集 - 中国政府网
    public final static String REQUEST_PATH_URL_WEB = "http://www.gov.cn/";
    public final static String[] REQUEST_PATH_URL_WEB_CONTENT_XPATH_ARRAY =
            {
            "/html/body/div[1]/div[2]/div[4]/div[1]/div[1]/ul/li/a/@href"
            };
    public final static String RESPONSE_PATH_URL_WEB_TITLE_XPATH =
            "//div[@class=\"article oneColumn pub_border\"]/h1/text()";
    public final static String RESPONSE_PATH_URL_WEB_TIME_XPATH =
            "//div[@class=\"article oneColumn pub_border\"]/div[1]/text()";
    public final static String RESPONSE_PATH_URL_WEB_COME_FROM_XPATH =
            "//div[@class=\"article oneColumn pub_border\"]/div[1]/span/text()";
    public final static String RESPONSE_PATH_URL_WEB_CONTENT_XPATH =
            "//*[@id=\"UCAP-CONTENT\"]";

}
