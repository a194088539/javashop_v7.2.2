package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.core.event.PageCreateEvent;
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
 * 页面生成
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018年3月23日 上午10:31:49
 */
@Component
public class PageCreateReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private List<PageCreateEvent> events;

    /**
     * 页面生成
     *
     * @param choosePages
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.PAGE_CREATE + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.PAGE_CREATE, type = ExchangeTypes.FANOUT)
    ))
    public void createPage(String[] choosePages) {

        logger.info("收到静态页生成消息：" + choosePages);

        if (events != null) {
            for (PageCreateEvent event : events) {
                try {
                    event.createPage(choosePages);
                } catch (Exception e) {
                    logger.error("页面生成出错", e);
                }
            }
        }
    }
}
