package com.enation.app.javashop.client.trade.impl;

import com.enation.app.javashop.client.trade.OrderMetaClient;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.trade.order.dos.OrderMetaDO;
import com.enation.app.javashop.model.trade.order.enums.OrderMetaKeyEnum;
import com.enation.app.javashop.service.trade.order.OrderMetaManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单操作SDK
 *
 * @author fk create in 2020/4/7
 * @version v2.0
 * @since v7.2.0
 */
@Service
public class OrderMetaClientDefaultImpl implements OrderMetaClient {

    @Autowired
    private OrderMetaManager orderMetaManager;

    @Override
    public void add(OrderMetaDO orderMetaDO) {
        orderMetaManager.add(orderMetaDO);
    }

    @Override
    public List<FullDiscountGiftDO> getGiftList(String orderSn, String status) {
        return orderMetaManager.getGiftList(orderSn, status);
    }

    @Override
    public void updateMetaStatus(String orderSn, OrderMetaKeyEnum metaKey, String status) {
        orderMetaManager.updateMetaStatus(orderSn, metaKey, status);
    }

    @Override
    public String getMetaValue(String orderSn, OrderMetaKeyEnum metaKey) {
        return orderMetaManager.getMetaValue(orderSn, metaKey);
    }
}
