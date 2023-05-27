package com.enation.app.javashop.framework.trigger.Interface;

/**
 * 延时任务执行器接口
 * @author liushuai
 * @version v1.0
 * @since v7.0
 * 2019/2/13 下午5:32
 * @Description:
 *
 */
public interface TimeTriggerExecuter {


    /**
     * 执行任务
     * @param object 任务参数
     */
    void execute(Object object);

}
