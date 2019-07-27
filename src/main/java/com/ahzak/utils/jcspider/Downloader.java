package com.ahzak.utils.jcspider;

import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/18 10:53
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class Downloader extends AbstractDownloader {

    private FreeProxyPool freeProxyPool;
    private FreeProxy freeProxy;

    private Map<String, CookieManager> cm = new ConcurrentHashMap<>();


    @Override
    public Page download(Request request, Task task) {
        WebClient webClient = createWebClient(request, task);
        JcPage jcPage = new JcPage();
        jcPage.setUrl(new PlainText(request.getUrl()));
        jcPage.setRequest(request);
        try {
            WebRequest webRequest = createRequest(request, task, freeProxy);
            HtmlPage htmlPage = null;
            for (;;) {
                try {
                    htmlPage = webClient.getPage(webRequest);
                    break;
                } catch (Exception e) {
                    // 发生异常就切换代理, 直到页面下载成功, 或者代理耗尽
                    if (freeProxyPool != null) {
                        if (!switchProxy()) {
                            log.debug("代理都切换完了还是没法访问-_-");
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }

            //异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
//            webClient.waitForBackgroundJavaScript(30000);
            if (htmlPage != null) {
                jcPage.setHtmlPage(htmlPage);
                jcPage.setRawText(htmlPage.asXml());
            }
        } catch (IOException e) {
            e.printStackTrace();
            jcPage.setDownloadSuccess(false);
        }
        return jcPage;
    }

    @Override
    public void setThread(int threadNum) {

    }


    private WebClient createWebClient(Request request, Task task) {
        WebClient webClient = new WebClient();
        //当JS执行出错的时候是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //当HTTP的状态非200时是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(false);
        //是否启用CSS, 因为不需要展现页面, 所以不需要启用
        webClient.getOptions().setCssEnabled(false);
        //很重要，启用JS
        webClient.getOptions().setJavaScriptEnabled(true);
        //很重要，设置支持AJAX
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setUseInsecureSSL(true);
        Site site = task.getSite();

        String ip = Optional.ofNullable(freeProxy).map(FreeProxy::getHost).orElse("127.0.0.1");

        CookieManager cookieManager;
        if ((cookieManager = cm.get(ip)) == null) {
            cookieManager = new CookieManager();
            cm.put(ip, cookieManager);
        }
        webClient.setCookieManager(cookieManager);

        Map<String, String> headers = new HashMap<>(16);
        headers.putAll(site.getHeaders());
        headers.putAll(request.getHeaders());
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            webClient.addRequestHeader(entry.getKey(), entry.getValue());
        }

        Map<String, String> cookies = new HashMap<>(16);
        cookies.putAll(site.getCookies());
        cookies.putAll(request.getCookies());

        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            cookieManager.addCookie(new Cookie(site.getDomain(), entry.getKey(), entry.getValue()));
        }

        webClient.getCookieManager().setCookiesEnabled(!site.isDisableCookieManagement());
        return webClient;
    }

    private WebRequest createRequest(Request request, Task task, FreeProxy freeProxy) throws MalformedURLException {
        WebRequest webRequest = new WebRequest(UrlUtils.toUrlUnsafe(request.getUrl()));
        webRequest.setCharset(StandardCharsets.UTF_8);
        if (freeProxy == null && freeProxyPool != null) {
            freeProxy = freeProxyPool.nextProxy(null);
        }
        if (freeProxy != null) {
            webRequest.setProxyHost(freeProxy.getHost());
            webRequest.setProxyPort(freeProxy.getPort());
        }

        Site site = task.getSite();
        webRequest.setAdditionalHeader("User-Agent", Optional.of(site).map(Site::getUserAgent).orElse(""));
        return webRequest;
    }


    public Downloader setProxyPool(FreeProxyPool freeProxyPool) {
        this.freeProxyPool = freeProxyPool;
        return this;
    }

    private boolean switchProxy() {
        if (freeProxyPool == null) {
            return false;
        }
        freeProxy = freeProxyPool.nextProxy(freeProxy);
        log.debug("切换代理  [{}]", freeProxy);
        return freeProxy != null;
    }


}
