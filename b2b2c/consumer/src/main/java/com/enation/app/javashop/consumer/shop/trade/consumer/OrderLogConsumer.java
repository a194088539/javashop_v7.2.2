package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.client.trade.OrderOperateClient;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.framework.util.DateUtil;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.OrderLogDO;
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
@Order(2)
public class OrderLogConsumer implements OrderStatusChangeEvent {

    @Autowired
    private OrderOperateClient orderOperateClient;

    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {
        OrderDO orderDO = orderMessage.getOrderDO();
        //如果订单状态为新订单
        if (OrderStatusEnum.NEW.value().equals(orderDO.getOrderStatus())) {
            //记录日志
            OrderLogDO logDO = new OrderLogDO();
            logDO.setOrderSn(orderDO.getSn());
            logDO.setMessage("创建订单");
            logDO.setOpName(orderDO.getMemberName());
            logDO.setOpTime(DateUtil.getDateline());
            this.orderOperateClient.addOrderLog(logDO);
        }
    }
}
