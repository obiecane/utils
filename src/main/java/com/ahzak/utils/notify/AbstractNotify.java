package com.ahzak.utils.notify;

import com.ahzak.utils.Assert;
import com.ahzak.utils.TextTemplate;
import com.ahzak.utils.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/5/31 14:35
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
abstract class AbstractNotify implements Notify {

    @Override
    public void send(String templateSymbol, Map<String, String> vals, Collection<String> targets) {
        Assert.notBlank(templateSymbol, "模板标识不能为空字符串");
        Assert.notEmpty(targets, "通知目标不能为空");

        // 1. 获取消息模板
        IMessageTemplate messageTemplate = getBySymbol(templateSymbol);
        if (messageTemplate == null) {
            throw new GlobalException("消息模板不存在: symbol: [{}]", templateSymbol);
        }
        String template = fetchTemplateFromIMessageTemplate(messageTemplate);
        String title = fetchTitleFromIMessageTemplate(messageTemplate);

        // 2. 填充消息模板
        String msg = TextTemplate.mold(template, vals);

        // 3. 去重
        targets = targets.stream().distinct().collect(Collectors.toList());

        // 4. 发信
        log.debug("发送通知: 目标:[{}],  内容:[{}]", targets, msg);
        send(title, msg, targets);
    }

    protected abstract void send(String title, String notifyContent, Collection<String> targets);

    protected IMessageTemplate getBySymbol(String templateSymbol) {
        return MessageTemplateHelper.getBySymbol(templateSymbol);
    }

    protected abstract String fetchTemplateFromIMessageTemplate(IMessageTemplate messageTemplate);

    protected abstract String fetchTitleFromIMessageTemplate(IMessageTemplate messageTemplate);
}
