package com.enation.app.javashop.framework.rabbitmq;

/**
 * mq的publisher
 *
 * @author fk
 * @version v7.2.0
 * @since v7.2.0
 * 2020-06-15 21:50:52
 */
public interface MessageSender {


    /**
     * 发布
     * @param message
     */
    void send(MqMessage message);

}
