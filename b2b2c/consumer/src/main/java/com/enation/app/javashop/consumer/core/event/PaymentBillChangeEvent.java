package com.enation.app.javashop.consumer.core.event;

import com.enation.app.javashop.model.base.message.PaymentBillStatusChangeMsg;

/**
 * 支付账单状态变化事件
 *
 * @author fk
 * @version v2.0
 * @since v7.2.1
 * 2020年3月11日 上午9:52:32
 */
public interface PaymentBillChangeEvent {

    /**
     * 支付账单状态改变，需要执行的操作
     *
     * @param paymentBillMessage
     */
    void billChange(PaymentBillStatusChangeMsg paymentBillMessage);
}
