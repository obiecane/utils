package com.ahzak.utils.timer;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/11/19 17:55
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class DelayTaskUtil {

    private static DelayTimer delayTimer = new DelayTimer();

    private DelayTaskUtil() {

    }


    public static void addDelayTask(DelayTask delayTask) {
        delayTimer.addDelayTask(delayTask);
    }


    public static void main(String[] args) throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
            DelayTaskUtil.addDelayTask(new DelayTask((long) (Math.random() * 100), () -> {
                long currentTimeMillis = System.currentTimeMillis();
                System.out.println(currentTimeMillis);
            }));
        }


    }

}
