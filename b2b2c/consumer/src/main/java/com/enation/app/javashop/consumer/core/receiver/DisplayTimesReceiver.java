package com.enation.app.javashop.consumer.core.receiver;

import com.enation.app.javashop.model.base.rabbitmq.AmqpExchange;
import com.enation.app.javashop.client.statistics.DisplayTimesClient;
import com.enation.app.javashop.model.statistics.dos.GoodsPageView;
import com.enation.app.javashop.model.statistics.dos.ShopPageView;
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
 * 流量统计amqp 消费
 *
 * @author Chopper
 * @version v1.0
 * @since v7.0
 * 2018-08-07 下午4:34
 */
@Component
public class DisplayTimesReceiver {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DisplayTimesClient displayTimesClient;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.SHOP_VIEW_COUNT + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.SHOP_VIEW_COUNT, type = ExchangeTypes.FANOUT)
    ))
    public void viewShop(List<ShopPageView> shopPageViews) {
        try {

            displayTimesClient.countShop(shopPageViews);
        } catch (Exception e) {
            logger.error("流量整理：店铺 异常", e);
        }
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = AmqpExchange.GOODS_VIEW_COUNT + "_QUEUE"),
            exchange = @Exchange(value = AmqpExchange.GOODS_VIEW_COUNT, type = ExchangeTypes.FANOUT)
    ))
    public void viewGoods(List<GoodsPageView> goodsPageViews) {
        try {
            displayTimesClient.countGoods(goodsPageViews);
        } catch (Exception e) {
            logger.error("流量整理：商品 异常", e);
        }
    }


}
