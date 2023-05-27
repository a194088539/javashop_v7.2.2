package com.enation.app.javashop.client.payment;

import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;
import com.enation.app.javashop.model.payment.dto.PayParam;

import java.util.Map;

/**
 * 支付中心client
 *
 * @author fk
 * @version v7.0
 * @date 20/3/9 下午3:51
 * @since v7.2.1
 */
public interface PaymentClient {

    /**
     * 对一个子订单发起支付
     * @param param
     * @return
     */
    Map pay(PayParam param);


    /**
     * 查询一个账单的支付状态
     * @param subSn
     * @param serviceType
     * @return
     */
    String queryBill(String subSn,String serviceType);

    /**
     * 根据支付插件id获取支付方式详细
     * @param pluginId
     * @return
     */
    PaymentMethodDO getByPluginId(String pluginId);


}
