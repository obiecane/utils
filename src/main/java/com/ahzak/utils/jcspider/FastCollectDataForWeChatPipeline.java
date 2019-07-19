package com.ahzak.utils.jcspider;
import com.jeecms.collect.data.model.JcCollectContent;
import com.jeecms.collect.data.repository.JcCollectContentRepository;
import com.jeecms.collect.data.response.FastCollectDataForWeChatResponse;
import com.jeecms.collect.data.service.UploadFileService;
import com.jeecms.collect.data.util.DoHtmlUtil;
import com.jeecms.collect.data.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class FastCollectDataForWeChatPipeline extends JcPipeline {

    @Autowired
    private JcCollectContentRepository jcCollectContentRepository;

    @Autowired
    private UploadFileService uploadFileService;

    private static JcCollectContentRepository JCCOLLECTCONTENTREPOSITORY;
    private static UploadFileService UPLOADFILESERVICE;


    @PostConstruct
    private void autowiredInit() {
        // TODO 改为通过ApplicationContext手动注入
        JCCOLLECTCONTENTREPOSITORY = jcCollectContentRepository;
        UPLOADFILESERVICE = uploadFileService;
    }

    private HttpServletRequest request;

    private HttpServletResponse response;


    public FastCollectDataForWeChatPipeline() {
        jcCollectContentRepository = JCCOLLECTCONTENTREPOSITORY;
        uploadFileService = UPLOADFILESERVICE;
    }


    public FastCollectDataForWeChatPipeline setHttpServletRequest(HttpServletRequest request){
        this.request = request;
        return this;
    }

    public FastCollectDataForWeChatPipeline setHttpServletResponse(HttpServletResponse response){
        this.response = response;
        return this;
    }


    @Override
    public void doProcess(ResultItems resultItems, Task task) {
        Map<String, Object> mapResults = resultItems.getAll();
        if(null != mapResults.get("content")){
            Integer jcCollectContentId = Integer.valueOf(mapResults.get("jcCollectContentId").toString().split("=")[1]);
            String content = mapResults.get("content").toString();
            String pathUrl = mapResults.get("pathUrl").toString();
            String loginUser = mapResults.get("loginUser").toString();
            String basePath = pathUrl.substring(0,pathUrl.lastIndexOf("/"));
            Map<String,String> map = DoHtmlUtil.getImgSrcFromHtmlScript(content,basePath,false);
            for(Map.Entry<String,String> entry : map.entrySet()){
                checkPause();
                try {
                    String uploadResultFileUrl = uploadFileService.uploadFileByUrl(entry.getValue(),loginUser,request,response);
                    String a = entry.getKey();
                    content = content.replaceAll(StringUtil.getNewStringFromString(a),uploadResultFileUrl);
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            JcCollectContent jcCollectContent = jcCollectContentRepository.findById(jcCollectContentId).get();
            JSONObject jsonObject = JSONObject.fromObject(jcCollectContent.getContentValue());
            FastCollectDataForWeChatResponse fastCollectDataForWeChatResponse = (FastCollectDataForWeChatResponse)JSONObject.toBean(jsonObject,FastCollectDataForWeChatResponse.class);//将建json对象转换为Person对象
            fastCollectDataForWeChatResponse.setContxt(content);
            JSONObject fastCollectDataForWebResponseJson = JSONObject.fromObject(fastCollectDataForWeChatResponse);
            jcCollectContent.setContentValue(fastCollectDataForWebResponseJson.toString());
            jcCollectContent.setUpdateTime(new Date());
            jcCollectContent.setUpdateUser(loginUser);
            jcCollectContentRepository.save(jcCollectContent);
        }
    }
}
