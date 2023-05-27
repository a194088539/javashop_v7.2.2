package com.enation.app.javashop.consumer.shop.payment;

import com.enation.app.javashop.client.trade.OrderOperateClient;
import com.enation.app.javashop.consumer.core.event.PaymentBillChangeEvent;
import com.enation.app.javashop.model.base.message.PaymentBillStatusChangeMsg;
import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单支付状态改变消费值
 *
 * @author fk
 * @version v2.0
 * @since v7.2.1
 * 2020年3月11日 上午9:52:13
 */
@Service
public class OrderPayStatusConsumer implements PaymentBillChangeEvent {

    @Autowired
    private OrderOperateClient orderOperateClient;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void billChange(PaymentBillStatusChangeMsg paymentBillMessage) {

        // 支付成功
        if (PaymentBillStatusChangeMsg.SUCCESS.equals(paymentBillMessage.getStatus())) {
            PaymentBillDO bill = paymentBillMessage.getBill();
            String tradeType = bill.getServiceType();

            orderOperateClient.paySuccess(tradeType,bill.getSubSn(), bill.getReturnTradeNo(), paymentBillMessage.getPayPrice());

            logger.debug("支付更改成功");
        }


    }

}
