package com.ahzak.utils.autofetch;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.css.CSSStyleDeclaration;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;
import us.codecraft.webmagic.Page;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/24 14:51
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public abstract class TitleAutoFetch implements AutoFetch {

    protected URL url;

    /** url的长度及出现的次数 */
    protected Map<Integer, Integer> lenCountMap = new HashMap<>();
    /** url的深度及出现的次数 */
    protected Map<Integer, Integer> depCountMap = new HashMap<>();
    /** url的域名及出现的次数 */
    protected Map<String, Integer> domainCountMap = new HashMap<>();

    protected Set<TitleLink> titleLinks = new LinkedHashSet<>();

    protected static final Pattern DOMAIN_PATTERN = Pattern.compile("(?<!http)([^.]*?\\.)+(?:com\\.cn|net\\.cn|org\\.cn|com|net|org|cn|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
    protected static final Pattern DATE_PATTERN_1 = Pattern.compile("((2\\d{3})|\\d{2})[-/]?([01]?\\d)[-/]?([0123]?\\d)");
    protected static final DateTimeFormatter DATE_FMT_1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    protected void initPageAttr(Page page) {
        try {
            url = new URL(page.getUrl().toString());
        } catch (MalformedURLException e) {
            url = null;
            e.printStackTrace();
        }
    }

    protected boolean checkTitle(DomNode domNode) {

//        String titleContent = domNode.getTextContent().replaceAll("[\n\t ]", "").trim();
//        String titleHref = fixUrl(domNode.getAttributes().getNamedItem("href").getNodeValue());
//        boolean displayed = domNode.isDisplayed();
//        boolean b = checkContentLength(domNode);
//        boolean b1 = checkHref(domNode);

        if (domNode.isDisplayed() && checkContentLength(domNode) && checkHref(domNode)) {
            if (checkParent(domNode)) {
                return true;
            } else {
                return checkFontSize(domNode);
            }
        }
        return false;
    }


    /**
     * 获取祖先节点
     * @param domNode 节点
     * @return java.util.List<org.w3c.dom.Node>
     * @author Zhu Kaixiao
     * @date 2019/7/23 11:53
     **/
    protected static List<Node> getAncestors(DomNode domNode) {
        final List<Node> list = new ArrayList<>();
        list.add(domNode);

        Node node = domNode.getParentNode();
        while (node != null) {
            list.add(0, node);
            node = node.getParentNode();
        }
        return list;
    }



    protected List<CSSStyleDeclaration> getStyles(DomNode domNode) {
        HtmlPage htmlPage = domNode.getHtmlPageOrNull();
        if (htmlPage != null && htmlPage.getEnclosingWindow().getWebClient().getOptions().isCssEnabled()) {
            final List<Node> ancestors = getAncestors(domNode);
            final ArrayList<CSSStyleDeclaration> styles = new ArrayList<>(ancestors.size());

            for (final Node node : ancestors) {
                final Object scriptableObject = ((DomNode) node).getScriptableObject();
                if (scriptableObject instanceof HTMLElement) {
                    final HTMLElement elem = (HTMLElement) scriptableObject;
                    final CSSStyleDeclaration style = elem.getWindow().getComputedStyle(elem, null);
                    styles.add(0, style);
                }
            }

            return styles;
        }

        return Collections.emptyList();
    }


    /**
     * 判断字体大小是否合适
     * @param domNode
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/7/23 13:41
     **/
    protected boolean checkFontSize(DomNode domNode) {
        List<CSSStyleDeclaration> styles = getStyles(domNode);

        for (CSSStyleDeclaration style : styles) {
            String fontSize = style.getFontSize();
            String px = fontSize.replaceAll("px", "");
            if (StringUtils.isBlank(px)) {
                continue;
            }
            int i = Integer.parseInt(px);
            return i > 13;
        }
        return false;
    }



    /**
     * 判断是否首页链接
     * 如: http://www.ecns.cn/
     *     http://shop.hisense.com/doc/
     *     http://www.wahaha.com.cn/product/index.htm
     * 都认为是首页链接
     * @param link 链接
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/7/23 11:08
     **/
    protected boolean isIndex(String link) {
        URL url;
        try {
            url = new URL(link);
            String file = url.getFile();
            if ("".equals(file)
                    || file.endsWith("/")
                    || file.endsWith("/index")
                    || StringUtils.substringBeforeLast(file, ".").endsWith("index")) {
                return true;
            }
        } catch (MalformedURLException e) {
            if (!link.startsWith("http")) {
                return isIndex(fixUrl(link));
            }
        }

        return false;
    }

    /**
     * 判断链接是否有效
     * @param domNode
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/7/23 13:41
     **/
    protected boolean checkHref(DomNode domNode) {
        String href = Optional.of(domNode)
                .map(DomNode::getAttributes)
                .map(attr -> attr.getNamedItem("href"))
                .map(Node::getNodeValue)
                .orElse(null);

        return StringUtils.isNotBlank(href) && !isIndex(href);
    }

    /**
     * 判断父节点中是否只有一个a标签
     * @param domNode a标签节点
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/7/23 13:40
     **/
    protected boolean checkParent(DomNode domNode) {
        Optional<DomNode> parentNode = Optional.ofNullable(domNode.getParentNode());

        if (parentNode.isPresent()) {
            DomNodeList<DomNode> childNodes = parentNode.get().getChildNodes();
            return childNodes.size() == 1;
        }

        return true;
    }

    /**
     * 判断内容长度是否合格
     * @param domNode 节点
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2019/7/23 13:41
     **/
    protected boolean checkContentLength(DomNode domNode) {
        String content = domNode.getTextContent();
        content = content.replaceAll("[\n\t（）(|) ]", "").trim();
        // 标题长度
        return !StringUtils.isBlank(content) && content.length() >= 6 ;
//                && (StringUtil.isEnglish(content)
//                ? StringUtil.words(content) <= 30
//                : content.replaceAll("\\s", "").length() <=  30);
    }


    /**
     * 修补残缺的url
     * @param href href
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/7/23 11:51
     **/
    protected String fixUrl(String href) {
        if (href.startsWith("http")) {
            return href;
        }
        if (href.startsWith("//")) {
            return "http:" + href;
        }
        if (href.startsWith(".")) {
            String d = url.getFile();
            if (d.contains(".")) {
                d = StringUtils.substringBeforeLast(d.split("\\.")[0], "/");
            }
            String tmp = url.getHost() + (url.getPort() == -1 ? "" : ":") + (url.getPort() == -1 ? "" : url.getPort()) + d + "/" + href.substring(1);
            String u = url.getProtocol() + "://" + tmp.replaceAll("/+", "/");
            return u;
        }
        Matcher matcher = DOMAIN_PATTERN.matcher(href);
        if (matcher.find()) {
            return "http://" + href;
        } else {
            return url.getProtocol() + "://" + (url.getHost() + (url.getPort() == -1 ? "" : ":") + (url.getPort() == -1 ? "" : url.getPort()) + "/" + href).replaceAll("/+", "/");
        }
    }



    protected class TitleLink implements FetchResult {
        String title;
        String url;
        private String domain;
        String topDomain;
        LocalDate date;
        int deep;
        int length;

        /**
         * 计算链接的权重
         * @return int
         * @author Zhu Kaixiao
         * @date 2019/7/24 9:14
         **/
        int getWeight() {
            int weight = 0;
            if (deep != 0) {
                int lenDen = Math.min(Math.max(TitleAutoFetch.this.titleLinks.size() / 15, 1), 3);
                int deepDen = Math.min(Math.max(TitleAutoFetch.this.titleLinks.size() / 10, 1), 5);
                int domainDen = Math.min(Math.max(TitleAutoFetch.this.titleLinks.size() / 5, 1), 8);
                int lenScore = TitleAutoFetch.this.lenCountMap.get(length) / lenDen;
                int deepScore = TitleAutoFetch.this.depCountMap.get(deep) / deepDen;
                int domainScore = TitleAutoFetch.this.domainCountMap.get(topDomain) / domainDen;
                int dateScore = date == null ? 0 : 22;
                if (dateScore != 0 || (lenScore != 0 && deepScore != 0 && domainScore != 0)) {
                    weight = lenScore + deepScore + domainScore + dateScore;
                }
            }
//            System.out.printf("%s\n%s\nWEIGHT: %02d\n", title, url, weight);
            return weight * 4;
        }


        /**
         * 计算链接的权重
         * TODO 加入字体大小得分
         * @return double
         * @author Zhu Kaixiao
         * @date 2019/7/24 9:13
         **/
        double getWeight2() {
            double weight = 0;
            if (deep != 0) {
                double lenScore = TitleAutoFetch.this.lenCountMap.get(length) / (double) TitleAutoFetch.this.titleLinks.size() * 80;
                double deepScore = TitleAutoFetch.this.depCountMap.get(deep) / (double) TitleAutoFetch.this.titleLinks.size() * 100;
                double domainScore = TitleAutoFetch.this.domainCountMap.get(topDomain) / (double) TitleAutoFetch.this.titleLinks.size() * 70;
                double dateScore = date == null ? 0 : 80;
                weight = lenScore + deepScore + domainScore + dateScore;
            }
//            System.out.printf("%s\n%s\nWEIGHT: %02f\n", title, url, weight);
            return weight;
        }


        TitleLink(String title, String url) {
            this.title = title;
            this.url = url;
            init();
        }


        @Override
        public String getContent() {
            return title;
        }

        @Override
        public String getLink() {
            return url;
        }

        private void init() {
            try {
                URL url = new URL(this.url);
                this.length = this.url.length();
                this.domain = url.getHost();
                this.topDomain = fetTopDomain();
                this.deep = url.getFile().split("/").length;
            } catch (MalformedURLException e) {
                this.length = 0;
                this.deep = 0;
                this.domain = "";
                this.topDomain = "";
            }
        }


        private String fetTopDomain() {
            if (domain.length() - domain.replaceAll(".", "").length() > 1) {
                return StringUtils.substringAfter(domain, ".");
            }
            return domain;
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, url);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TitleLink) {
                TitleLink other = (TitleLink) obj;
                return Objects.equals(title, other.title) && Objects.equals(url, other.url);
            }
            return false;
        }
    }


}
