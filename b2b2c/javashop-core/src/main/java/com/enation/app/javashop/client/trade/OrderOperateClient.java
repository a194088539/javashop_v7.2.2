package com.enation.app.javashop.client.trade;

import com.enation.app.javashop.model.trade.cart.dos.OrderPermission;
import com.enation.app.javashop.model.trade.order.dos.*;
import com.enation.app.javashop.model.trade.order.enums.OrderOutStatusEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderOutTypeEnum;
import com.enation.app.javashop.model.trade.order.enums.OrderServiceStatusEnum;
import com.enation.app.javashop.model.trade.order.vo.CancelVO;

/**
 * 订单操作SDK
 *
 * @author fk create in 2020/4/7
 * @version v2.0
 * @since v7.2.0
 */
public interface OrderOperateClient {


    /**
     * 更新订单项快照ID
     *
     * @param itemsJson
     * @param orderSn
     * @return
     */
    void updateItemJson(String itemsJson, String orderSn);

    /**
     * 添加交易快照
     * @param orderDO
     */
    void addGoodsSnapshot(OrderDO orderDO);

    /**
     * 更新订单的店铺名称
     * @param shopId
     * @param shopName
     */
    void editOrderShopName(Long shopId, String shopName);

    /**
     * 订单取消
     *
     * @param cancelVO   取消vo
     * @param permission 需要检测的订单权限
     */
    void cancel(CancelVO cancelVO, OrderPermission permission);

    /**
     * 更新订单的售后状态
     *
     * @param orderSn
     * @param serviceStatus
     */
    void updateServiceStatus(String orderSn, OrderServiceStatusEnum serviceStatus);

    /**
     * 修改订单出库状态
     * @param orderSn 订单编号
     * @param typeEnum 出库类型
     * @param  statusEnum  出库状态
     * @return OrderOutStatus 订单出库状态
     */
    void editOutStatus(String orderSn, OrderOutTypeEnum typeEnum, OrderOutStatusEnum statusEnum);

    /**
     * 添加交易记录表
     * @param transactionRecord 交易记录表
     * @return TransactionRecord 交易记录表
     */
    TransactionRecord addTransactionRecord(TransactionRecord transactionRecord);

    /**
     * 支付成功调用
     * @param tradeType
     * @param subSn 业务单号
     * @param returnTradeNo 第三方平台回传单号（第三方平台的支付单号）
     * @param payPrice 支付金额
     */
    void paySuccess(String tradeType,String subSn,String returnTradeNo,Double payPrice);

    /**
     * 添加订单出库状态
     * @param orderOutStatus 订单出库状态信息
     */
    void add(OrderOutStatus orderOutStatus);

    /**
     * 添加订单操作日志
     * @param logDO
     */
    void addOrderLog(OrderLogDO logDO);

}
