package com.ahzak.utils.notify;


import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 通知工具类
 * 包含短信通知, 邮件通知, 站内信通知的快速调用方法
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/19 10:26
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public abstract class NotifyUtil {


    /**
     * @author Zhu Kaixiao
     * @date 2020/5/31 16:23
     * @see NotifyUtil#sendNotify(NotifyEnum, String, Map, Collection)
     */
    public static void sendNotify(NotifyEnum notifyEnum, String templateSymbol, Map<String, String> vals, String target) {
        sendNotify(notifyEnum, templateSymbol, vals, Collections.singletonList(target));
    }


    /**
     * 根据通知类型发送通知
     *
     * @param notifyEnum     通知类型
     * @param templateSymbol 模板标识
     * @param vals           模板变量值
     * @param targetes       发送目标
     * @author Zhu Kaixiao
     * @date 2020/5/31 16:23
     */
    public static void sendNotify(NotifyEnum notifyEnum, String templateSymbol, Map<String, String> vals, Collection<String> targetes) {
        switch (notifyEnum) {
            case SMS:
                sendSmsNotify(templateSymbol, vals, targetes);
            case EMAIL:
                sendEmailNotify(templateSymbol, vals, targetes);
            case WEBSITE:
                sendWebSiteNotify(templateSymbol, vals, targetes);
            default:
                // do nothing
        }
    }

    /**
     * @author Zhu Kaixiao
     * @date 2020/5/31 16:24
     * @see NotifyUtil#sendSmsNotify(String, Map, Collection)
     */
    public static void sendSmsNotify(String templateSymbol, Map<String, String> vals, String phone) {
        sendSmsNotify(templateSymbol, vals, Collections.singletonList(phone));
    }

    /**
     * 发送短信通知
     *
     * @param templateSymbol 模板标识
     * @param vals           模板变量值
     * @param phones         收信手机号
     * @author Zhu Kaixiao
     * @date 2020/5/31 16:25
     */
    public static void sendSmsNotify(String templateSymbol, Map<String, String> vals, Collection<String> phones) {
        Notify notify = NotifyFactory.createNotify(NotifyEnum.SMS);
        notify.send(templateSymbol, vals, phones);
    }

    /**
     * @author Zhu Kaixiao
     * @date 2020/5/31 16:25
     * @see NotifyUtil#sendEmailNotify(String, Map, Collection)
     */
    public static void sendEmailNotify(String templateSymbol, Map<String, String> vals, String emailAddress) {
        sendEmailNotify(templateSymbol, vals, Collections.singletonList(emailAddress));
    }

    /**
     * 发送邮件通知
     *
     * @param templateSymbol 模板标识
     * @param vals           模板变量值
     * @param emailAddresses 收信邮箱地址
     * @author Zhu Kaixiao
     * @date 2020/5/31 16:25
     */
    public static void sendEmailNotify(String templateSymbol, Map<String, String> vals, Collection<String> emailAddresses) {
        Notify notify = NotifyFactory.createNotify(NotifyEnum.EMAIL);
        notify.send(templateSymbol, vals, emailAddresses);
    }


    /**
     * @author Zhu Kaixiao
     * @date 2020/5/31 16:27
     * @see NotifyUtil#sendWebSiteNotify(String, Map, Collection)
     */
    public static void sendWebSiteNotify(String templateSymbol, Map<String, String> vals, String target) {
        sendWebSiteNotify(templateSymbol, vals, Collections.singletonList(target));
    }

    /**
     * 发送站内信通知
     *
     * @param templateSymbol 模板标识
     * @param vals           模板变量值
     * @param targetes       收信目标
     * @author Zhu Kaixiao
     * @date 2020/5/31 16:26
     */
    public static void sendWebSiteNotify(String templateSymbol, Map<String, String> vals, Collection<String> targetes) {
        throw new RuntimeException("站内信还没做好");
    }

}
