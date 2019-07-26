package com.ahzak.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/25 11:02
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class HtmlDownloader {

    private HtmlDownloader() {}

    private static final Pattern charsetPattern = Pattern.compile("(?i)\\bcharset=\\s*(?:\"|')?([^\\s,;\"']*)");
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

    // 引用资源本地化
    private static boolean resLocalization = false;

    public static void main(String[] args) throws IOException {
//        download("https://new.qq.com/omn/20190725/20190725A0TD2900.html", true);
        // https://news.sina.com.cn/o/2019-07-26/doc-ihytcitm4815258.shtml
        // http://www.gov.cn/guowuyuan/2019-07/25/content_5415268.htm
        download("https://news.sina.com.cn/o/2019-07-26/doc-ihytcitm4815258.shtml", true);
    }


    /**
     * 下载html页面
     * @param url 页面url
     * @param resLocalization 引用资源本地化, 如果设为true, 将会把css,js,img下载到本地,并引用本地资源
     *                        如果下载失败, 将依然引用远端资源
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/7/26 12:00
     **/
    public static String download(String url, boolean resLocalization) {
        try {
            Document doc = doGetDocument(url);
            String download = Page.of(doc, resLocalization).repCss().repImg().repJs().repIframe().download();
            return download;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void download(String url, OutputStream outputStream, boolean resLocalization) {
        try {

            Document doc = doGetDocument(url);
            Page.of(doc, resLocalization).repCss().repImg().repJs().repIframe().download(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getContent(String url) {
        try {
            Document doc = doGetDocument(url);
            Page page = Page.of(doc, false);
            return page.repCss().repImg().repJs().repIframe().getContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String htmlFilename(String url) {
        String filename = StringUtils.substringAfterLast(url, "/");
        boolean suffixValid = filename.endsWith(".html") || filename.endsWith(".htm");
        if (StringUtils.isBlank(filename) || !suffixValid) {
            filename = randomHtmlName();
        }
        return filename;
    }

    private static String getCharsetFromContentType(String contentType) {
        if (contentType == null) {
            return null;
        } else {
            Matcher m = charsetPattern.matcher(contentType);
            if (m.find()) {
                String charset = m.group(1).trim();
                charset = charset.replace("charset=", "");
                if (charset.length() == 0) {
                    return null;
                }

                try {
                    if (Charset.isSupported(charset)) {
                        return charset;
                    }

                    charset = charset.toUpperCase(Locale.ENGLISH);
                    if (Charset.isSupported(charset)) {
                        return charset;
                    }
                } catch (IllegalCharsetNameException var4) {
                    return null;
                }
            }

            return null;
        }
    }

    private static Document doGetDocument(String urlStr) {
        Document doc = null;
        try {
            URL url = new URL(urlStr);
            //创建httpClient实例
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                //创建httpGet实例
                HttpGet httpGet = new HttpGet(url.toURI());
                Random random = new Random();
                httpGet.addHeader("User-Agent", USER_AGENTS[random.nextInt(USER_AGENTS.length)]);
                httpGet.addHeader("Host", url.getHost());
                httpGet.addHeader("Connection", "keep-alive");
                httpGet.addHeader("Cache-Control", "max-age=0");
                httpGet.addHeader("Upgrade-Insecure-Requests", "1");
                httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
                httpGet.addHeader("Accept-Encoding", "gzip, deflate");
                httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");

                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    if (response != null) {
                        //获取网页内容
                        HttpEntity entity = response.getEntity();
                        String contentType = entity.getContentType().getValue();
                        ByteBuffer bf = ByteBuffer.wrap(EntityUtils.toByteArray(entity));

                        String charsetName = getCharsetFromContentType(contentType);
                        if (charsetName == null) {
                            String docData = Charset.forName("UTF-8").decode(bf).toString();
                            doc = Jsoup.parse(docData, urlStr);
                            Element meta = doc.select("meta[http-equiv=content-type], meta[charset]").first();
                            if (meta != null) {
                                String foundCharset = null;
                                if (meta.hasAttr("http-equiv")) {
                                    foundCharset = getCharsetFromContentType(meta.attr("content"));
                                }

                                if (foundCharset == null && meta.hasAttr("charset")) {
                                    try {
                                        if (Charset.isSupported(meta.attr("charset"))) {
                                            foundCharset = meta.attr("charset");
                                        }
                                    } catch (IllegalCharsetNameException var9) {
                                        foundCharset = null;
                                    }
                                }

                                if (StringUtils.isNotBlank(foundCharset) && !"UTF-8".equals(foundCharset)) {
                                    foundCharset = foundCharset.trim().replaceAll("[\"']", "");
                                    bf.rewind();
                                    Charset charset = Charset.forName(foundCharset);
                                    docData = charset.decode(bf).toString();
                                    doc = Jsoup.parse(docData, urlStr);
                                    doc.charset(charset);
                                }
                            }
                        } else {
                            Charset charset = Charset.forName(charsetName);
                            String docData = charset.decode(bf).toString();
                            doc = Jsoup.parse(docData, urlStr);
                            doc.charset(charset);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }


    private static String randomHtmlName() {
        return UUID.randomUUID().toString().replaceAll("-", "") + ".html";
    }

    private static File getBaseDir(Page page) {
        File baseDirFile = null;
        try {
            URI uri = HtmlDownloader.class.getClassLoader().getResource(".").toURI();
            baseDirFile = new File(new File(uri) , File.separator + "static" + File.separator + page.url.getHost() + File.separator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baseDirFile;
    }



    private static class Page {
        URL url;
        /** 页面的Document对象 */
        Document doc;
        /** 引用资源本地化标志 */
        boolean resLocalization;
        /** 待下载的css */
        private List<Resource> cssList = new LinkedList<>();
        /** 待下载的js */
        private List<Resource> jsList = new LinkedList<>();
        /** 待下载的图片 */
        private List<Resource> imgList = new LinkedList<>();
        /** 待下载的iframe */
        private List<Page> iframeList = new LinkedList<>();

        static Page of(Document doc, boolean resLocalization) {
            Page page = new Page();
            page.resLocalization = resLocalization;
            page.doc = doc;
            try {
                page.url = new URL(doc.location());
            } catch (MalformedURLException ignore) {
            }
            return page;
        }


        String getContent() {
            return doc.html();
        }

        String getUrl() {
            return doc.location();
        }

        Charset getCharset() {
            return doc.charset();
        }




        /**
         * 把页面中引用图片的相对路径改为绝对路径
         * @return com.jeecms.collect.data.util.HtmlDownloader.Page
         * @author Zhu Kaixiao
         * @date 2019/7/25 14:56
         **/
        Page repImg() {
            //替换图片地址为绝对地址
            for (Element img : doc.body().select("img")) {
                Resource res = Resource.of(this, img);
                if (resLocalization) {
                    //获取图片资源，用于后期统一下载
                    imgList.add(res);
                    img.attr("src", res.localRel);
                } else {
                    img.attr("src", res.absUrl);
                }
            }
            return this;
        }

        /**
         * 把页面中引用JS的相对路径改为绝对路径
         * @return com.jeecms.collect.data.util.HtmlDownloader.Page
         * @author Zhu Kaixiao
         * @date 2019/7/25 14:57
         **/
        Page repJs() {
            //替换js地址为绝对地址
            for (Element js : doc.select("script[src]")) {
                Resource res = Resource.of(this, js);
                if (resLocalization) {
                    jsList.add(res);
                    js.attr("src", res.localRel);
                } else {
                    js.attr("src", res.absUrl);
                }
            }
            return this;
        }

        /**
         * 把页面中引用CSS的相对路径改为绝对路径
         * @return com.jeecms.collect.data.util.HtmlDownloader.Page
         * @author Zhu Kaixiao
         * @date 2019/7/25 14:57
         **/
        Page repCss() {
            //替换css地址为绝对地址
            for (Element css : doc.select("link[href][rel=stylesheet]")) {
                Resource res = Resource.of(this, css);
                if (resLocalization) {
                    cssList.add(res);
                    css.attr("href", res.localRel);
                } else {
                    css.attr("href", res.absUrl);
                }
            }
            return this;
        }


        /**
         * 处理网页中嵌套的页面
         *
         * @return com.jeecms.collect.data.util.HtmlDownloader.Page
         * @author Zhu Kaixiao
         * @date 2019/7/25 14:55
         **/
        Page repIframe() throws IOException {
            //替换css地址为绝对地址
            for (Element iframe : doc.select("iframe[src]")) {
                //获得绝对路径
                String absIframeUrl = iframe.attr("abs:src");

                if (resLocalization) {
                    Document iframeDoc = doGetDocument(absIframeUrl);
                    Page iPage = Page.of(iframeDoc, resLocalization);
                    iframeList.add(iPage);
                    //替换地址
                    iframe.attr("src", "./" + htmlFilename(absIframeUrl));
                } else {
                    //替换地址
                    iframe.attr("src", absIframeUrl);
                }
            }
            return this;
        }

        /**
         * 下载页面
         * @return java.lang.String
         * @author Zhu Kaixiao
         * @date 2019/7/25 14:57
         **/
        String download() throws IOException {

            for (Page iPage : iframeList) {
                iPage.repCss().repJs().repImg().download();
            }

            jsList.forEach(Resource::download);
            cssList.forEach(Resource::download);
            imgList.forEach(Resource::download);

            String filename = htmlFilename(getUrl());
            File baseDir = getBaseDir(this);
            File localPagePath = new File(baseDir, filename);
            FileUtils.write(localPagePath, getContent(), getCharset());
            return filename;
        }

        void download(OutputStream outputStream) throws IOException {
            IOUtils.write(getContent(), outputStream, getCharset());
        }


        private String fixAbsUrl(String oriUrl, String absUrl) {
            if (StringUtils.isBlank(absUrl)) {
                if (absUrl.startsWith("//")) {
                    absUrl = "http:" + absUrl;
                } else if (absUrl.startsWith("/")) {
                    absUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort()) + oriUrl;
                } else {
                    System.err.println("获取绝对路径失败");
                }
            }
            return absUrl;
        }
    }

    private static class Resource {
        String absUrl;
        String oriUrl;
        Element eleNode;
        // 本地引用路径
        String localRel;
        Page page;

        static Resource of(Page page, Element eleNode) {
            Resource res = new Resource();
            String atteName = srcAtteName(eleNode);
            res.page = page;
            res.eleNode = eleNode;
            res.oriUrl = eleNode.attr(atteName);
            //获得绝对路径
            res.absUrl = page.fixAbsUrl(res.oriUrl, eleNode.attr("abs:" + atteName));
            try {
                res.localRel = "." + getLocalRelFilename(res.absUrl);
            } catch (MalformedURLException ignore) {
            }
            return res;
        }


        void download() {
            try {
                File baseDir = getBaseDir(page);
                File localFile = new File(baseDir, getLocalRelFilename(absUrl));
                // 如果是css文件, 把里面的资源也下下来
                if (localFile.getName().toLowerCase(Locale.ENGLISH).endsWith(".css")) {
                    downloadCss(absUrl, localFile.getCanonicalPath());
                } else {
                    TransportUtil.downLoadFromUrl(absUrl, localFile.getCanonicalPath());
                }
            } catch (IOException e) {
                String atteName = srcAtteName(eleNode);
                eleNode.attr(atteName, absUrl);
            }
        }

        private static Pattern p = Pattern.compile("(?<=url\\s{0,10}\\().*?(?=\\))");


        private void downloadCss(String urlStr, String savePath) throws IOException {
            File file = new File(savePath);

            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("Host", url.getHost());
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");


            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                 FileOutputStream fos = new FileOutputStream(file)) {
                String s;
                while ((s = reader.readLine()) != null) {
                    Matcher matcher;
                    int findStart = 0;
                    while ((matcher = p.matcher(s)).find(findStart)) {
                        String nUrl = matcher.group();
                        if (nUrl.startsWith("\"")) {
                            nUrl = nUrl.substring(1);
                        }
                        if (nUrl.endsWith("\"")) {
                            nUrl = nUrl.substring(0, nUrl.length() - 1);
                        }
                        if (nUrl.contains("?")) {
                            nUrl = nUrl.split("\\?")[0];
                        }

                        if (!nUrl.startsWith("http")) {
                            if (nUrl.startsWith("//")) {
                                nUrl = "http:" + nUrl;
                            } else {
                                // 修补url
                                nUrl = "/" + StringUtils.substringBeforeLast(url.getFile(), "/") + "/" + nUrl;
                                nUrl = nUrl.replaceAll("/\\./", "/");
                                nUrl = nUrl.replaceAll("/[^/]+?/\\.\\./", "/");
                                nUrl = url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort()) + "/" + nUrl;
                                nUrl = nUrl.replaceAll("//+", "/");
                                nUrl = url.getProtocol() + "://" + nUrl;
                            }
                            // 下载
                            File baseDir = getBaseDir(page);
                            File localFile = new File(baseDir, getLocalRelFilename(nUrl));
                            try {
                                TransportUtil.downLoadFromUrl(nUrl, localFile.getCanonicalPath(), false);
                                // 因为目录层级和远端保持一致, 所以不需要替换引用
                            } catch (Exception e) {
                                if (!(e instanceof IllegalArgumentException)) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        findStart = matcher.end();
                    }

                    s += "\n";
                    byte[] buffer = s.getBytes();
                    fos.write(buffer, 0, buffer.length);
                }
                fos.flush();
            }

            System.out.println("info:" + url + " download success");
        }

        private static String srcAtteName(Element eleNode) {
            String atteName;
            String nodeName = eleNode.nodeName();
            if ("img".equalsIgnoreCase(nodeName) || "script".equalsIgnoreCase(nodeName)) {
                atteName = "src";
            } else {
                atteName = "href";
            }
            return atteName;
        }

        private static String getLocalRelFilename(String urlStr) throws MalformedURLException {
            URL url = new URL(urlStr);
            return url.getFile();
        }
    }

}