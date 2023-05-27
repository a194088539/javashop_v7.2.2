package com.enation.app.javashop.client.payment.impl;

import com.enation.app.javashop.client.payment.PaymentClient;
import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;
import com.enation.app.javashop.model.payment.dto.PayParam;
import com.enation.app.javashop.service.payment.PaymentBillManager;
import com.enation.app.javashop.service.payment.PaymentManager;
import com.enation.app.javashop.service.payment.PaymentMethodManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 支付中心client实现
 *
 * @author fk
 * @version v7.0
 * @date 20/3/9 下午3:51
 * @since v7.2.1
 */
@Service
@ConditionalOnProperty(value="javashop.product", havingValue="stand")
public class PaymentClientImpl implements PaymentClient {

    @Autowired
    private PaymentManager paymentManager;

    @Autowired
    private PaymentBillManager paymentBillManager;

    @Autowired
    private PaymentMethodManager paymentMethodManager;

    @Override
    public Map pay(PayParam param) {

        return paymentManager.pay(param);
    }

    @Override
    public String queryBill(String subSn,String serviceType) {

        return paymentManager.queryResult(subSn, serviceType);
    }

    @Override
    public PaymentMethodDO getByPluginId(String pluginId) {
        return paymentMethodManager.getByPluginId(pluginId);
    }
}
