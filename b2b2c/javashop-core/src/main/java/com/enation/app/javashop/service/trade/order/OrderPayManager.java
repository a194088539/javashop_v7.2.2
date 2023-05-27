package com.enation.app.javashop.service.trade.order;

import com.enation.app.javashop.model.payment.dto.PayParam;

import java.util.Map;

/**
 * @author fk
 * @version v2.0
 * @Description: 订单支付
 * @date 2018/4/1617:09
 * @since v7.0.0
 */
public interface OrderPayManager {

    /**
     * 支付
     *
     * @param param
     * @return
     */
    Map pay(PayParam param);


    /**
     * 支付成功调用
     * @param tradeType
     * @param subSn 业务单号
     * @param returnTradeNo 第三方平台回传单号（第三方平台的支付单号）
     * @param payPrice 支付金额
     */
    void paySuccess(String tradeType,String subSn,String returnTradeNo,Double payPrice);

}
