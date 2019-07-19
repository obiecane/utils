package com.ahzak.utils.jcspider;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代理ip池
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/17 15:11
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@Component
public class FreeProxyPool {

    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 OPR/26.0.1656.60",
            "Opera/8.0 (Windows NT 5.1; U; en)",
            "Mozilla/5.0 (Windows NT 5.1; U; en; rv:1.8.1) Gecko/20061208 Firefox/2.0.0 Opera 9.50",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; en) Opera 9.50",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0",
            "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2 ",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36",
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

    private static final Pattern p = Pattern.compile("(?<=;}\\);</script>).*(?=<br>高效高匿名代理IP提取地址)");
    private static final Pattern PATTERN_66IP = Pattern.compile("(?<=</script>).*(?=<br/></div>)");

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(16, 32, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1024),
            new ThreadFactoryBuilder().setNameFormat("proxy-pick-%02d").build(),
            new ThreadPoolExecutor.AbortPolicy());

    private static final List<FreeProxy> freeProxyPool = Collections.synchronizedList(new ArrayList<>(100));

    private static final int PROXY_PICK_SIZE = 50;

    private static final String FREE_PROXY_URL_1 = "https://lab.crossincode.com/proxy/get/?num=" + PROXY_PICK_SIZE;
    private static final String FREE_PROXY_URL_2 = "http://www.89ip.cn/tqdl.html?api=1&num=" + PROXY_PICK_SIZE + "&port=&address=&isp=";
    // 高匿代理
    private static final String FREE_PROXY_URL_3 = "https://www.xicidaili.com/nn";
    private static final String FREE_PROXY_URL_4 = "http://www.66ip.cn/nmtq.php?getnum=" + PROXY_PICK_SIZE + "&isp=0&anonymoustype=3&start=&ports=&export=&ipaddress=&area=0&proxytype=1&api=66ip";

    private static final String SOGOU_SNUID_COOKIE_URL = "https://weixin.sogou.com/weixin?type=2&query=%E5%AE%9D%E5%A4%9A%E5%85%AD%E8%8A%B1&ie=utf8&s_from=input&sug=n&sug_type=&w=01019900&sut=205&sst0=1543168556321&lkt=1,1543168556219,1543168556219";


    static {
        // TODO 定时刷新
        refreshProxy();
    }

    public static void main(String[] args) throws IOException {

//        while (freeProxyPool.isEmpty()) {
//
//        }

        String s = get66ip("http://www.66ip.cn/nmtq.php?getnum=50&isp=0&anonymoustype=3&start=&ports=&export=&ipaddress=&area=0&proxytype=1&api=66ip");

        System.out.println(s);
        resolve66ip(s);
//        getCookie1(randomProxy());
    }


    /**
     * 解析西刺免费代理页面
     * @param html html
     * @return java.util.List<com.jeecms.collect.data.service.jcspider.FreeProxy>
     * @author Zhu Kaixiao
     * @date 2019/7/18 13:48
     **/
    private static List<FreeProxy> resolveXici(String html) {
        Document doc = Jsoup.parse(html);
        Elements select = doc.select("#ip_list>tbody>tr>td:nth-child(2)");
        String[] hosts = select.text().split(" ");
        Elements select2 = doc.select("#ip_list>tbody>tr>td:nth-child(3)");
        String[] ports = select2.text().split(" ");
        List<FreeProxy> proxies = new ArrayList<>(hosts.length);
        for (int i = 0; i < hosts.length; i++) {
            FreeProxy freeProxy = new FreeProxy();
            proxies.add(freeProxy);
            freeProxy.setHost(hosts[i]);
            freeProxy.setPort(Integer.parseInt(ports[i]));
        }
        return proxies;
    }


    private static List<FreeProxy> resolve66ip(String html) {
        List<FreeProxy> proxies = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.body();
        String text = body.text();
        String[] hps = text.split(" ");
        for (String hp : hps) {
            FreeProxy freeProxy = convertStrToProxy(hp);
            proxies.add(freeProxy);
        }
        return proxies;
    }

    @Scheduled(initialDelay = 0, fixedRate = 10 * 60 * 1000)
    public void sch() {
        System.out.println("free proxy 定时任务...");
    }


    private static void refreshProxy() {
        try {
            List<FreeProxy> proxies = new ArrayList<>(100);
            String html;
//        html = doGet(FREE_PROXY_URL_1);
//        if (html != null) {
//            List<FreeProxy> freeProxies = fetchFromCrossin(html);
//            proxies.addAll(freeProxies);
//        }
//        html = doGet(FREE_PROXY_URL_2);
//        if (html != null) {
//            List<FreeProxy> freeProxies = regexProxy(html);
//            proxies.addAll(freeProxies);
//        }
            html = doGet(FREE_PROXY_URL_3);
            if (html != null) {
                List<FreeProxy> freeProxies = resolveXici(html);
                proxies.addAll(freeProxies);
            }

            html = get66ip(FREE_PROXY_URL_4);
            if (html != null) {
                List<FreeProxy> freeProxies = resolve66ip(html);
                proxies.addAll(freeProxies);
            }

            for (FreeProxy freeProxy : proxies) {
                EXECUTOR.submit(() -> {
                    if (!freeProxyPool.contains(freeProxy)) {
                        boolean useful = testProxy(freeProxy);
                        if (useful) {
                            freeProxyPool.add(freeProxy);
                        }
                    }
                });
            }
        } catch (Exception ignore) {

        }

    }



    // 抓取代理
    private static String doGet(String url) {
        String result = null;
        try {
            //创建httpClient实例
            CloseableHttpClient httpClient = HttpClients.createDefault();
            //创建httpGet实例
            HttpGet httpGet = new HttpGet(url);
            Random random = new Random();
            httpGet.addHeader("User-Agent", USER_AGENTS[random.nextInt(USER_AGENTS.length)]);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity entity = response.getEntity();  //获取网页内容
                result = EntityUtils.toString(entity, "UTF-8");
                response.close();
            }
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static List<FreeProxy> regexProxy(String html) {
        List<FreeProxy> freeProxies = new ArrayList<>(PROXY_PICK_SIZE);
        html = html.replaceAll("\n", "");

        Matcher matcher = p.matcher(html);
        if (matcher.find()) {
            String group = matcher.group();
            String[] split = group.split("<br>");
            for (String hp : split) {
                FreeProxy freeProxy = convertStrToProxy(hp);
                freeProxies.add(freeProxy);
            }
        }
        return freeProxies;
    }

    private static List<FreeProxy> fetchFromCrossin(String json) {
        List<FreeProxy> freeProxies = new ArrayList<>(PROXY_PICK_SIZE);
        Map map = JSONObject.parseObject(json, HashMap.class);
        JSONArray arr = (JSONArray) map.get("proxies");
        for (Object value : arr) {
            JSONObject o = (JSONObject) value;
            String hp = o.getString("http");
            FreeProxy freeProxy = convertStrToProxy(hp);
            freeProxies.add(freeProxy);
        }
        return freeProxies;
    }


    private static FreeProxy convertStrToProxy(String hp) {
        String[] hpArr = hp.split(":");
        FreeProxy freeProxy = new FreeProxy();
        freeProxy.setHost(hpArr[0]);
        freeProxy.setPort(Integer.parseInt(hpArr[1]));
        return freeProxy;
    }

    // 清洗代理
    private static boolean testProxy(FreeProxy freeProxy) {
        try {
            long start = System.currentTimeMillis();
            URL url = new URL("http://www.baidu.com");
            // 创建代理服务器
            InetSocketAddress addr = new InetSocketAddress(freeProxy.getHost(), freeProxy.getPort());
            Proxy proxy = new Proxy(Proxy.Type.HTTP, addr); // http 代理
            URLConnection conn = url.openConnection(proxy);
            InputStream in = conn.getInputStream();
            String s = IOUtils.toString(in, StandardCharsets.UTF_8);
            if (s.indexOf("百度") > 0) {
                long end = System.currentTimeMillis();
                long timeout = end - start;
                // 延时越低, 分数越高
                long w = 60000 - timeout;
                freeProxy.setWeight(w < 0 ? 1000 : w);
                log.debug("[OK  ] {}", freeProxy);
                return true;
            } else {
                log.debug("[FAIL] {}", freeProxy);
            }
        } catch (IOException e) {
            log.debug("[FAIL] {}", freeProxy);
        }
        return false;
    }


    public FreeProxy randomProxy() {
        if (freeProxyPool.isEmpty()) {
            log.warn("代理池为空");
            return null;
        }
        Random random = new Random();
        return freeProxyPool.get(random.nextInt(freeProxyPool.size()));
    }

    public FreeProxy nextProxy(FreeProxy proxy) {
        if (proxy == null) {
            if (!freeProxyPool.isEmpty()) {
                freeProxyPool.sort((o1, o2) -> Long.compare(o2.getWeight(), o1.getWeight()));
                return freeProxyPool.get(0);
            }
        } else {
            // 减分
            proxy.setWeight(proxy.getWeight() - 5000);
            if (proxy.getWeight() < 0) {
                freeProxyPool.remove(proxy);
            }
            // 按分排序  取第一个
            freeProxyPool.sort((o1, o2) -> Long.compare(o2.getWeight(), o1.getWeight()));
            return freeProxyPool.get(0);
//            int i = freeProxyPool.indexOf(proxy);
//            if (i > 0 && i + 1 < freeProxyPool.size()) {
//                return freeProxyPool.get(i + 1);
//            }
        }
        return null;
    }


    private static String get66ip(String url) {
        WebClient webClient = new WebClient();
        //当JS执行出错的时候是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //当HTTP的状态非200时是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setActiveXNative(false);
        //是否启用CSS, 因为不需要展现页面, 所以不需要启用
        webClient.getOptions().setCssEnabled(true);
        //很重要，启用JS
        webClient.getOptions().setJavaScriptEnabled(true);
        //很重要，设置支持AJAX
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setUseInsecureSSL(true);
        WebRequest request = null;
        try {
            request = new WebRequest(UrlUtils.toUrlUnsafe(url));
            request.setCharset(Charset.forName("UTF-8"));
            request.setAdditionalHeader("Host", "www.66ip.cn");
            request.setAdditionalHeader("Connection", "keep-alive");
            request.setAdditionalHeader("Cache-Control", "max-age=0");
            request.setAdditionalHeader("Upgrade-Insecure-Requests", "1");
            request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
            request.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
            request.setAdditionalHeader("Accept-Encoding", "gzip, deflate");
            request.setAdditionalHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
            request.setAdditionalHeader("Cookie", "__jsluid_h=d83195b9102f972b15ced004c8f73d33; __jsl_clearance=1563499524.541|0|anQtszcB9Kf3nIbTbzRRNRncT4k%3D");

            HtmlPage page = webClient.getPage(request);
            webClient.waitForBackgroundJavaScript(30000);
            String s = page.asXml();
            return s;
        } catch (Exception e) {

        }
        return null;
    }



    public static void getCookie(FreeProxy proxy) {
        String url = "https://weixin.sogou.com/weixin?type=2&query=%E5%AE%9D%E5%A4%9A%E5%85%AD%E8%8A%B1&ie=utf8&s_from=input&sug=n&sug_type=&w=01019900&sut=205&sst0=1543168556321&lkt=1,1543168556219,1543168556219";
        HttpClient httpClient = new HttpClient();

        // 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
        GetMethod getMethod = new GetMethod(url);

        //设置代理服务器的ip地址和端口
        httpClient.getHostConfiguration().setProxy(proxy.getHost(), proxy.getPort());
        httpClient.getParams().setAuthenticationPreemptive(true);

        // 设置登陆时要求的信息，用户名和密码
//        NameValuePair[] data = { new NameValuePair("loginName", "chzeze123"), new NameValuePair("loginPasswd", "**") };
        try {
            // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            int statusCode = httpClient.executeMethod(getMethod);

            // 获Cookie
            Cookie[] cookies = httpClient.getState().getCookies();
            StringBuffer tmpcookies = new StringBuffer();
            for (Cookie c : cookies) {
                tmpcookies.append(c.toString() + ";");
                System.out.println("cookies = " + c.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getCookie1(FreeProxy proxy) {
        WebClient webClient = new WebClient();
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);

        WebRequest request = null;
        try {
            request = new WebRequest(UrlUtils.toUrlUnsafe(SOGOU_SNUID_COOKIE_URL));
            request.setCharset(Charset.forName("UTF-8"));
//            FreeProxy freeProxy = FreeProxyPool.randomProxy();
            request.setProxyHost(proxy.getHost());
            request.setProxyPort(proxy.getPort());


            request.setAdditionalHeader("Accept", "*/*");
            request.setAdditionalHeader("Origin", "https://weixin.sogou.com");
            request.setAdditionalHeader("Referer", "https://weixin.sogou.com/");//设置请求报文头里的refer字段
            request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");


            webClient.getPage(request);
            CookieManager cookieManager = webClient.getCookieManager();
            Set<com.gargoylesoftware.htmlunit.util.Cookie> cookies = cookieManager.getCookies();
            cookies.forEach(cookie -> System.out.printf("cookie:  %s -> %s\n", cookie.getName(), cookie.getValue()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}


