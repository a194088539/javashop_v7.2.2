package com.enation.app.javashop.consumer.shop.aftersale;

import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.member.DepositeClient;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 预存款消费者
 * @author: liuyulei
 * @create: 2020-02-24 18:07
 * @version:1.0
 * @since:7.1.4
 **/
@Component
public class DepositeConsumer implements OrderStatusChangeEvent {


    @Autowired
    private DepositeClient depositeClient;


    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {
        if(OrderStatusEnum.CANCELLED.equals(orderMessage.getNewStatus())){
            OrderDO order = orderMessage.getOrderDO();
            //如果使用了预存款支付
            if(order.getBalance() > 0 && PayStatusEnum.PAY_NO.name().equals(order.getPayStatus())){
                depositeClient.increase(order.getBalance(),order.getMemberId(),"商品订单取消，退还预存款,订单号:" + order.getSn());
            }
        }
    }
}
