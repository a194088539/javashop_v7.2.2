package com.enation.app.javashop.consumer.core.receiver;

import java.util.List;

import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.member.vo.MemberLoginMsg;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.javashop.consumer.core.event.MemberLoginEvent;

/**
 * 会员登录消费者
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月23日 上午10:31:06
 */
@Component
public class MemberLoginReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<MemberLoginEvent> events;

    /**
     * 会员登录
     *
     * @param memberLoginMsg
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.MEMEBER_LOGIN + "LOGIN_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.MEMEBER_LOGIN, type = ExchangeTypes.FANOUT)
    ))
    public void memberLogin(MemberLoginMsg memberLoginMsg) {

        if (events != null) {
            for (MemberLoginEvent event : events) {
                try {
                    event.memberLogin(memberLoginMsg);
                } catch (Exception e) {
                    logger.error("会员登录出错", e);
                }
            }
        }
    }
}
