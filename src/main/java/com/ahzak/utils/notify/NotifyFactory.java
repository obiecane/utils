package com.ahzak.utils.notify;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/5/31 15:04
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class NotifyFactory {

    private static Map<NotifyEnum, Notify> notifyMap = new HashMap<>();

    static Notify createNotify(NotifyEnum notifyEnum) {
        return getNotifyFromCache(notifyEnum);
    }

    private static Notify getNotifyFromCache(NotifyEnum notifyEnum) {
        return notifyMap.computeIfAbsent(notifyEnum, NotifyFactory::createNotify0);
    }

    private static Notify createNotify0(NotifyEnum notifyEnum) {
        switch (notifyEnum) {
            case SMS:
                return new SmsNotify();
            case EMAIL:
                return new EmailNotify();
            case WEBSITE:
                return new WebSiteNotify();
            default:
                return null;
        }
    }

}
