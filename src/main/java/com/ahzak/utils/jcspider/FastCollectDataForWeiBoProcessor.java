package com.ahzak.utils.jcspider;

import com.ahzak.utils.jcspider.context.CollectTaskContext;
import com.ahzak.utils.jcspider.context.CommonContext;
import com.ahzak.utils.jcspider.context.FastCollectWeiBoTemplate;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Site;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
public class FastCollectDataForWeiBoProcessor extends JcPageProcessor {
    @Autowired
    private JcCollectContentRepository jcCollectContentRepository;

    @Autowired
    private UploadFileService uploadFileService;

    private static JcCollectContentRepository JCCOLLECTCONTENTREPOSITORY;
    private static UploadFileService UPLOADFILESERVICE;

    private FastCollectDataForWeiBoRequest fastCollectDataForWeiBoRequest;
    private FastCollectDataForWeiBoResponseXpath fastCollectDataForWeiBoResponseXpath;
    private HttpServletRequest request;
    private HttpServletResponse response;


    public FastCollectDataForWeiBoProcessor() {
        jcCollectContentRepository = JCCOLLECTCONTENTREPOSITORY;
        uploadFileService = UPLOADFILESERVICE;
    }


    @PostConstruct
    private void initAutowired() {
        JCCOLLECTCONTENTREPOSITORY = jcCollectContentRepository;
        UPLOADFILESERVICE = uploadFileService;
    }


    public FastCollectDataForWeiBoProcessor setFastCollectDataForWeiBoRequest(FastCollectDataForWeiBoRequest fastCollectDataForWeiBoRequest){
        this.fastCollectDataForWeiBoRequest = fastCollectDataForWeiBoRequest;
        return this;
    }
    public FastCollectDataForWeiBoProcessor setFastCollectDataForWeiBoResponseXpath(FastCollectDataForWeiBoResponseXpath fastCollectDataForWeiBoResponseXpath){
        this.fastCollectDataForWeiBoResponseXpath = fastCollectDataForWeiBoResponseXpath;
        return this;
    }

    public FastCollectDataForWeiBoProcessor setHttpServletRequest(HttpServletRequest request){
        this.request = request;
        return this;
    }

    public FastCollectDataForWeiBoProcessor setHttpServletResponse(HttpServletResponse response){
        this.response = response;
        return this;
    }
    // 抓取网站的相关配置。
    private Site site = Site.me()
            .addCookie("Cookie",null == FastCollectWeiBoTemplate.WEI_BO_COOKIE_STR ? WeiBoUtil.createWeiBoCookie() : FastCollectWeiBoTemplate.WEI_BO_COOKIE_STR)
            .setUserAgent(CollectTaskContext.USER_AGENT)//UserAgent
            .setCharset(CollectTaskContext.CHARSET_DEFAULT_VALUE)//编码
            .setSleepTime(CollectTaskContext.SLEEP_TIME_MILLISECOND_DEFAULT_NUM)//抓取间隔
            .setRetryTimes(CollectTaskContext.RETRY_TIMES_DEFAULT_NUM);//重试次数
    @Override
    public void doProcess(JcPage page) {
        if (page.getUrl().toString().equals(fastCollectDataForWeiBoRequest.getUrlPath())) {
            TreeMap<String,TreeMap<String,Object>> mapMap = new TreeMap<>();
            for(int i = 0 ; i < 10 ; i ++ ){
                int index = 2*i + 7;
                TreeMap<String,Object> map = new TreeMap<>();
                map.put("index",i + 1);
                map.put("title",fastCollectDataForWeiBoResponseXpath.getTitleXpathMap());
                Map<String,String> timeXpathMap = new TreeMap<>();
                for (Map.Entry<String,String> entry : fastCollectDataForWeiBoResponseXpath.getTimeXpathMap().entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index);
                    timeXpathMap.put(key,value);
                }
                map.put("time",timeXpathMap);
                Map<String,String> contentXpathMap = new TreeMap<>();
                for (Map.Entry<String,String> entry : fastCollectDataForWeiBoResponseXpath.getContentXpathMap().entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index);
                    contentXpathMap.put(key,value);
                }
                map.put("content",contentXpathMap);
                Map<String,String> comeFromXpathMap = new TreeMap<>();
                for (Map.Entry<String,String> entry : fastCollectDataForWeiBoResponseXpath.getComeFromXpathMap().entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index);
                    comeFromXpathMap.put(key,value);
                }
                map.put("comeFrom",comeFromXpathMap);
                Map<String,String> forwardNumXpathMap = new TreeMap<>();
                for (Map.Entry<String,String> entry : fastCollectDataForWeiBoResponseXpath.getForwardNumXpathMap().entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index);
                    forwardNumXpathMap.put(key,value);
                }
                map.put("forwardNum",forwardNumXpathMap);
                Map<String,String> commentsNumXpathMap = new TreeMap<>();
                for (Map.Entry<String,String> entry : fastCollectDataForWeiBoResponseXpath.getCommentsNumXpathMap().entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index);
                    commentsNumXpathMap.put(key,value);
                }
                map.put("commentsNum",commentsNumXpathMap);
                Map<String,String> likeNumXpathMap = new TreeMap<>();
                for (Map.Entry<String,String> entry : fastCollectDataForWeiBoResponseXpath.getLikeNumXpathMap().entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index);
                    likeNumXpathMap.put(key,value);
                }
                map.put("likeNum",likeNumXpathMap);
                map.put("picture",fastCollectDataForWeiBoRequest.getPictureUrlXPath().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index));
                mapMap.put(map.get("index").toString(),map);
            }

            for(Map.Entry<String,TreeMap<String,Object>> entry : mapMap.entrySet()){
                JcCollectContent jcCollectContent = new JcCollectContent();
                FastCollectDataForWeiBoResponse fastCollectDataForWeiBoResponse = new FastCollectDataForWeiBoResponse();
                boolean haveDiv2 = false;//微博图文标志
                Map<String,String> timeXpathMap = (Map<String,String>) entry.getValue().get("time");
                for (Map.Entry<String,String> timeXpathEntry : timeXpathMap.entrySet()){
                    String time = page.getHtml().xpath(timeXpathEntry.getValue()).get();
                    if(null != time){
                        fastCollectDataForWeiBoResponse.setReleaseTime(time);
                        if(timeXpathEntry.getValue().contains("div[2]"))haveDiv2 = true;
                    }
                }
                Map<String,String> titleXpathMap = (Map<String,String>) entry.getValue().get("title");
                String title = null;//用户7239742138 男/其他
                for (Map.Entry<String,String> titleXpathEntry : titleXpathMap.entrySet()){
                    String titleResult = page.getHtml().xpath(titleXpathEntry.getValue()).get();
                    if(null != titleResult) {
                        title = titleResult;
                    }
                    break;
                }
                fastCollectDataForWeiBoResponse.setTitle(null != fastCollectDataForWeiBoResponseXpath.getTitleXpathMap()
                        ? title : null);
                if (null != title && null != fastCollectDataForWeiBoResponse.getReleaseTime()) {
                    //*[@id="M_HDJjllDsR"]/div[1]/span
                    Map<String,String> contentXpathMap = (Map<String,String>) entry.getValue().get("content");
                    for (Map.Entry<String,String> contentXpathEntry : contentXpathMap.entrySet()){
                        if((haveDiv2 && contentXpathEntry.getValue().contains("div[1]"))|| (!haveDiv2 &&!contentXpathEntry.getValue().contains("div[1]"))){
                            String content = page.getHtml().xpath(contentXpathEntry.getValue()).get();
                            if(null != content){
                                fastCollectDataForWeiBoResponse.setContxt(content);
                            }
                            break;
                        }

                    }
                    Map<String,String> comeFromXpathMap = (Map<String,String>) entry.getValue().get("comeFrom");
                    for (Map.Entry<String,String> comeFromXpathEntry : comeFromXpathMap.entrySet()){
                        String comeFrom = page.getHtml().xpath(comeFromXpathEntry.getValue()).get();
                        if(null != comeFrom){
                            fastCollectDataForWeiBoResponse.setContentSourceId(comeFrom);
                        }
                    }
                    Map<String,String> forwardNumXpathMap = (Map<String,String>) entry.getValue().get("forwardNum");
                    for (Map.Entry<String,String> forwardNumXpathEntry : forwardNumXpathMap.entrySet()){
                        //*[@id="M_HDJOh8otm"]/div[2]/a[4]
                        if((haveDiv2 && forwardNumXpathEntry.getValue().contains("div[2]"))|| (!haveDiv2 &&!forwardNumXpathEntry.getValue().contains("div[2]"))){
                            String forwardNum = page.getHtml().xpath(forwardNumXpathEntry.getValue()).get();
                            if(null != forwardNum){
                                fastCollectDataForWeiBoResponse.setForward(forwardNum);
                            }
                            break;
                        }

                    }
                    Map<String,String> commentsNumXpathMap = (Map<String,String>) entry.getValue().get("commentsNum");
                    for (Map.Entry<String,String> commentsNumXpathEntry : commentsNumXpathMap.entrySet()){
                        //*[@id="M_HDJOh8otm"]/div[2]/a[5]
                        if((haveDiv2 && commentsNumXpathEntry.getValue().contains("div[2]"))|| (!haveDiv2 &&!commentsNumXpathEntry.getValue().contains("div[2]"))){
                            String commentsNum = page.getHtml().xpath(commentsNumXpathEntry.getValue()).get();
                            if(null != commentsNum){
                                fastCollectDataForWeiBoResponse.setRepeat(commentsNum);
                            }
                            break;
                        }

                    }
                    Map<String,String> likeNumXpathMap = (Map<String,String>) entry.getValue().get("likeNum");
                    for (Map.Entry<String,String> likeNumXpathEntry : likeNumXpathMap.entrySet()){
                        //*[@id="M_HDJOh8otm"]/div[2]/a[3]
                        if((haveDiv2 && likeNumXpathEntry.getValue().contains("div[2]"))|| (!haveDiv2 &&!likeNumXpathEntry.getValue().contains("div[2]"))){
                            String likeNum = page.getHtml().xpath(likeNumXpathEntry.getValue()).get();
                            if(null != likeNum){
                                fastCollectDataForWeiBoResponse.setPraised(likeNum);
                            }
                            break;
                        }

                    }
                    List<String> pictureUrlList = new ArrayList<>();
                    if(null != fastCollectDataForWeiBoResponseXpath.getPictureXpath() && haveDiv2){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        String pictureUrlXPath =  entry.getValue().get("picture").toString();
                        String basePath = page.getUrl().get();
                        basePath = basePath.substring(0,basePath.lastIndexOf("/"));
                        String strSrc = page.getHtml().xpath(pictureUrlXPath).get();
                        if(null != strSrc){
                            String url;
                            if (strSrc.startsWith("http://") || strSrc.startsWith("https://")) {
                                url = strSrc;
                            } else {
                                if (strSrc.startsWith("/")) {
                                    url = basePath + strSrc;
                                } else {
                                    url = basePath + "/" + strSrc;
                                }
                            }
                            final WebClient webClient = new WebClient(BrowserVersion.CHROME);
                            webClient.getOptions().setJavaScriptEnabled(true);
                            webClient.getOptions().setCssEnabled(false);
                            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                            webClient.getOptions().setThrowExceptionOnScriptError(false);
                            for(Cookie cookie : FastCollectWeiBoTemplate.WEI_BO_COOKIE_OBJ){
                                webClient.getCookieManager().addCookie(cookie);
                            }
                            try {
                                HtmlPage pageHtmlUnit = webClient.getPage(url);
                                for(int i = 0 ; i < 9 ; i ++ ) {
                                    int index = i + 2;
                                    String pictureXpathForOneOfMany = fastCollectDataForWeiBoResponseXpath.getPictureXpath().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,"" + index);
                                    DomAttr domText = pageHtmlUnit.getFirstByXPath(pictureXpathForOneOfMany);
                                    if(null != domText){
                                        String basePathHtmlUnit = pageHtmlUnit.getUrl().toString();
                                        basePathHtmlUnit = basePathHtmlUnit.substring(0,(basePathHtmlUnit.indexOf("/")+2) + basePathHtmlUnit.substring(basePathHtmlUnit.indexOf("/")+2).indexOf("/"));
                                        String pictureUrl = "";
                                        if (domText.getValue().startsWith("http://") || domText.getValue().startsWith("https://")) {
                                            pictureUrl = strSrc;
                                        } else {
                                            if (domText.getValue().startsWith("/")) {
                                                pictureUrl = basePathHtmlUnit + domText.getValue();
                                            } else {
                                                pictureUrl = basePathHtmlUnit + "/" + domText.getValue();
                                            }
                                        }
                                        pictureUrlList.add( UrlUtil.getRealUrl(pictureUrl,FastCollectWeiBoTemplate.WEI_BO_COOKIE_STR));
                                    }else break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }finally {
                                if(null != webClient)
                                    webClient.close();
                            }
                            List<String> uploadResultFileUrlList = new ArrayList<>();
                            for(String pictureUrl : pictureUrlList){
                                String uploadResultFileUrl = uploadFileService.uploadFileByUrl(pictureUrl.replaceAll("amp;",""),loginUser,request,response);
                                uploadResultFileUrlList.add(uploadResultFileUrl);
                            }
                            if(!uploadResultFileUrlList.isEmpty())fastCollectDataForWeiBoResponse.setResource(uploadResultFileUrlList);
                        }

                    }
                    jcCollectContent.setTaskId(collectTaskId);
                    JSONObject fastCollectDataForWebResponseJson = JSONObject.fromObject(fastCollectDataForWeiBoResponse);
                    jcCollectContent.setContentValue(fastCollectDataForWebResponseJson.toString());
                    jcCollectContent.setCreateTime(new Date());
                    jcCollectContent.setCreateUser(loginUser);
                    jcCollectContent.setDeletedFlag(CommonContext.DATA_UN_DELETED);
                    jcCollectContentRepository.save(jcCollectContent);
                }
            }
        }
    }

    @Override
    public Site getSite() {
        return this.site;
    }
}
