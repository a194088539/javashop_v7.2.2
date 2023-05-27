package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.core.event.MemberAskSendMessageEvent;
import com.enation.app.javashop.model.base.message.MemberAskMessage;
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
 * 会员商品咨询消息接收者
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-09-16
 */
@Component
public class MemberAskSendMessageReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<MemberAskSendMessageEvent> events;

    /**
     * 会员商品咨询
     *
     * @param memberAskMessage
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.MEMBER_GOODS_ASK + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.MEMBER_GOODS_ASK, type = ExchangeTypes.FANOUT)
    ))
    public void goodsAsk(MemberAskMessage memberAskMessage) {

        if (events != null) {
            for (MemberAskSendMessageEvent event : events) {
                try {
                    event.goodsAsk(memberAskMessage);
                } catch (Exception e) {
                    logger.error("处理会员商品咨询出错",e);
                }
            }
        }

    }
}
