package com.ahzak.utils.notify;

import java.util.Collection;
import java.util.Map;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/5/31 14:31
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
interface Notify {

    void send(String templateSymbol, Map<String, String> vals, Collection<String> phones);
}
