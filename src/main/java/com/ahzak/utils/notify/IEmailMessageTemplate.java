package com.ahzak.utils.notify;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/5/31 15:13
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public interface IEmailMessageTemplate extends IMessageTemplate {

    /**
     * 获取邮件模板
     */
    String getEmailTemplate();

    String getEmailTitle();
}
