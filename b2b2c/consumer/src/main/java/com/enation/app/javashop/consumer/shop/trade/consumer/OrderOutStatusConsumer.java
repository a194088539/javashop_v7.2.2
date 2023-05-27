package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.client.trade.OrderOperateClient;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderOutStatus;
import com.enation.app.javashop.model.trade.order.enums.OrderOutStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderOutTypeEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 创建订单出库状态设置消费者
 * @author duanmingyu
 * @version 1.0
 * @since 7.2.1
 * @date 2020-07-09
 */
@Component
@Order(1)
public class OrderOutStatusConsumer implements OrderStatusChangeEvent {

    @Autowired
    private OrderOperateClient orderOperateClient;

    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {
        OrderDO orderDO = orderMessage.getOrderDO();
        //如果订单状态为新订单
        if (OrderStatusEnum.NEW.value().equals(orderDO.getOrderStatus())) {
            for (String type : OrderOutTypeEnum.getAll()) {
                OrderOutStatus orderOutStatus = new OrderOutStatus();
                orderOutStatus.setOrderSn(orderDO.getSn());
                orderOutStatus.setOutType(type);
                orderOutStatus.setOutStatus(OrderOutStatusEnum.WAIT.name());
                this.orderOperateClient.add(orderOutStatus);
            }
        }
    }
}
