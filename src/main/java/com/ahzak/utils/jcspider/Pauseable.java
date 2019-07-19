package com.ahzak.utils.jcspider;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/12 17:18
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public interface Pauseable {

    /** 暂停中 */
    int PAUSEING = 1;

    /** 已设置暂停, 但是还没真正停下来 */
    int SET_PAUSE = 2;

    /** 运行中 */
    int RUNNING = 3;

    /** 已设置恢复, 但是还没真正恢复运行 */
    int SET_RESUME = 4;


    void pause();

    void resume();
}
