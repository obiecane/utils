package com.ahzak.utils.email;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/28 17:30
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jeemarket.eamil")
public class EmailProperties {

    private String hostName;

    private String username;

    private String password;

    private String fromName;
}
