package com.ahzak.utils.jcspider;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.jeecms.collect.data.context.CollectTaskContext;
import com.jeecms.collect.data.context.CommonContext;
import com.jeecms.collect.data.context.FastCollectWeChatTemplate;
import com.jeecms.collect.data.model.JcCollectContent;
import com.jeecms.collect.data.repository.JcCollectContentRepository;
import com.jeecms.collect.data.request.FastCollectDataForWeChatRequest;
import com.jeecms.collect.data.response.FastCollectDataForWeChatResponse;
import com.jeecms.collect.data.response.FastCollectDataForWeChatResponseXpath;
import com.jeecms.common.base.websocket.WebSocketContext;
import com.jeecms.common.util.MyDateUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class FastCollectDataForWeChatProcessor extends JcPageProcessor {

    @Autowired
    private JcCollectContentRepository jcCollectContentRepository;

    private static JcCollectContentRepository JCCOLLECTCONTENTREPOSITORY;

    @PostConstruct
    private void autowiredInit() {
        JCCOLLECTCONTENTREPOSITORY = jcCollectContentRepository;
    }

    public FastCollectDataForWeChatProcessor() {
        jcCollectContentRepository = JCCOLLECTCONTENTREPOSITORY;
    }

    private FastCollectDataForWeChatRequest fastCollectDataForWeChatRequest;
    private FastCollectDataForWeChatResponseXpath fastCollectDataForWeChatResponseXpath;


    public FastCollectDataForWeChatProcessor setFastCollectDataForWeChatRequest(FastCollectDataForWeChatRequest fastCollectDataForWeChatRequest) {
        this.fastCollectDataForWeChatRequest = fastCollectDataForWeChatRequest;
        return this;
    }

    public FastCollectDataForWeChatProcessor setFastCollectDataForWeChatResponseXpath(FastCollectDataForWeChatResponseXpath fastCollectDataForWeChatResponseXpath) {
        this.fastCollectDataForWeChatResponseXpath = fastCollectDataForWeChatResponseXpath;
        return this;
    }

    /**
     * 处理搜狗搜索页面, 如果处理成功, 返回下一级页面的url, 否则返回null
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/7/17 17:25
     **/
    private String processSoGou(HtmlPage pageHtmlUnit) throws IOException {
        HtmlPage pageHtmlUnitLast;
        boolean antiSpider = isSogouAntiSpider(pageHtmlUnit);
        if (pageHtmlUnit != null && !antiSpider) {
            HtmlAnchor aAndHref = pageHtmlUnit.getFirstByXPath(fastCollectDataForWeChatRequest.getUrlPathContentXpath());
            pageHtmlUnitLast = aAndHref.click();
            if (isSogouAntiSpider(pageHtmlUnitLast)) {
                webLogsService.output(loginUser + WebSocketContext.MESSAGE_PARTITION_ELEMENT
                        + MyDateUtils.formatDate(new Date(), "MM/dd hh:mm:ss") + ": 触发了搜狗反爬虫-->  " + pageHtmlUnit.getUrl().toString());
                return null;
            } else {
                return pageHtmlUnitLast.getUrl().toString();
            }
        } else {
            //TODO
            return null;
        }
    }

    @Override
    public void doProcess(JcPage page) {
        if (page.getUrl().toString().equals(fastCollectDataForWeChatRequest.getUrlPath())) {
            try {
                String nextUrl = processSoGou(page.getHtmlPage());
                if(null == nextUrl)//TODO
                    return;
                page.addTargetRequest(nextUrl);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (page.getUrl().toString().contains("&jcCollectContentId=")) {
            String jcCollectContentId = page.getUrl().toString().substring(page.getUrl().toString().indexOf("&jcCollectContentId="));
            page.putField("jcCollectContentId", jcCollectContentId);
            page.putField("content", page.getHtml().xpath(fastCollectDataForWeChatResponseXpath.getUrlPathContentXpath()));
            page.putField("pathUrl", page.getUrl().get());
            page.putField("loginUser", loginUser);
        } else {
            List<String> detailUrls = new ArrayList<>();
            TreeMap<String,Map<String,String>> mapMap = new TreeMap<>();
            for(int i = 0 ; i < 10 ; i++ ){
                Map<String,String> map = new TreeMap<>();
                map.putAll(FastCollectWeChatTemplate.RESPONSE_XPATH_MAP);
                for(Map.Entry<String,String> entry : map.entrySet()){
                    entry.setValue(entry.getValue().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+(i+1)));
                }
                mapMap.put(map.get("url"), map);
            }
            for (Map.Entry<String, Map<String, String>> entry : mapMap.entrySet()) {
                JcCollectContent jcCollectContent = new JcCollectContent();
                String basePath = page.getUrl().get();
                basePath = basePath.substring(0,basePath.lastIndexOf("/"));
                if (null != page.getHtml().xpath(entry.getKey())) {
                    WebClient webClient = createWebClientForSoGou();
                    HtmlPage pageHtmlUnit = null;
                    String strSrc = "" ;
                    String url;
                    try{
                        Thread.sleep(CollectTaskContext.SLEEP_TIME_MILLISECOND_DEFAULT_NUM);
                        pageHtmlUnit = webClient.getPage(page.getUrl().toString());

                        DomAttr domAttr = pageHtmlUnit.getFirstByXPath(entry.getKey());
                        strSrc = domAttr.getValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (strSrc.startsWith("http://") || strSrc.startsWith("https://")) {
                        url = strSrc;
                    } else {
                        if (strSrc.startsWith("/")) {
                            url = basePath + strSrc;
                        } else {
                            url = basePath + "/" + strSrc;
                        }
                    }
                    FastCollectDataForWeChatResponse fastCollectDataForWeChatResponse = new FastCollectDataForWeChatResponse();
                    fastCollectDataForWeChatResponse.setOutLink(fastCollectDataForWeChatResponseXpath.getUrlXpath() ? url : null);
                    if (fastCollectDataForWeChatResponseXpath.getTitleXpath()) {
                        DomText domText = pageHtmlUnit.getFirstByXPath(entry.getValue().get("title"));
                        fastCollectDataForWeChatResponse.setTitle(domText == null ? "" : domText.getWholeText());
                    }
                    if (fastCollectDataForWeChatResponseXpath.getTimeXpath()) {
                        DomText domText = pageHtmlUnit.getFirstByXPath(entry.getValue().get("time"));
                        fastCollectDataForWeChatResponse.setReleaseTime(null == domText ? "" : domText.getTextContent());
                    }
                    if (fastCollectDataForWeChatResponseXpath.getDescriptionXpath()) {
                        DomText domText = pageHtmlUnit.getFirstByXPath(entry.getValue().get("description"));
                        fastCollectDataForWeChatResponse.setDescription(null == domText ? "" : domText.getTextContent());
                    }
                    jcCollectContent.setTaskId(collectTaskId);
                    JSONObject fastCollectDataForWebResponseJson = JSONObject.fromObject(fastCollectDataForWeChatResponse);
                    jcCollectContent.setContentValue(fastCollectDataForWebResponseJson.toString());
                    jcCollectContent.setCreateTime(new Date());
                    jcCollectContent.setCreateUser(loginUser);
                    jcCollectContent.setDeletedFlag(CommonContext.DATA_UN_DELETED);
                    jcCollectContentRepository.save(jcCollectContent);

                    if (fastCollectDataForWeChatResponseXpath.getContentXpath()) {
                        url += "&jcCollectContentId=" + jcCollectContent.getId();
                        detailUrls.add(url);
                    }
                } else {
                    break;
                }
            }
            page.addTargetRequests(detailUrls);
        }
    }
    private WebClient createWebClientForSoGou() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.addRequestHeader("", "");
        webClient.getCookieManager().addCookie(new Cookie(FastCollectWeChatTemplate.WE_CHAT_WEB_DOMAIN, "SNUID", FastCollectWeChatTemplate.WE_CHAT_WEB_SNUID));
        webClient.getCookieManager().addCookie(new Cookie(FastCollectWeChatTemplate.WE_CHAT_WEB_DOMAIN, "SUID", FastCollectWeChatTemplate.WE_CHAT_WEB_SUID));
        webClient.getCookieManager().addCookie(new Cookie(FastCollectWeChatTemplate.WE_CHAT_WEB_DOMAIN, "SUV", FastCollectWeChatTemplate.WE_CHAT_WEB_SUV));
        webClient.getCookieManager().addCookie(new Cookie(FastCollectWeChatTemplate.WE_CHAT_WEB_DOMAIN, "IPLOC", FastCollectWeChatTemplate.WE_CHAT_WEB_IPLOC));
        webClient.getCookieManager().addCookie(new Cookie(FastCollectWeChatTemplate.WE_CHAT_WEB_DOMAIN, "browerV", FastCollectWeChatTemplate.WE_CHAT_WEB_BROWER_V));
        webClient.getCookieManager().addCookie(new Cookie(FastCollectWeChatTemplate.WE_CHAT_WEB_DOMAIN, "osV", FastCollectWeChatTemplate.WE_CHAT_WEB_OS_V));
        webClient.getCookieManager().addCookie(new Cookie(FastCollectWeChatTemplate.WE_CHAT_WEB_DOMAIN, "rewardsn", FastCollectWeChatTemplate.WE_CHAT_WEB_REWARDSN));
        webClient.getCookieManager().addCookie(new Cookie(FastCollectWeChatTemplate.WE_CHAT_WEB_DOMAIN, "wxtokenkey", FastCollectWeChatTemplate.WE_CHAT_WEB_WXTOKENKEY));

        return webClient;
    }

    /**
     * 判断是否触发了搜狗反爬, 如果是则返回true, 否则返回false
     * @param page 页面
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/7/17 13:37
     **/
    private boolean isSogouAntiSpider(HtmlPage page) {
        String urlStr = page.getUrl().toString();
        boolean isTri = urlStr.startsWith("http://weixin.sogou.com/antispider")
                || urlStr.startsWith("https://weixin.sogou.com/antispider");
        return isTri;
    }
}
