package com.ahzak.utils.notify;

import java.util.Collection;

/**
 * 站内信通知
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/5/31 15:26
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class WebSiteNotify extends AbstractNotify {
    @Override
    protected void send(String title, String notifyContent, Collection<String> phones) {

    }

    @Override
    protected String fetchTemplateFromIMessageTemplate(IMessageTemplate messageTemplate) {
        return null;
    }

    @Override
    protected String fetchTitleFromIMessageTemplate(IMessageTemplate messageTemplate) {
        return null;
    }
}
