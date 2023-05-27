package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.client.trade.OrderClient;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.PaymentTypeEnum;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单状态改变消费
 * 订单付款后修改订单项的可退款金额
 * @author duanmingyu
 * @version v1.0
 * @Description:
 * @since v7.1
 * @date 2019-05-10
 */
@Component
public class OrderCountRefundPriceConsumer implements OrderStatusChangeEvent {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderClient orderClient;

    @Override
    public void orderChange(OrderStatusChangeMsg orderStatusChangeMsg) {
        try {

            OrderDO order = orderStatusChangeMsg.getOrderDO();
            String paymentType = order.getPaymentType();
            OrderStatusEnum orderStatus = orderStatusChangeMsg.getNewStatus();
            //在线支付&&订单已支付
            boolean online = PaymentTypeEnum.ONLINE.value().equals(paymentType) && OrderStatusEnum.PAID_OFF.equals(orderStatus);
            //货到付款&&订单已收货
            boolean cod = PaymentTypeEnum.COD.value().equals(paymentType) && OrderStatusEnum.ROG.equals(orderStatus);
            //在线支付&&订单已支付 或者  货到付款&&订单已收货
            if (online || cod) {
                this.orderClient.addOrderItemRefundPrice(orderStatusChangeMsg.getOrderDO());
            }
        } catch (Exception e) {
            logger.error("订单变更消息异常:",e);
            e.printStackTrace();
        }
    }
}
