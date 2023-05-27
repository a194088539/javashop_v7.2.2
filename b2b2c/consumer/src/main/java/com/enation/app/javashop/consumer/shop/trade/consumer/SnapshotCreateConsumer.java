package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.client.trade.OrderOperateClient;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生成商品交易快照
 * @author Snow create in 2018/5/22
 * @version v2.0
 * @since v7.0.0
 */
@Component
public class SnapshotCreateConsumer implements OrderStatusChangeEvent {

    @Autowired
    private OrderOperateClient orderOperateClient;

    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {

        if(orderMessage.getNewStatus().equals(OrderStatusEnum.NEW)){
            OrderDO orderDO = orderMessage.getOrderDO();

            this.orderOperateClient.addGoodsSnapshot(orderDO);
        }
    }


}
