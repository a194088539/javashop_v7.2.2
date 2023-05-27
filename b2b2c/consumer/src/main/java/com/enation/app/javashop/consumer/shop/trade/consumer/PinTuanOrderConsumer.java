package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.client.trade.PintuanOrderClient;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kingapex on 2019-01-25.
 * 拼团订单消费者<br/>
 * 如果是拼团订单，检测相应的拼团活动是否已经参团成功<br/>
 * 如果成功，要更新相应数据
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-01-25
 */
@Component
public class PinTuanOrderConsumer implements OrderStatusChangeEvent {

    @Autowired
    private PintuanOrderClient pintuanOrderClient;

    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {

        //对已付款的订单
        if (orderMessage.getNewStatus().equals(OrderStatusEnum.PAID_OFF)) {
            OrderDO orderDO = orderMessage.getOrderDO();
            if (orderDO.getOrderType().equals(OrderTypeEnum.PINTUAN.name())) {
                pintuanOrderClient.payOrder(orderDO);
            }

        }

    }
}
