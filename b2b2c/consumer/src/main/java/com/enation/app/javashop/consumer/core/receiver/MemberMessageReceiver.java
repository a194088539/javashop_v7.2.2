package com.enation.app.javashop.consumer.core.receiver;

import java.util.List;

import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.javashop.consumer.core.event.MemberMessageEvent;

/**
 * 站内消息
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月23日 上午10:31:13
 */
@Component
public class MemberMessageReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<MemberMessageEvent> events;

    /**
     * 站内消息
     *
     * @param messageid
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.MEMBER_MESSAGE + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.MEMBER_MESSAGE, type = ExchangeTypes.FANOUT)
    ))
    public void memberMessage(Long messageid) {

        if (events != null) {
            for (MemberMessageEvent event : events) {
                try {
                    event.memberMessage(messageid);
                } catch (Exception e) {
                    logger.error("站内消息发送出错", e);
                }
            }
        }
    }
}
