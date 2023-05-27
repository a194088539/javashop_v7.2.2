package com.enation.app.javashop.client.payment.impl;

import com.enation.app.javashop.client.payment.RefundClient;
import com.enation.app.javashop.service.payment.RefundManager;
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
@ConditionalOnProperty(value = "javashop.product", havingValue = "stand")
public class RefundClientDefaultImpl implements RefundClient {

    @Autowired
    private RefundManager refundManager;


    @Override
    public Map originRefund(String returnTradeNo, String refundSn, Double refundPrice, String subType) {
        return refundManager.originRefund(returnTradeNo, subType + refundSn, refundPrice);
    }

    @Override
    public String queryRefundStatus(String returnTradeNo, String refundSn,String subType) {
        return refundManager.queryRefundStatus(returnTradeNo,subType + refundSn);
    }
}
