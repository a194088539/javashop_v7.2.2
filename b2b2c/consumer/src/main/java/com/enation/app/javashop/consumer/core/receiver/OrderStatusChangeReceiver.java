package com.enation.app.javashop.consumer.core.receiver;

import java.util.List;

import com.enation.app.javashop.consumer.core.event.AfterSaleChangeEvent;
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

import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;

/**
 * 订单状态改变消费者
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月23日 上午10:31:42
 */
@Component
public class OrderStatusChangeReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<OrderStatusChangeEvent> events;

    protected final static int MAX_TIMES_CHANGE = 3;

    /**
     * 订单状态改变
     *
     * @param orderMessage
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.ORDER_STATUS_CHANGE + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.ORDER_STATUS_CHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void orderChange(OrderStatusChangeMsg orderMessage)  throws InterruptedException {
        if (events != null) {
            for (OrderStatusChangeEvent event : events) {
				this.orderChange(event,orderMessage,0);
            }
        }

    }

    /**
     * 启动重试机制
     *
     * @param event
     * @param orderMessage
     * @param times
     * @throws InterruptedException
     */
    private void orderChange(OrderStatusChangeEvent event, OrderStatusChangeMsg orderMessage, Integer times) throws InterruptedException {

        if (times >= MAX_TIMES_CHANGE) {
            logger.error("超过预定次数，不再重试：" + orderMessage);
            return;
        }

        try {
            event.orderChange(orderMessage);
            return;
        } catch (Exception e) {
            // 如果更新失败，则等待1秒重试
            logger.error("处理订单状态变化消息出错,第" + times + "次", e);
            Thread.sleep(1000);
            orderChange(event, orderMessage, ++times);
        }
    }

}
