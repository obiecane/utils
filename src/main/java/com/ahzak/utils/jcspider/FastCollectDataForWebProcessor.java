package com.ahzak.utils.jcspider;

import com.jeecms.collect.data.request.FastCollectDataForWebRequest;
import com.jeecms.collect.data.response.FastCollectDataForWebResponseXpath;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FastCollectDataForWebProcessor extends JcPageProcessor {
    private FastCollectDataForWebRequest fastCollectDataForWebRequest;
    private FastCollectDataForWebResponseXpath fastCollectDataForWebResponseXpath;


    public FastCollectDataForWebProcessor setFastCollectDataForWebRequest(FastCollectDataForWebRequest fastCollectDataForWebRequest) {
        this.fastCollectDataForWebRequest = fastCollectDataForWebRequest;
        return this;
    }

    public FastCollectDataForWebProcessor setFastCollectDataForWebResponseXpath(FastCollectDataForWebResponseXpath fastCollectDataForWebResponseXpath) {
        this.fastCollectDataForWebResponseXpath = fastCollectDataForWebResponseXpath;
        return this;
    }


    @Override
    public void doProcess(JcPage page) {
        if (page.getUrl().toString().equals(fastCollectDataForWebRequest.getUrlPath())) {
            for (String urlPathContentXpath : fastCollectDataForWebRequest.getUrlPathContentXpathArray()) {
                List<String> detailUrls = page.getHtml().xpath(urlPathContentXpath).all();
                page.addTargetRequests(detailUrls);
                checkPause();
            }
        } else {
            page.putField("pathUrl", page.getUrl().get());
            page.putField("loginUser", loginUser);
            page.putField("collectTaskId", collectTaskId);
            page.putField("title", fastCollectDataForWebResponseXpath.getContentXpath() == null
                    ? null : page.getHtml().xpath(fastCollectDataForWebResponseXpath.getTitleXpath()));
            page.putField("url", fastCollectDataForWebResponseXpath.getUrlXpath()
                    ? page.getUrl().get() : null);
            page.putField("time", fastCollectDataForWebResponseXpath.getTimeXpath() == null
                    ? null : page.getHtml().xpath(fastCollectDataForWebResponseXpath.getTimeXpath()));
            page.putField("comeFrom", fastCollectDataForWebResponseXpath.getComeFromXpath() == null
                    ? null : page.getHtml().xpath(fastCollectDataForWebResponseXpath.getComeFromXpath()));
            page.putField("content", fastCollectDataForWebResponseXpath.getContentXpath() == null
                    ? null : page.getHtml().xpath(fastCollectDataForWebResponseXpath.getContentXpath()));
        }
    }


}

