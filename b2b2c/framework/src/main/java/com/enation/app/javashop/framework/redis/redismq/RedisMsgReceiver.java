package com.enation.app.javashop.framework.redis.redismq;

/**
 * 敏感词消息接收接口
 *
 * @author fk
 * @version 2.0
 * @since 7.1.5
 * 2019-09-07 18：00
 */
public interface RedisMsgReceiver {

    /**
     * 管道名称
     * @return
     */
    String getChannelName();

    /**
     * 收到消息执行的方法
     * @param message
     */
    void receiveMsg(String message);

}
