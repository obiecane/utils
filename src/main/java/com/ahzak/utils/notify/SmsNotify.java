package com.ahzak.utils.notify;

import java.util.Collection;

/**
 * 短信通知
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/5/31 14:31
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class SmsNotify extends AbstractNotify {

    @Override
    public void send(String title, String notifyContent, Collection<String> phones) {
//        SmsUtil.send(phones, notifyContent);
    }


    @Override
    protected String fetchTemplateFromIMessageTemplate(IMessageTemplate messageTemplate) {
        if (messageTemplate instanceof ISmsMessageTemplate) {
            return ((ISmsMessageTemplate) messageTemplate).getSmsTemplate();
        } else {
            throw new IllegalArgumentException("不支持的模板类型");
        }
    }

    @Override
    protected String fetchTitleFromIMessageTemplate(IMessageTemplate messageTemplate) {
        return "";
    }
}
