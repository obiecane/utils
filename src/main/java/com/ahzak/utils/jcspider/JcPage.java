package com.ahzak.utils.jcspider;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.Getter;
import lombok.Setter;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.Map;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/18 10:56
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class JcPage extends Page {

    @Setter
    private Page page;

    @Getter
    @Setter
    private HtmlPage htmlPage;


    @Override
    public Page setSkip(boolean skip) {
        if (page != null) {
            return page.setSkip(skip);
        }
        return super.setSkip(skip);
    }

    @Override
    public void putField(String key, Object field) {
        if (page != null) {
            page.putField(key, field);
        } else {
            super.putField(key, field);
        }
    }

    @Override
    public Html getHtml() {
        if (page != null) {
            return page.getHtml();
        }
        return super.getHtml();
    }

    @Override
    public Json getJson() {
        if (page != null) {
            return page.getJson();
        }
        return super.getJson();
    }

    @Override
    @Deprecated
    public void setHtml(Html html) {
        if (page != null) {
            page.setHtml(html);
        } else {
            super.setHtml(html);
        }
    }

    @Override
    public List<Request> getTargetRequests() {
        if (page != null) {
            return page.getTargetRequests();
        }
        return super.getTargetRequests();
    }

    @Override
    public void addTargetRequests(List<String> requests) {
        if (page != null) {
            page.addTargetRequests(requests);
        } else {
            super.addTargetRequests(requests);
        }
    }

    @Override
    public void addTargetRequests(List<String> requests, long priority) {
        if (page != null) {
            page.addTargetRequests(requests, priority);
        } else {
            super.addTargetRequests(requests, priority);
        }
    }

    @Override
    public void addTargetRequest(String requestString) {
        if (page != null) {
            page.addTargetRequest(requestString);
        } else {
            super.addTargetRequest(requestString);
        }
    }

    @Override
    public void addTargetRequest(Request request) {
        if (page != null) {
            page.addTargetRequest(request);
        } else {
            super.addTargetRequest(request);
        }
    }

    @Override
    public Selectable getUrl() {
        if (page != null) {
            return page.getUrl();
        }
        return super.getUrl();
    }

    @Override
    public void setUrl(Selectable url) {
        if (page != null) {
            page.setUrl(url);
        } else {
            super.setUrl(url);
        }
    }

    @Override
    public Request getRequest() {
        if (page != null) {
            return page.getRequest();
        }
        return super.getRequest();
    }

    @Override
    public void setRequest(Request request) {
        if (page != null) {
            page.setRequest(request);
        } else {
            super.setRequest(request);
        }
    }

    @Override
    public ResultItems getResultItems() {
        if (page != null) {
            return page.getResultItems();
        }
        return super.getResultItems();
    }

    @Override
    public int getStatusCode() {
        if (page != null) {
            return page.getStatusCode();
        }
        return super.getStatusCode();
    }

    @Override
    public void setStatusCode(int statusCode) {
        if (page != null) {
            page.setStatusCode(statusCode);
        } else {
            super.setStatusCode(statusCode);
        }
    }

    @Override
    public String getRawText() {
        if (page != null) {
            return page.getRawText();
        }
        return super.getRawText();
    }

    @Override
    public Page setRawText(String rawText) {
        if (page != null) {
            return page.setRawText(rawText);
        }
        return super.setRawText(rawText);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        if (page != null) {
            return page.getHeaders();
        }
        return super.getHeaders();
    }

    @Override
    public void setHeaders(Map<String, List<String>> headers) {
        if (page != null) {
            page.setHeaders(headers);
        } else {
            super.setHeaders(headers);
        }
    }

    @Override
    public boolean isDownloadSuccess() {
        if (page != null) {
            return page.isDownloadSuccess();
        }
        return super.isDownloadSuccess();
    }

    @Override
    public void setDownloadSuccess(boolean downloadSuccess) {
        if (page != null) {
            page.setDownloadSuccess(downloadSuccess);
        } else {
            super.setDownloadSuccess(downloadSuccess);
        }
    }

    @Override
    public byte[] getBytes() {
        if (page != null) {
            return page.getBytes();
        }
        return super.getBytes();
    }

    @Override
    public void setBytes(byte[] bytes) {
        if (page != null) {
            page.setBytes(bytes);
        } else {
            super.setBytes(bytes);
        }
    }

    @Override
    public String getCharset() {
        if (page != null) {
            return page.getCharset();
        }
        return super.getCharset();
    }

    @Override
    public void setCharset(String charset) {
        if (page != null) {
            page.setCharset(charset);
        } else {
            super.setCharset(charset);
        }
    }
}
