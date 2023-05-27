package com.enation.app.javashop.client.trade.impl;

import com.enation.app.javashop.client.trade.PintuanOrderClient;
import com.enation.app.javashop.model.promotion.pintuan.PintuanOrderDetailVo;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.service.trade.pintuan.PintuanOrderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class PintuanOrderClientImpl implements PintuanOrderClient {

    @Autowired
    private PintuanOrderManager pintuanOrderManager;


    @Override
    public PintuanOrderDetailVo getMainOrderBySn(String orderSn) {
        return pintuanOrderManager.getMainOrderBySn(orderSn);
    }

    @Override
    public void cancelOrder(String orderSn) {
        pintuanOrderManager.cancelOrder(orderSn);
    }

    @Override
    public void payOrder(OrderDO order) {
        pintuanOrderManager.payOrder(order);
    }
}
