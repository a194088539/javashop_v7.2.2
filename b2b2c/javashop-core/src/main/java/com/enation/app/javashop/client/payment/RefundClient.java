package com.enation.app.javashop.client.payment;

import java.util.Map;

/**
 * 支付中心退款client
 *
 * @author fk
 * @version v7.0
 * @date 20/3/9 下午3:51
 * @since v7.2.1
 */
public interface RefundClient {

    /**
     * 原路退回
     *
     * @param returnTradeNo 第三方订单号
     * @param refundSn      退款编号，子业务中必须唯一
     * @param refundPrice   退款金额
     * @param subType       子业务类型
     * @return
     */
    Map originRefund(String returnTradeNo, String refundSn, Double refundPrice,String subType);


    /**
     * 查询退款状态
     *
     * @param returnTradeNo 第三方订单号
     * @param refundSn      退款编号，子业务中必须唯一
     * @param subType      子业务类型
     * @return
     */
    String queryRefundStatus(String returnTradeNo, String refundSn,String subType);


}
