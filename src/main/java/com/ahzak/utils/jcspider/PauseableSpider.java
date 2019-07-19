package com.ahzak.utils.jcspider;

import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/12 17:09
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class PauseableSpider extends Spider implements Pauseable {

    private static final String PAUSE_CMD = "pause";
    private static final String RESUME_CMD = "resume";


    private int pauseStatus;
    private int id;

    /** 爬虫运行前的前置操作, 默认啥都不干 */
    private Consumer<Void> beforeRun = nil -> {};

    private Queue<String> pauseCmdQueue = new ConcurrentLinkedQueue<>();



    protected List<SpiderListener> spiderListeners = new LinkedList<>();
    private PauseListener pauseListener = new PauseListener() {
        @Override
        public void beforePause(Pauseable pauseable) {
            pauseStatus = PAUSEING;
            synchronized (this) {
                if (PauseableSpider.this.pageProcessor != pauseable) {
                    ((AbstractPauseable) PauseableSpider.this.pageProcessor).cancel();
                }
                for (Pipeline p : PauseableSpider.this.pipelines) {
                    if (p != pauseable && p instanceof AbstractPauseable) {
                        // 重置p的pauseFlg
                        ((AbstractPauseable) p).cancel();
                    }
                }
            }

            // 执行队列中堆积的命令
            String cmd;
            while ((cmd = pauseCmdQueue.poll()) != null) {
                if (RESUME_CMD.equals(cmd)) {
                    // 如果下一条命令是恢复, 那么直接取消本次暂停
                    ((AbstractPauseable)pauseable).cancel();
                    break;
                } else if (PAUSE_CMD.equals(cmd)) {
                    continue;
                }
            }
        }

        @Override
        public void afterResume(Pauseable pauseable) {
            pauseStatus = RUNNING;

            String cmd;
            while ((cmd = pauseCmdQueue.poll()) != null) {
                if (RESUME_CMD.equals(cmd)) {
                    continue;
                } else if (PAUSE_CMD.equals(cmd)) {
                    doPause();
                    break;
                }
            }
        }
    };


    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor pageProcessor
     */
    public PauseableSpider(int id, JcPageProcessor pageProcessor) {
        super(pageProcessor);
        super.setSpiderListeners(spiderListeners);
        pageProcessor.addPauseListener(pauseListener);
        this.id = id;
    }

    public static PauseableSpider create(int id, JcPageProcessor pageProcessor) {
        return new PauseableSpider(id, pageProcessor);
    }

    @Override
    public Spider addPipeline(Pipeline pipeline) {
        if (pipeline instanceof JcPipeline) {
            ((JcPipeline) pipeline).addPauseListener(pauseListener);
            return super.addPipeline(pipeline);
        }
        throw new IllegalArgumentException("只接受PauseablePipeline类型");
    }

    @Override
    public Spider setPipelines(List<Pipeline> pipelines) {
        for (Pipeline p : pipelines) {
            if (p instanceof JcPipeline) {
                ((JcPipeline) p).addPauseListener(pauseListener);
            } else {
                throw new IllegalArgumentException("只接受PauseablePipeline类型");
            }
        }
        return super.setPipelines(pipelines);
    }


    @Override
    public Spider setSpiderListeners(List<SpiderListener> spiderListeners) {
        this.spiderListeners = spiderListeners;
        return super.setSpiderListeners(spiderListeners);
    }


    public PauseableSpider addSpiderListener(SpiderListener spiderListener) {
        this.spiderListeners.add(spiderListener);
        return this;
    }


    public PauseableSpider setBefore(Consumer<Void> beforeRun) {
        this.beforeRun = beforeRun;
        return this;
    }


    @Override
    public void run() {
        beforeRun.accept(null);
        pauseStatus = RUNNING;
        super.run();
        for (SpiderListener listener : spiderListeners) {
            if (listener instanceof JcSpiderListener) {
                ((JcSpiderListener) listener).onComplete(this);
            }
        }
    }

    @Override
    public synchronized void pause() {
        // 如果蜘蛛已设置恢复但是还没到运行中, 把暂停命令加入队列
        // 否则立即暂停
        if (pauseStatus == SET_RESUME) {
            pauseCmdQueue.add(PAUSE_CMD);
        } else {
            doPause();
        }
        log.info("暂停任务...");
    }

    @Override
    public synchronized void resume() {
        // 如果蜘蛛已设置暂停但是还没到暂停中, 把恢复命令加入队列
        // 否则立即恢复
        if (pauseStatus == SET_PAUSE) {
            pauseCmdQueue.add(RESUME_CMD);
        } else {
            doResume();
        }
        log.info("继续任务...");
    }


    private void doResume() {
        if (pauseStatus == RUNNING || pauseStatus == SET_RESUME) {
            return;
        }
        pauseStatus = SET_RESUME;
        for (Pipeline p : pipelines) {
            if (p instanceof Pauseable) {
                ((Pauseable) p).resume();
            }
        }
        if (this.pageProcessor instanceof Pauseable) {
            ((Pauseable) this.pageProcessor).resume();
        }
    }

    private void doPause() {
        if (pauseStatus == PAUSEING || pauseStatus == SET_PAUSE) {
            return;
        }
        pauseStatus = SET_PAUSE;
        if (this.pageProcessor instanceof Pauseable) {
            ((Pauseable) this.pageProcessor).pause();
        }
        for (Pipeline p : pipelines) {
            if (p instanceof Pauseable) {
                ((Pauseable) p).pause();
            }
        }
    }

    public int getId() {
        return id;
    }
}
