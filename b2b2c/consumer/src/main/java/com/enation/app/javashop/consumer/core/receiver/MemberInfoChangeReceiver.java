package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.core.event.MemberInfoChangeEvent;
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

import java.util.List;

/**
 * 修改会员资料
 *
 * @author zh
 * @version v7.0
 * @date 18/12/26 下午4:17
 * @since v7.0
 */
@Component
public class MemberInfoChangeReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<MemberInfoChangeEvent> events;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.MEMBER_INFO_CHANGE + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.MEMBER_INFO_CHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void createIndexPage(Long memberId) {
        if (events != null) {
            for (MemberInfoChangeEvent event : events) {
                try {
                    event.memberInfoChange(memberId);
                } catch (Exception e) {
                    logger.error("处理会员资料变化消息出错", e);
                }
            }
        }

    }
}
