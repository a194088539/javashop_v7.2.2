package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.consumer.core.event.AskReplyEvent;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import com.enation.app.javashop.model.base.message.AskReplyMessage;
import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 会员商品咨询回复消息接收者
 *
 * @author duanmingyu
 * @version v1.0
 * @since v7.1.5
 * 2019-09-17
 */
@Component
public class TxReceiver {

    /**
     * 会员回复商品咨询
     *
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value =  "tx_QUEUE"),
            exchange = @Exchange(value = "tx", type = ExchangeTypes.FANOUT)
    ))

    public void goodsAsk(String msg) {
         System.out.println("receive "+ msg);
        try {
            ask();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @ShardingTransactionType(TransactionType.XA)
    public void ask() {
        System.out.println("做成一个异常");
        throw new RuntimeException("abc");
    }
}
