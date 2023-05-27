package com.enation.app.javashop.client.payment.impl;

import com.enation.app.javashop.client.payment.PayLogClient;
import com.enation.app.javashop.model.trade.order.dos.PayLog;
import com.enation.app.javashop.service.payment.PayLogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单操作SDK
 *
 * @author fk create in 2020/4/7
 * @version v2.0
 * @since v7.2.0
 */
@Service
public class PayLogClientImpl implements PayLogClient {

    @Autowired
    private PayLogManager payLogManager;

    @Override
    public PayLog add(PayLog payLog) {
        return payLogManager.add(payLog);
    }

    @Override
    public PayLog edit(PayLog payLog, Long id) {
        return payLogManager.edit(payLog, id);
    }

    @Override
    public PayLog getModel(String orderSn) {
        return payLogManager.getModel(orderSn);
    }
}
