package com.enation.app.javashop.service.payment;

import com.enation.app.javashop.model.payment.dto.PayParam;
import com.enation.app.javashop.model.payment.enums.ClientType;
import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.model.payment.vo.PaymentMethodVO;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 全局统一支付接口
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2019-04-10
 */
public interface PaymentManager {


    /**
     * 支付
     * @param payBill
     * @return
     */
    Map pay(PayBill payBill);

    /**
     * 支付异步回调
     * @param paymentPluginId
     * @param clientType
     * @return
     */
    String payCallback(String paymentPluginId, ClientType clientType);


    /**
     * 获取客户端支持的支付方式
     * @param clientType  客户端类型
     * @return
     */
    List<PaymentMethodVO> queryPayments(String clientType);


    /**
     * 同步回调方法
     *
     * @param tradeType
     * @param paymentPluginId
     */
    void payReturn(TradeTypeEnum tradeType, String paymentPluginId);


    /**
     * 查询支付结果并更新账单状态
     * @param subSn
     * @param serviceType
     * @return
     */
    String queryResult(String subSn,String serviceType);

    /**
     * 对一个子订单发起支付
     * @param param
     * @return
     */
    Map pay(PayParam param);
}
