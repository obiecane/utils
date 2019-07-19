package com.ahzak.utils.jcspider.context;


public class CollectTaskContext {
    //默认线程数
    public final static Integer THREAD_DEFAULT_NUM = 1;
    //默认编码
    public final static String CHARSET_DEFAULT_VALUE = "UTF-8";
    //默认抓取间隔
    public final static Integer SLEEP_TIME_MILLISECOND_DEFAULT_NUM = 1500;
    //默认重试次数
    public final static Integer RETRY_TIMES_DEFAULT_NUM = 3;

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36";

    public static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 OPR/26.0.1656.60",
            "Opera/8.0 (Windows NT 5.1; U; en)",
            "Mozilla/5.0 (Windows NT 5.1; U; en; rv:1.8.1) Gecko/20061208 Firefox/2.0.0 Opera 9.50",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; en) Opera 9.50",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0",
            "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2 ",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.133 Safari/534.16",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
            "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SV1; QQDownload 732; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0) ",
    };


    //`type` smallint(1) NOT NULL COMMENT '采集类型（1模板采集2自定义采集）',
    public final static Integer FAST_TYPE = 1 ;
    public final static Integer CUSTOM_TYPE = 2 ;
    //`model_type` smallint(1) DEFAULT NULL COMMENT '模板类型（1中国政府网2百度搜索词3微博4公众号）',
    public final static Integer FAST_TYPE_WEB = 1;
    public final static Integer FAST_TYPE_BAI_DU = 2;
    public final static Integer FAST_TYPE_WEI_BO = 3;
    public final static Integer FAST_TYPE_WE_CHAT = 4;

    public final static String RESPONSE_ITEM_FIELD_TITLE = "title";//标题
    public final static String RESPONSE_ITEM_FIELD_URL = "outLink";//文章链接/百度链接
    public final static String RESPONSE_ITEM_FIELD_TIME = "releaseTime";//时间
    public final static String RESPONSE_ITEM_FIELD_COME_FROM = "contentSourceId";//来源
    public final static String RESPONSE_ITEM_FIELD_CONTENT = "contxt";//内容
    public final static String RESPONSE_ITEM_FIELD_DESCRIPTION = "description";//描述
    public final static String RESPONSE_ITEM_FIELD_PICTURE = "resource";//图片
    public final static String RESPONSE_ITEM_FIELD_FORWARD = "forward";//转发数
    public final static String RESPONSE_ITEM_FIELD_REPEAT = "repeat";//评论数
    public final static String RESPONSE_ITEM_FIELD_PRAISED = "praised";//点赞数


}
