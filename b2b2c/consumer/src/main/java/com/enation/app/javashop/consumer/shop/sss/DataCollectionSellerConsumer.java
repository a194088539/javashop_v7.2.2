package com.enation.app.javashop.consumer.shop.sss;

import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.statistics.ShopDataClient;
import com.enation.app.javashop.model.statistics.dto.ShopData;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商品收藏更新收集
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-07-23 下午5:50
 */
@Component
public class DataCollectionSellerConsumer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ShopDataClient shopDataClient;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.SELLER_COLLECTION_CHANGE + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.SELLER_COLLECTION_CHANGE, type = ExchangeTypes.FANOUT)
    ))
    public void sellerCollectionChange(ShopData shopData) {
        shopDataClient.updateCollection(shopData);

    }


}
