package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.core.event.ShopStatusChangeEvent;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.model.base.message.ShopStatusChangeMsg;
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
 * 店铺状态变更消费者
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/9/9 下午11:06
 */
@Component
public class ShopStatusChangeReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<ShopStatusChangeEvent> events;

    /**
     * 店铺状态变更
     *
     * @param shopStatusChangeMsg
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.CLOSE_STORE + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.CLOSE_STORE, type = ExchangeTypes.FANOUT)
    ))
    public void changeStatus(ShopStatusChangeMsg shopStatusChangeMsg) {

        if (events != null) {
            for (ShopStatusChangeEvent event : events) {
                try {
                    event.changeStatus(shopStatusChangeMsg);
                } catch (Exception e) {
                    logger.error("店铺状态变更消息出错", e);
                }
            }
        }

    }

}
