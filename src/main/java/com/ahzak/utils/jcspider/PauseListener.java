package com.ahzak.utils.jcspider;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/15 8:42
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public interface PauseListener {
    /**
     * 暂停前触发
     */
    void beforePause(Pauseable pauseable);

    /**
     * 恢复后触发
     */
    void afterResume(Pauseable pauseable);
}
