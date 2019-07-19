package com.ahzak.utils.jcspider;

import com.jeecms.collect.data.service.WebLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.PostConstruct;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/12 17:18
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public abstract class JcPipeline extends AbstractPauseable implements Pipeline {

    @Autowired
    protected WebLogsService webLogsService;
    private static WebLogsService WEBLOGSSERVICE;

    @PostConstruct
    private void initAutowired() {
        WEBLOGSSERVICE = webLogsService;
    }

    protected JcPipeline() {
        webLogsService = WEBLOGSSERVICE;
    }

    /**
     * 处理页面的方法
     * @param resultItems 页面
     * @param task
     * @author Zhu Kaixiao
     * @date 2019/7/16 17:54
     * @deprecated
     * 不要直接重写本方法, 否则{@code beforeProcess()}和{@code afterProcess()}将会失效
     * 去重写{@code beforeProcess(Page page)} 效果是一样的
     **/
    @Override
    @Deprecated
    public void process(ResultItems resultItems, Task task) {
        beforeProcess(resultItems, task);
        doProcess(resultItems, task);
        afterProcess(resultItems, task);
    }

    /**
     * 在正式处理结果之前的操作
     * 默认是检验暂停
     * @param resultItems 结果
     * @param task
     * @author Zhu Kaixiao
     * @date 2019/7/16 18:02
     **/
    protected void beforeProcess(ResultItems resultItems, Task task) {
        checkPause();
    }

    /**
     * 在正式处理页面之后的操作
     * @param resultItems 结果
     * @param task
     * @author Zhu Kaixiao
     * @date 2019/7/16 18:02
     **/
    protected void afterProcess(ResultItems resultItems, Task task) {

    }

    /**
     * 对页面做操作
     * @param resultItems 结果
     * @param task
     * @author Zhu Kaixiao
     * @date 2019/7/16 18:05
     **/
    protected abstract void doProcess(ResultItems resultItems, Task task);
}
