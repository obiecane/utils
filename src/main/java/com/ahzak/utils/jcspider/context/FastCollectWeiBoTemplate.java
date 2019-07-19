package com.ahzak.utils.jcspider.context;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.util.Set;
import java.util.TreeMap;

public class FastCollectWeiBoTemplate {
    //快速采集 -  微博
    public final static String REQUEST_PATH_URL_WEI_BO = "https://weibo.cn/u/";
    //标题/用户
    public final static TreeMap<String,String> RESPONSE_PATH_URL_WEI_BO_TITLE_XPATH = new TreeMap<>();
    static {
        RESPONSE_PATH_URL_WEI_BO_TITLE_XPATH.put("titleXpath1","/html/body/div[4]/table/tbody/tr/td[2]/div/span[1]/text()");
        //RESPONSE_PATH_URL_WEI_BO_TITLE_XPATH.put("titleXpath2","/html/body/div[4]/table/tbody/tr/td[2]/div/span[1]/text()[1]");
    }

    //时间
    public final static TreeMap<String,String> RESPONSE_PATH_URL_WEI_BO_TIME_AND_COME_FROM_XPATH = new TreeMap<>();//07月17日 15:55 来自专业版微博
    static {
        RESPONSE_PATH_URL_WEI_BO_TIME_AND_COME_FROM_XPATH.put("timeXpath1","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div/span[2]/text()");//纯文字内容div 7 、9、11、13、15、17、19、21、23、25
        RESPONSE_PATH_URL_WEI_BO_TIME_AND_COME_FROM_XPATH.put("timeXpath2","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[2]/span/text()");//图文div7 、9、11、13、15、17、19、21、23、25
    }
    //内容
    public final static TreeMap<String,String> RESPONSE_PATH_URL_WEI_BO_CONTENT_XPATH  = new TreeMap<>();
    static {
        RESPONSE_PATH_URL_WEI_BO_CONTENT_XPATH.put("contentXpath1","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div/span[1]/text()");//纯文字内容div 7 、9、11、13、15、17、19、21、23、25
        RESPONSE_PATH_URL_WEI_BO_CONTENT_XPATH.put("contentXpath2","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[1]/span/text()");//图文div7 、9、11、13、15、17、19、21、23、25
    }
    //图片请求跳转链接至图片列表页
    public final static String  REQUEST_PATH_URL_WEI_BO_PICTURE_XPATH = "/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[1]/a/@href";

    //图片列表
    public final static String  RESPONSE_PATH_URL_WEI_BO_PICTURE_XPATH = "/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/a[2]/@href";//div 2、3、4、5、6、7、8、9、10

    //转发数
    public final static TreeMap<String,String> RESPONSE_PATH_URL_WEI_BO_FORWARD_XPATH  = new TreeMap<>();
    static {
        //*[@id="M_HDTeY4rQi"]/div/a[2]
        RESPONSE_PATH_URL_WEI_BO_FORWARD_XPATH.put("forwardXpath1","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div/a[2]/text()");//纯文字内容div 7 、9、11、13、15、17、19、21、23、25
        //*[@id="M_HDJOh8otm"]/div[2]/a[4]
        //*[@id="M_HDJOh8otm"]/div[2]/a[4]
        RESPONSE_PATH_URL_WEI_BO_FORWARD_XPATH.put("forwardXpath2","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[2]/a[4]/text()");//图文div7 、9、11、13、15、17、19、21、23、25
    }
    //评论
    public final static TreeMap<String,String> RESPONSE_PATH_URL_WEI_BO_REPEAT_XPATH  = new TreeMap<>();
    static {
        //*[@id="M_HDTeY4rQi"]/div/a[3]
        RESPONSE_PATH_URL_WEI_BO_REPEAT_XPATH.put("repeatXpath1","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div/a[3]/text()");//纯文字内容div 7 、9、11、13、15、17、19、21、23、25
        //*[@id="M_HDJOh8otm"]/div[2]/a[5]
        RESPONSE_PATH_URL_WEI_BO_REPEAT_XPATH.put("repeatXpath2","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[2]/a[5]/text()");//图文div7 、9、11、13、15、17、19、21、23、25
    }
    //点赞数
    public final static TreeMap<String,String> RESPONSE_PATH_URL_WEI_BO_PRAISED_XPATH  = new TreeMap<>();
    static {
        //*[@id="M_HDTeY4rQi"]/div/a[1]
        RESPONSE_PATH_URL_WEI_BO_PRAISED_XPATH.put("praisedXpath1","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div/a[1]/text()");//纯文字内容div 7 、9、11、13、15、17、19、21、23、25
        //*[@id="M_HDJOh8otm"]/div[2]/a[3]
        RESPONSE_PATH_URL_WEI_BO_PRAISED_XPATH.put("praisedXpath2","/html/body/div["+CommonContext.XPATH_PARTITION_ELEMENT+"]/div[2]/a[3]/text()");//图文div7 、9、11、13、15、17、19、21、23、25

    }

    //微博账号（用户名、密码）
    public final static String WEI_BO_USERNAME = "15902140751";
    public final static String WEI_BO_PASSWORD = "811811Zhu";

    public static String WEI_BO_COOKIE_STR ;
    public static Set<Cookie> WEI_BO_COOKIE_OBJ ;
     static {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getCookieManager().clearCookies();
        try {
            HtmlPage page = webClient.getPage("https://passport.weibo.cn/signin/login");
            HtmlTextInput inputLoginName = page.getHtmlElementById("loginName");
            inputLoginName.setText(WEI_BO_USERNAME);
            Thread.sleep(1000);
            HtmlPasswordInput inputLoginPassword = page.getHtmlElementById("loginPassword");
            inputLoginPassword.setText(WEI_BO_PASSWORD);
            HtmlAnchor submitButton = page.getHtmlElementById("loginAction");
            Thread.sleep(1000);
            submitButton.click();
            Set<Cookie> cookieSet = webClient.getCookieManager().getCookies();
            StringBuilder sb = new StringBuilder();
            for (Cookie cookie : cookieSet) {
                sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
            }
            WEI_BO_COOKIE_STR = sb.toString();
            WEI_BO_COOKIE_OBJ = cookieSet;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null != webClient)
                webClient.close();
        }
    }

}
