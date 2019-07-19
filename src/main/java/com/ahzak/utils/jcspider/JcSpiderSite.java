package com.ahzak.utils.jcspider;

import us.codecraft.webmagic.Site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 爬虫Site配置对象
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/17 9:07
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class JcSpiderSite extends Site {

    private List<String> userAgentList = Collections.emptyList();

    @Override
    public Site setUserAgent(String userAgent) {
        userAgentList = Collections.singletonList(userAgent);
        return this;
    }

    public JcSpiderSite setUserAgentList(List<String> userAgentList) {
        this.userAgentList = new ArrayList<>(userAgentList);
        return this;
    }


    /**
     * 重写原来的 getUserAgent(), 实现每次调用随机返回UA
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/7/17 9:25
     **/
    @Override
    public String getUserAgent() {
        if (userAgentList.isEmpty()) {
            return null;
        }

        // 使用随机的ua
        Random random = new Random();
        String ua = this.userAgentList.get(random.nextInt(this.userAgentList.size()));
        return ua;
    }


    public static JcSpiderSite me() {
        return new JcSpiderSite();
    }
}
