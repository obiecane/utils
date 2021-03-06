package com.ahzak.utils.jcspider;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/19 11:23
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Data
@EqualsAndHashCode
public class FreeProxy {

    private String host;
    private int port;
    private long weight;
}
