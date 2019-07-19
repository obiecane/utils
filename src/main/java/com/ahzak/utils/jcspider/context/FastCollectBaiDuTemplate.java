package com.ahzak.utils.jcspider.context;

import java.util.TreeMap;

public class FastCollectBaiDuTemplate {
    //快速采集 - 百度搜索引擎
    public final static String REQUEST_PATH_URL_BAI_DU = "https://www.baidu.com/s?&ie=utf-8&wd=";
    public final static String RESPONSE_PATH_URL_BAI_DU_TITLE_XPATH = "//*[@id=\""+CommonContext.XPATH_PARTITION_ELEMENT+"\"]/h3/a/html()";
    public final static TreeMap<String,String> RESPONSE_PATH_URL_BAI_DU_DESCRIPTION_XPATH = new TreeMap<>();
    static {
        RESPONSE_PATH_URL_BAI_DU_DESCRIPTION_XPATH.put("descriptionXpath1","//*[@id=\""+CommonContext.XPATH_PARTITION_ELEMENT+"\"]/div/div[2]/div[1]/html()");
        RESPONSE_PATH_URL_BAI_DU_DESCRIPTION_XPATH.put("descriptionXpath2","//*[@id=\""+CommonContext.XPATH_PARTITION_ELEMENT+"\"]/div[1]/html()");
    }

    public final static String RESPONSE_PATH_URL_BAI_DU_CONTENT_XPATH = "//*[@id=\""+CommonContext.XPATH_PARTITION_ELEMENT+"\"]/h3/a/@href";

}
