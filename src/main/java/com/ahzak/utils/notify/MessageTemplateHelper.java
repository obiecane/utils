package com.ahzak.utils.notify;

import com.ahzak.utils.spring.SpringUtil;

import java.util.Collection;
import java.util.Map;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/5/31 14:55
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class MessageTemplateHelper {

    private static volatile Collection<IMessageTemplateFetcher> fetchers;

    static <T extends IMessageTemplate> T getBySymbol(String symbol) {
        if (fetchers == null) {
            synchronized (MessageTemplateHelper.class) {
                if (fetchers == null) {
                    Map<String, IMessageTemplateFetcher> beansOfType = SpringUtil.getBeansOfType(IMessageTemplateFetcher.class);
                    fetchers = beansOfType.values();
                }
            }
        }

        for (IMessageTemplateFetcher fetcher : fetchers) {
            IMessageTemplate template = fetcher.getBySymbol(symbol);
            if (template != null) {
                return (T) template;
            }
        }
        return null;
    }


}
