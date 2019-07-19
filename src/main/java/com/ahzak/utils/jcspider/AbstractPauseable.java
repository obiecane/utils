package com.ahzak.utils.jcspider;

import com.ahzak.utils.date.MyDateUtils;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/15 8:44
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class AbstractPauseable implements Pauseable {



    /**
     * 暂停状态
     */
    protected volatile int pauseStatus;


    /**
     * 暂停标志
     * 如果设置为true, 表示需要暂停
     * 如果设置为false, 表示需要恢复运行
     */
    private volatile boolean pauseFlg;

    private List<PauseListener> pauseListeners = new LinkedList<>();

    /**
     * 暂停任务
     *
     * @author Zhu Kaixiao
     * @date 2019/7/12 17:04
     **/
    @Override
    public void pause() {
        log.trace("[{}] pause, pauseFlg[{}]", this.getClass().getSimpleName(), pauseFlg);
        pauseFlg = true;
    }

    /**
     * 恢复任务
     *
     * @author Zhu Kaixiao
     * @date 2019/7/12 17:05
     **/
    @Override
    public void resume() {
        log.trace("[{}] resume, pauseFlg[{}]", this.getClass().getSimpleName(), pauseFlg);
        while (pauseFlg) {
            synchronized (this) {
                this.notify();
            }
        }
    }


    /**
     * 执行任务时检查标志, 判断是否需要暂停
     * @author Zhu Kaixiao
     * @date 2019/7/12 17:05
     **/
    protected void checkPause() {
        log.trace("[{}] begin check pause", this.getClass().getSimpleName());
        if (pauseFlg) {
            try {
                synchronized (this) {
                    for (PauseListener l : pauseListeners) {
                        l.beforePause(this);
                    }
                    // 有可能外部在beforePause回调时调用了cancel(), 所以需要再判断一次
                    if (pauseFlg) {
                        log.trace("[{}] begin wait", this.getClass().getSimpleName());
                        this.wait();
                        pauseFlg = false;
                    }
                    for (PauseListener l : pauseListeners) {
                        l.afterResume(this);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取消暂停, 当设置了暂停标志, 但是还没到暂停状态时, 可以取消暂停
     * PS: 如果已经暂停了, 再调用该方法, 将会导致任务无法恢复
     * @author Zhu Kaixiao
     * @date 2019/7/15 11:02
     **/
    public void cancel() {
        log.trace("[{}] cancel pause, pauseFlg[{}]", this.getClass().getSimpleName(), pauseFlg);
        pauseFlg = false;
    }


    public void addPauseListener(PauseListener pauseListener) {
        this.pauseListeners.add(pauseListener);
    }



    protected String pageLog(String loginUser, Page page) {
        String pageLog = loginUser + "___"
                + MyDateUtils.formatDate(new Date(), "MM/dd hh:mm:ss") + ": 开始请求  " + page.getUrl();
        log.trace(pageLog);
        return pageLog;
    }


    protected void randomSleep(int maxSec) {
        Random random = new Random();
        int sec = random.nextInt(maxSec) + 1;
        try {
            TimeUnit.SECONDS.sleep(sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void randomSleep() {
        randomSleep(60);
    }
}
