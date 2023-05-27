package com.enation.app.javashop.consumer.shop.trade.consumer;

import com.enation.app.javashop.client.payment.PaymentClient;
import com.enation.app.javashop.client.payment.PayLogClient;
import com.enation.app.javashop.consumer.core.event.ASNewOrderEvent;
import com.enation.app.javashop.consumer.core.event.OrderStatusChangeEvent;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.model.base.message.OrderStatusChangeMsg;
import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.PayLog;
import com.enation.app.javashop.model.trade.order.enums.OrderStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.PayStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单支付后，修改付款单
 *
 * @author Snow create in 2018/7/23
 * @version v2.0
 * @since v7.0.0
 */
@Component
public class OrderPayLogConsumer implements OrderStatusChangeEvent, ASNewOrderEvent {

    @Autowired
    private PayLogClient payLogClient;

    @Autowired
    private PaymentClient paymentClient;

    @Override
    public void orderChange(OrderStatusChangeMsg orderMessage) {

        //订单已付款
        if (orderMessage.getNewStatus().name().equals(OrderStatusEnum.PAID_OFF.name())) {
            final Long ONE = -1L;
            //获取订单信息
            OrderDO orderDO = orderMessage.getOrderDO();
            //获取收款单信息
            PayLog payLog = this.payLogClient.getModel(orderDO.getSn());
            //如果支付ID不为空，那么就是进行过支付了
            if (orderDO.getPaymentPluginId() != null) {
                //如果PaymentMethodId 不为-1 ，那么就是正常的支付方式
                if (!ONE.equals(orderDO.getPaymentMethodId())) {
                    // 查询支付方式
                    PaymentMethodDO paymentMethod = this.paymentClient.getByPluginId(orderDO.getPaymentPluginId());
                    //支付的方法名字为空
                    if (paymentMethod == null) {
                        String paymentPluginId = "balancePayPlugin";
                        // 判断是否为预存款支付
                        if (paymentPluginId.equals(orderDO.getPaymentPluginId())) {
                            orderDO.setPaymentMethodName("预存款");
                            orderDO.setPayMoney(orderDO.getBalance());
                        }
                    } else {
                        //支付的方法名字不为空，就是有微信或支付宝等在线支付了
                        if (orderDO.getBalance() != null && orderDO.getBalance() > 0) {
                            orderDO.setPayMoney(CurrencyUtil.add(orderDO.getPayMoney(), orderDO.getBalance()));
                            orderDO.setPaymentMethodName("预存款" + orderDO.getPaymentMethodName());
                            //如果不是混合支付，直接跳过，最后读取订单的支付方式就可以了
                        }
                    }
                } else {
                    orderDO.setPaymentMethodName("预存款 + 管理员收款");
                    orderDO.setPayMoney(CurrencyUtil.add(orderDO.getPayMoney(), orderDO.getBalance()));
                }
            } else {
                orderDO.setPaymentMethodName("管理员收款");
            }
            payLog.setPayMoney(orderDO.getPayMoney());
            payLog.setPayType(orderDO.getPaymentMethodName());
            payLog.setPayWay(orderDO.getPaymentType());
            payLog.setPayTime(orderDO.getPaymentTime());
            payLog.setPayStatus(PayStatusEnum.PAY_YES.name());
            payLog.setPayOrderNo(orderDO.getPayOrderNo());
            this.payLogClient.edit(payLog, payLog.getPayLogId());
        }
    }
}
