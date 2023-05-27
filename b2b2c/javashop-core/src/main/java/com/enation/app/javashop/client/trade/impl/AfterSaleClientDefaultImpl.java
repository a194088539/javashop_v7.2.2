package com.enation.app.javashop.client.trade.impl;

import com.enation.app.javashop.model.aftersale.dos.RefundDO;
import com.enation.app.javashop.model.aftersale.vo.ApplyAfterSaleVO;
import com.enation.app.javashop.model.goods.enums.Permission;
import com.enation.app.javashop.service.aftersale.AfterSaleManager;
import com.enation.app.javashop.client.trade.AfterSaleClient;
import com.enation.app.javashop.service.aftersale.AfterSaleQueryManager;
import com.enation.app.javashop.service.aftersale.AfterSaleRefundManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 售后client 默认实现
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/4/2
 */
@Service
public class AfterSaleClientDefaultImpl implements AfterSaleClient {

    @Autowired
    protected AfterSaleManager afterSaleManager;

    @Autowired
    private AfterSaleQueryManager afterSaleQueryManager;

    @Autowired
    private AfterSaleRefundManager afterSaleRefundManager;

    @Override
    public void cancelPintuanOrder(String orderSn, String cancelReason) {
        afterSaleManager.cancelPintuanOrder(orderSn, cancelReason);
    }

    @Override
    public ApplyAfterSaleVO detail(String serviceSn) {
        return afterSaleQueryManager.detail(serviceSn, Permission.CLIENT);
    }

    @Override
    public RefundDO getAfterSaleRefundModel(String serviceSn) {
        return afterSaleRefundManager.getModel(serviceSn);
    }

    @Override
    public void refundCompletion() {
        afterSaleManager.refundCompletion();
    }

    @Override
    public void editAfterSaleShopName(Long shopId, String shopName) {
        afterSaleManager.editAfterSaleShopName(shopId, shopName);
    }
}
