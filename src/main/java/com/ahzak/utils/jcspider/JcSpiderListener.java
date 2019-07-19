package com.ahzak.utils.jcspider;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/12 17:51
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public interface JcSpiderListener extends us.codecraft.webmagic.SpiderListener {

    /**
     * 爬行完成时触发
     * @author Zhu Kaixiao
     * @date 2019/7/15 13:48
     **/
    void onComplete(PauseableSpider spider);
}
