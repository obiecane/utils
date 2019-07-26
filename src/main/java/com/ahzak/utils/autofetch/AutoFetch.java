package com.ahzak.utils.autofetch;

import com.ahzak.utils.jcspider.JcPage;

import java.util.List;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/24 14:50
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public interface AutoFetch {


    List<FetchResult> fetch(JcPage page);

}
