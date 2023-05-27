package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.core.event.AfterSaleChangeEvent;
import com.enation.app.javashop.model.base.message.AfterSaleChangeMessage;
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
 * 售后服务单状态变化接收者
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-10-24
 */
@Component
public class AfterSaleChangeReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<AfterSaleChangeEvent> events;

    protected final static  int MAX_TIMES_CHANGE = 3;


    /**
     * 处理售后服务单状态变化消息
     * @param afterSaleChangeMessage
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.AS_STATUS_CHANGE + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.AS_STATUS_CHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void afterSaleChange(AfterSaleChangeMessage afterSaleChangeMessage) throws InterruptedException {
        if (events != null) {
            for (AfterSaleChangeEvent event : events) {
                
                this.afterSaleChange(event,afterSaleChangeMessage,0);
            }
        }
    }

    /**
     * 启动重试机制
     * @param afterSaleChangeMessage
     * @param times
     * @param event
     */
    private void afterSaleChange(AfterSaleChangeEvent event,AfterSaleChangeMessage afterSaleChangeMessage,Integer times) throws InterruptedException {

        if (times >= MAX_TIMES_CHANGE) {
            logger.error("超过预定次数，不再重试："+afterSaleChangeMessage);
            return;
        }

        try {
            event.afterSaleChange(afterSaleChangeMessage);
            return;
        } catch (Exception e) {
            // 如果更新失败，则等待1秒重试
            logger.error("处理售后服务单状态变化消息出错,第"+times+"次",e);
            Thread.sleep(1000);
            afterSaleChange(event,afterSaleChangeMessage,++times);
        }
    }


}
