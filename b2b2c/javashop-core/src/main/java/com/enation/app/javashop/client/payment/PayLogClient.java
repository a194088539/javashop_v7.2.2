package com.enation.app.javashop.client.payment;

import com.enation.app.javashop.model.trade.order.dos.PayLog;

/**
 * 支付日志SDK
 *
 * @author fk create in 2020/4/7
 * @version v2.0
 * @since v7.2.0
 */
public interface PayLogClient {

    /**
     * 添加收款单
     * @param payLog 收款单
     * @return PayLog 收款单
     */
    PayLog add(PayLog payLog);

    /**
     * 修改收款单
     * @param payLog 收款单
     * @param id 收款单主键
     * @return PayLog 收款单
     */
    PayLog edit(PayLog payLog, Long id);

    /**
     * 根据订单号
     * @param orderSn
     * @return
     */
    PayLog getModel(String  orderSn);


}
