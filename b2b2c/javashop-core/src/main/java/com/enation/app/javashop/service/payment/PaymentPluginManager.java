package com.enation.app.javashop.service.payment;

import com.enation.app.javashop.model.payment.enums.ClientType;
import com.enation.app.javashop.model.payment.vo.ClientConfig;
import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.model.payment.vo.RefundBill;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * 支付插件
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-04-11 16:06:57
 */
public interface PaymentPluginManager {


    /**
     * 获取支付插件id
     *
     * @return
     */
    String getPluginId();

    /**
     * 支付名称
     *
     * @return
     */
    String getPluginName();

    /**
     * 自定义客户端配置文件
     *
     * @return
     */
    List<ClientConfig> definitionClientConfig();

    /**
     * 支付
     *
     * @param bill
     * @return
     */
    Map pay(PayBill bill);

    /**
     * 同步回调
     *
     * @param tradeType
     */
    void onReturn(TradeTypeEnum tradeType);

    /**
     * 异步回调
     *
     * @param clientType
     * @return
     */
    String onCallback( ClientType clientType);

    /**
     * 主动查询支付结果
     *
     * @param billSn 账单号
     * @param config 支付使用的参数
     * @return
     */
    String onQuery(String billSn,Map config);


    /**
     * 退款，原路退回
     *
     * @param bill
     * @return
     */
    boolean onTradeRefund(RefundBill bill);

    /**
     * 查询退款状态
     *
     * @param bill
     * @return
     */
    String queryRefundStatus(RefundBill bill);

    /**
     * 是否支持原路退回   0 不支持  1支持
     *
     * @return
     */
    Integer getIsRetrace();

}
