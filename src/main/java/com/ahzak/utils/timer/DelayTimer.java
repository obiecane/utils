package com.ahzak.utils.timer;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * 延时任务执行器
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/11/19 17:14
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class DelayTimer {

    private Dial dial = new Dial();
    private ExecutorService executorService;
    private ScheduledExecutorService scheduledExecutorService;

    {
        executorService = new ThreadPoolExecutor(16, 256, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1024), new BasicThreadFactory.Builder().namingPattern("delayTimer-execute-pool-%d").daemon(true).build(),
                new ThreadPoolExecutor.AbortPolicy());

        scheduledExecutorService = Executors.newScheduledThreadPool(3,
                new BasicThreadFactory.Builder().namingPattern("delayTimer-scheduled-pool-%d").daemon(true).build());

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            System.out.println(LocalDateTime.now());
            ++dial.currentIndex;
            if (dial.currentIndex == 3600) {
                dial.currentIndex = 0;
            }
            Queue<DelayTask> delayTasks = dial.taskSlot[dial.currentIndex];
            Iterator<DelayTask> iterator = delayTasks.iterator();
            while (iterator.hasNext()) {
                DelayTask delayTask = iterator.next();
                if (delayTask.cycleNum == 0) {
                    executorService.submit(delayTask.getRunnable());
                    iterator.remove();
                } else {
                    --delayTask.cycleNum;
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }


    private static class Dial {
        final Queue<DelayTask>[] taskSlot;
        volatile int currentIndex;

        Dial() {
            taskSlot = new ConcurrentLinkedQueue[3600];
            for (int i = 0; i < 3600; i++) {
                taskSlot[i] = new ConcurrentLinkedQueue<>();
            }
        }
    }


    void addDelayTask(DelayTask delayTask) {
        DelayTask clone = new DelayTask(delayTask.getDelay(), delayTask.getRunnable());
        int slot = (int) (clone.getDelay() % 3600 + dial.currentIndex);
        dial.taskSlot[slot].add(clone);
    }

}
