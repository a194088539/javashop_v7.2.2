package com.enation.app.javashop.client.payment;

import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import com.enation.app.javashop.model.payment.vo.PayBill;

/**
 * 支付账单client
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/16
 */
public interface PaymentBillClient {
    /**
     * 创建一个支付账单
     * @param paymentBill
     * @return
     */
    PayBill add(PaymentBillDO paymentBill);
}
