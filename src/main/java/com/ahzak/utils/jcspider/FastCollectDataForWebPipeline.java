package com.ahzak.utils.jcspider;

import com.jeecms.collect.data.context.CommonContext;
import com.jeecms.collect.data.model.JcCollectContent;
import com.jeecms.collect.data.repository.JcCollectContentRepository;
import com.jeecms.collect.data.response.FastCollectDataForWebResponse;
import com.jeecms.collect.data.service.UploadFileService;
import com.jeecms.collect.data.util.DoHtmlUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Component
public class FastCollectDataForWebPipeline extends JcPipeline {

    private static JcCollectContentRepository JCCOLLECTCONTENTREPOSITORY;
    private static UploadFileService UPLOADFILESERVICE;

    @Autowired
    private JcCollectContentRepository jcCollectContentRepository;

    @Autowired
    private UploadFileService uploadFileService;

    private HttpServletRequest request;

    private HttpServletResponse response;

    public FastCollectDataForWebPipeline() {
        jcCollectContentRepository = JCCOLLECTCONTENTREPOSITORY;
        uploadFileService = UPLOADFILESERVICE;
    }

    @PostConstruct
    private void initAutowired() {
        JCCOLLECTCONTENTREPOSITORY = jcCollectContentRepository;
        UPLOADFILESERVICE = uploadFileService;
    }

    public FastCollectDataForWebPipeline setHttpServletRequest(HttpServletRequest request){
        this.request = request;
        return this;
    }

    public FastCollectDataForWebPipeline setHttpServletResponse(HttpServletResponse response){
        this.response = response;
        return this;
    }

    @Override
    public void doProcess(ResultItems resultItems, Task task) {
        Map<String, Object> mapResults = resultItems.getAll();
        JcCollectContent jcCollectContent = new JcCollectContent();
        if(mapResults.size()>0){
            String loginUser = mapResults.get("loginUser").toString();
            Integer collectTaskId = Integer.valueOf(mapResults.get("collectTaskId").toString());
            FastCollectDataForWebResponse fastCollectDataForWebResponse = new FastCollectDataForWebResponse();
            fastCollectDataForWebResponse.setTitle(null != mapResults.get("title") ? mapResults.get("title").toString() : null);
            fastCollectDataForWebResponse.setOutLink(null != mapResults.get("url") ? mapResults.get("url").toString() : null);
            fastCollectDataForWebResponse.setReleaseTime(null != mapResults.get("time") ? mapResults.get("time").toString() : null);
            fastCollectDataForWebResponse.setContentSourceId(null != mapResults.get("comeFrom") ? mapResults.get("comeFrom").toString() : null);
            String contxt = null;
            if(null != mapResults.get("content")){
                String content = mapResults.get("content").toString();
                String basePath = mapResults.get("pathUrl").toString();
                basePath = basePath.substring(0,basePath.lastIndexOf("/"));
                Map<String,String> map = DoHtmlUtil.getImgSrcFromHtmlScript(content,basePath,false);
                for(Map.Entry<String,String> entry : map.entrySet()){
                    try {
                        String uploadResultFileUrl = uploadFileService.uploadFileByUrl(entry.getValue(),loginUser,request,response);
                        content = content.replaceAll(entry.getKey(),uploadResultFileUrl);
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    checkPause();
                }
                contxt = content;
            }
            fastCollectDataForWebResponse.setContxt(contxt);
            JSONObject fastCollectDataForWebResponseJson = JSONObject.fromObject(fastCollectDataForWebResponse);
            jcCollectContent.setTaskId(collectTaskId);
            jcCollectContent.setContentValue(fastCollectDataForWebResponseJson.toString());
            jcCollectContent.setCreateTime(new Date());
            jcCollectContent.setCreateUser(loginUser);
            jcCollectContent.setDeletedFlag(CommonContext.DATA_UN_DELETED);
            jcCollectContentRepository.save(jcCollectContent);
//            webLogsService.output(loginUser+WebSocketContext.MESSAGE_PARTITION_ELEMENT+mapResults.get("pathUrl").toString());
        }

    }


}