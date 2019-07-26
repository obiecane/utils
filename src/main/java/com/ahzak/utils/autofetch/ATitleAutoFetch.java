package com.ahzak.utils.autofetch;

import com.ahzak.utils.jcspider.JcPage;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 自动识别标题
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/24 15:08
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class ATitleAutoFetch extends TitleAutoFetch {

    @Override
    public List<FetchResult> fetch(JcPage page) {
        initPageAttr(page);
        // 1. 带有链接的a标签, 且其中的文字字体大于xxPX, display不为none, 长度不超过30字
        // 2. 非图片标签中匹配url, 文字大小
        HtmlPage htmlPage = page.getHtmlPage();
        DomNodeList<DomNode> aList = htmlPage.querySelectorAll("a[href]");

        for (DomNode node : aList) {
            if (checkTitle(node)) {
                String titleContent = node.getTextContent().replaceAll("[\n\t ]", "").trim();
                String titleHref = fixUrl(node.getAttributes().getNamedItem("href").getNodeValue());
                TitleLink titleLink = new TitleLink(titleContent, titleHref);
                if (titleLinks.contains(titleLink) || titleHref.contains("javascript:") || titleHref.startsWith("http://mailto:")) {
                    continue;
                }
                titleLinks.add(titleLink);
                lenCountMap.put(titleLink.length, lenCountMap.getOrDefault(titleLink.length, 0) + 1);
                depCountMap.put(titleLink.deep, depCountMap.getOrDefault(titleLink.deep, 0) + 1);
                domainCountMap.put(titleLink.topDomain, domainCountMap.getOrDefault(titleLink.topDomain, 0) + 1);
                Matcher matcher;
                while ((matcher = DATE_PATTERN_1.matcher(titleHref)).find()) {
                    try {
                        String y = "20" + matcher.group(1);
                        y = y.substring(y.length() - 4);
                        String m = "0" + matcher.group(3);
                        m = m.substring(m.length() - 2);
                        String d = "0" + matcher.group(4);
                        d = d.substring(d.length() - 2);
                        titleLink.date = LocalDate.parse(y + "-" + m + "-" + d, DATE_FMT_1);
                    } catch (Exception ignore) { }

                    if (titleLink.date == null) {
                        titleHref = titleHref.substring(matcher.start() + 1);
                    } else {
                        break;
                    }
                }
            }
        }


        int count = 0;
        List<FetchResult> list = new LinkedList<>();
        List<TitleLink> badList = new LinkedList<>();
        for (TitleLink titleLink : titleLinks) {
            if (titleLink.getWeight2() >= 100) {
                list.add(titleLink);
                System.out.println(titleLink.title);
                System.out.println(titleLink.url);
                ++count;
            } else {
                badList.add(titleLink);
            }
        }

        System.out.println("分数不合格:");
        for (TitleLink titleLink : badList) {
            System.out.println(titleLink.title);
            System.out.println(titleLink.url);
        }

        System.out.println();
        System.out.println("a标签总数:  " + aList.size());
        System.out.println("预备标题总数:  " + titleLinks.size());
        System.out.println("分数不合格总数:  " + badList.size());
        System.out.println("分数合格总数:  " + count);

        return list;
    }

}
