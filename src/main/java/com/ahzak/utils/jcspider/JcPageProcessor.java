package com.ahzak.utils.jcspider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;


/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/12 17:15
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public abstract class JcPageProcessor extends AbstractPauseable implements PageProcessor {

    protected String loginUser;
    protected Integer collectTaskId;



    /** 抓取网站的相关配置。 */
    private Site site = JcSpiderSite.me();
//            .setUserAgentList(Arrays.asList(CollectTaskContext.USER_AGENTS))//UserAgent
//            .setCharset(CollectTaskContext.CHARSET_DEFAULT_VALUE)//编码
//            .setSleepTime(CollectTaskContext.SLEEP_TIME_MILLISECOND_DEFAULT_NUM)//抓取间隔
//            .setRetryTimes(CollectTaskContext.RETRY_TIMES_DEFAULT_NUM);//重试次数


    @Override
    public Site getSite() {
        return site;
    }

    public JcPageProcessor setLoginUser(String loginUser) {
        this.loginUser = loginUser;
        return this;
    }

    public JcPageProcessor setCollectTaskId(Integer collectTaskId) {
        this.collectTaskId = collectTaskId;
        return this;
    }

    /**
     * 处理页面的方法
     * @param page 页面
     * @author Zhu Kaixiao
     * @date 2019/7/16 17:54
     * @deprecated
     * 不要直接重写本方法, 否则{@code beforeProcess()}和{@code afterProcess()}将会失效
     * 去重写{@code beforeProcess(Page page)} 效果是一样的
     **/
    @Override
    @Deprecated
    public void process(Page page) {
        JcPage jcPage;
        if (page instanceof JcPage) {
            jcPage = (JcPage) page;
        } else {
            jcPage = new JcPage();
            jcPage.setPage(page);
        }
        beforeProcess(jcPage);
        doProcess(jcPage);
        afterProcess(jcPage);
    }


    /**
     * 在正式处理页面之前的操作
     * 默认是检验暂停和输出日志
     * @param page 页面
     * @author Zhu Kaixiao
     * @date 2019/7/16 18:02
     **/
    protected void beforeProcess(JcPage page) {
        checkPause();

    }

    /**
     * 在正式处理页面之后的操作
     * @param page 页面
     * @author Zhu Kaixiao
     * @date 2019/7/16 18:02
     **/
    protected void afterProcess(JcPage page) {

    }

    /**
     * 对页面做操作
     * @param page 页面
     * @author Zhu Kaixiao
     * @date 2019/7/16 18:05
     **/
    protected abstract void doProcess(JcPage page);


}
