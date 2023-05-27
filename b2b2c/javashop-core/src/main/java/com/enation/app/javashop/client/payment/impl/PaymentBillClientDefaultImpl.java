package com.enation.app.javashop.client.payment.impl;

import com.enation.app.javashop.client.payment.PaymentBillClient;
import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.service.payment.PaymentBillManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 支付账单client java的实现
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/16
 */
@Service
public class PaymentBillClientDefaultImpl implements PaymentBillClient {

    @Autowired
    private PaymentBillManager paymentBillManager;

    @Override
    public PayBill add(PaymentBillDO paymentBill) {
        return paymentBillManager.add(paymentBill);
    }
}
