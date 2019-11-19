package com.ahzak.utils.timer;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/11/19 17:33
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class DelayTask {

    /**
     * 延时时长 单位秒
     */
    @Getter
    private long delay;

    long cycleNum;

    /**
     * 需要执行的方法
     */
    @Getter
    @Setter
    private Runnable runnable;

    public DelayTask(long delay, Runnable runnable) {
        setDelay(delay);
        setRunnable(runnable);
    }

    /**
     * 如果在该任务对象被加入到延时任务执行列表中后，再调用setDelay方法， 将会导致延时错乱
     * @param delay 延时时长 单位：秒
     */
    public void setDelay(long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("延时时长不能小于0");
        }
        this.delay = delay;
        cycleNum = delay / 3600;
    }
}
