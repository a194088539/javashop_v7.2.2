package com.enation.app.javashop.client.trade.impl;


import com.enation.app.javashop.client.trade.OrderOperateClient;
import com.enation.app.javashop.model.trade.cart.dos.OrderPermission;
import com.enation.app.javashop.model.trade.order.dos.*;
import com.enation.app.javashop.model.trade.order.enums.OrderOutStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderOutTypeEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.CancelVO;
import com.enation.app.javashop.service.trade.order.*;
import com.enation.app.javashop.service.trade.snapshot.GoodsSnapshotManager;
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
public class OrderOperateClientDefaultImpl implements OrderOperateClient {

    @Autowired
    private OrderOperateManager orderOperateManager;

    @Autowired
    private GoodsSnapshotManager goodsSnapshotManager;

    @Autowired
    private OrderOutStatusManager orderOutStatusManager;

    @Autowired
    private TransactionRecordManager transactionRecordManager;

    @Autowired
    private OrderPayManager orderPayManager;

    @Autowired
    private OrderLogManager orderLogManager;

    @Override
    public void updateItemJson(String itemsJson, String orderSn) {

        orderOperateManager.updateItemJson(itemsJson, orderSn);
    }

    @Override
    public void addGoodsSnapshot(OrderDO orderDO) {
        goodsSnapshotManager.add(orderDO);
    }

    @Override
    public void editOrderShopName(Long shopId, String shopName) {
        orderOperateManager.editOrderShopName(shopId, shopName);
    }

    @Override
    public void cancel(CancelVO cancelVO, OrderPermission permission) {
        orderOperateManager.cancel(cancelVO, permission);
    }

    @Override
    public void updateServiceStatus(String orderSn, OrderServiceStatusEnum serviceStatus) {
        orderOperateManager.updateServiceStatus(orderSn, serviceStatus);
    }

    @Override
    public void editOutStatus(String orderSn, OrderOutTypeEnum typeEnum, OrderOutStatusEnum statusEnum) {
        orderOutStatusManager.edit(orderSn, typeEnum, statusEnum);
    }

    @Override
    public TransactionRecord addTransactionRecord(TransactionRecord transactionRecord) {
        return transactionRecordManager.add(transactionRecord);
    }

    @Override
    public void paySuccess(String tradeType, String subSn, String returnTradeNo, Double payPrice) {

        orderPayManager.paySuccess(tradeType, subSn, returnTradeNo, payPrice);
    }

    @Override
    public void add(OrderOutStatus orderOutStatus) {
        orderOutStatusManager.add(orderOutStatus);
    }

    @Override
    public void addOrderLog(OrderLogDO logDO) {
        orderLogManager.add(logDO);
    }
}
