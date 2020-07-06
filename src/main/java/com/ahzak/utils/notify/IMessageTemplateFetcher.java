package com.ahzak.utils.notify;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/19 10:34
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public interface IMessageTemplateFetcher {

    /**
     * 根据模板唯一标识获取模板
     *
     * @param symbol 模板标识
     * @return com.jeecms.market.utils.notify.IMessageTemplate
     * @author Zhu Kaixiao
     * @date 2020/6/19 11:23
     */
    IMessageTemplate getBySymbol(String symbol);

}
