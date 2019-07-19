package com.ahzak.utils.jcspider;

import com.jeecms.collect.data.context.CommonContext;
import com.jeecms.collect.data.model.JcCollectContent;
import com.jeecms.collect.data.repository.JcCollectContentRepository;
import com.jeecms.collect.data.request.FastCollectDataForBaiDuRequest;
import com.jeecms.collect.data.response.FastCollectDataForBaiDuResponse;
import com.jeecms.collect.data.response.FastCollectDataForBaiDuResponseXpath;
import com.jeecms.collect.data.service.UploadFileService;
import com.jeecms.collect.data.util.DoHtmlUtil;
import com.jeecms.collect.data.util.StringUtil;
import com.jeecms.collect.data.util.UrlUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Component
public class FastCollectDataForBaiDuProcessor extends JcPageProcessor {
    @Autowired
    private JcCollectContentRepository jcCollectContentRepository;
    @Autowired
    private UploadFileService uploadFileService;

    private static JcCollectContentRepository JCCOLLECTCONTENTREPOSITORY;
    private static UploadFileService UPLOADFILESERVICE;

    private FastCollectDataForBaiDuRequest fastCollectDataForBaiDuRequest;
    private FastCollectDataForBaiDuResponseXpath fastCollectDataForBaiDuResponseXpath;
    private HttpServletRequest request;
    private HttpServletResponse response;


    public FastCollectDataForBaiDuProcessor() {
        jcCollectContentRepository = JCCOLLECTCONTENTREPOSITORY;
        uploadFileService = UPLOADFILESERVICE;
    }


    @PostConstruct
    private void initAutowired() {
        JCCOLLECTCONTENTREPOSITORY = jcCollectContentRepository;
        UPLOADFILESERVICE = uploadFileService;
    }


    public FastCollectDataForBaiDuProcessor setFastCollectDataForBaiDuRequest(FastCollectDataForBaiDuRequest fastCollectDataForBaiDuRequest){
        this.fastCollectDataForBaiDuRequest = fastCollectDataForBaiDuRequest;
        return this;
    }
    public FastCollectDataForBaiDuProcessor setFastCollectDataForBaiDuResponseXpath(FastCollectDataForBaiDuResponseXpath fastCollectDataForBaiDuResponseXpath){
        this.fastCollectDataForBaiDuResponseXpath = fastCollectDataForBaiDuResponseXpath;
        return this;
    }

    public FastCollectDataForBaiDuProcessor setHttpServletRequest(HttpServletRequest request){
        this.request = request;
        return this;
    }

    public FastCollectDataForBaiDuProcessor setHttpServletResponse(HttpServletResponse response){
        this.response = response;
        return this;
    }


    @Override
    public void doProcess(JcPage page) {
        if (page.getUrl().toString().equals(fastCollectDataForBaiDuRequest.getUrlPath())) {
            TreeMap<String,TreeMap<String,Object>> mapMap = new TreeMap<>();
            for(int i = 0 ; i < 10 ; i ++ ){
                int index = i + 1 ;
                TreeMap<String,Object> map = new TreeMap<>();
                map.put("title",fastCollectDataForBaiDuResponseXpath.getTitleXpath().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index));
                map.put("url",fastCollectDataForBaiDuResponseXpath.getUrlXpath().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index));
                Map<String,String> descriptionXpathMap = new TreeMap<>();
                for (Map.Entry<String,String> entry : fastCollectDataForBaiDuResponseXpath.getDescriptionXpathMap().entrySet()){
                    String key = entry.getKey();
                    String value = entry.getValue().replaceAll(CommonContext.XPATH_PARTITION_ELEMENT,""+index);
                    descriptionXpathMap.put(key,value);
                    checkPause();
                }
                map.put("description",descriptionXpathMap);
                mapMap.put(map.get("url").toString(),map);
            }
            for(Map.Entry<String,TreeMap<String,Object>> entry : mapMap.entrySet()){
                JcCollectContent jcCollectContent = new JcCollectContent();
                String basePath = page.getUrl().get();
                basePath = basePath.substring(0,basePath.lastIndexOf("/"));
                if (null != page.getHtml().xpath(entry.getKey()).toString()) {
                    String strSrc = page.getHtml().xpath(entry.getKey()).toString();
                    String url;
                    if(strSrc.startsWith("http://") || strSrc.startsWith("https://"))
                        url = strSrc;
                    else {
                        if(strSrc.startsWith("/"))
                            url = basePath + strSrc;
                        else
                            url = basePath +"/" + strSrc;
                    }

                    FastCollectDataForBaiDuResponse fastCollectDataForBaiDuResponse = new FastCollectDataForBaiDuResponse();
                    fastCollectDataForBaiDuResponse.setTitle(fastCollectDataForBaiDuResponseXpath.getTitleXpath() == null
                            ? null : page.getHtml().xpath(entry.getValue().get("title").toString()).toString());

                    if(!fastCollectDataForBaiDuResponseXpath.getDescriptionXpathMap().isEmpty()){
                        Map<String,String> descriptionXpathMap = (Map<String,String>) entry.getValue().get("description");
                        for (Map.Entry<String,String> descriptionXpathEntry : descriptionXpathMap.entrySet()){
                            if( null != page.getHtml().xpath(descriptionXpathEntry.getValue()).toString()){
                                String content = page.getHtml().xpath(descriptionXpathEntry.getValue()).toString();
                                Map<String,String> map = DoHtmlUtil.getImgSrcFromHtmlScript(content,basePath,true);
                                for(Map.Entry<String,String> htmlScriptEntry : map.entrySet()){
                                    try {
                                        String uploadResultFileUrl = uploadFileService.uploadFileByUrl(htmlScriptEntry.getValue().replaceAll("amp;",""),loginUser,request,response);
                                        String a = htmlScriptEntry.getKey();
                                        content = content.replaceAll(StringUtil.getNewStringFromString(a),uploadResultFileUrl);
                                        Thread.sleep(1000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    checkPause();
                                }
                                fastCollectDataForBaiDuResponse.setDescription(content);
                                break;
                            }
                        }
                    }
                    fastCollectDataForBaiDuResponse.setOutLink(fastCollectDataForBaiDuResponseXpath.getUrlXpath() == null
                            ? null : UrlUtil.getRealUrl(url));
                    jcCollectContent.setTaskId(collectTaskId);
                    JSONObject fastCollectDataForWebResponseJson = JSONObject.fromObject(fastCollectDataForBaiDuResponse);
                    jcCollectContent.setContentValue(fastCollectDataForWebResponseJson.toString());
                    jcCollectContent.setCreateTime(new Date());
                    jcCollectContent.setCreateUser(loginUser);
                    jcCollectContent.setDeletedFlag(CommonContext.DATA_UN_DELETED);
                    jcCollectContentRepository.save(jcCollectContent);
                }
            }
        }
    }
}
