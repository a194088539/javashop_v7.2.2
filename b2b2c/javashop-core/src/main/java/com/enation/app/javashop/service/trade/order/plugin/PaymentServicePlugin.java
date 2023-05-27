package com.enation.app.javashop.service.trade.order.plugin;


import com.enation.app.javashop.model.trade.order.vo.BalancePayVO;

/**
*
* @description: 支付业务插件
* @author: liuyulei
* @create: 2019/12/27 11:47
* @version:1.0
* @since:7.1.5
**/
public interface PaymentServicePlugin {

    int MAX_TIMES = 3;


    /**
     * 获取支付子业务类型
     *
     * @return
     */
    String getServiceType();

    /**
     * 获取子业务待支付金额
     * @param subSn
     * @return
     */
    Double getPrice(String subSn);

    /**
     * 检测交易（订单）是否可以被支付
     *
     * @param subSn        业务编号
     * @param times     次数
     * @return 是否可以被支付
     */
    boolean checkStatus(String subSn,Integer times);

    /**
     * 支付成功调用
     * @param subSn 业务单号
     * @param returnTradeNo 第三方平台回传单号（第三方平台的支付单号）
     * @param payPrice 支付金额
     */
    void paySuccess(String subSn,String returnTradeNo,Double payPrice);

    /**
     * 修改支付方式
     * @param subSn  子业务编号
     * @param pluginId  支付插件id
     * @param methodName  支付插件名称
     */
    void updatePaymentMethod(String subSn,String pluginId,String methodName);


    /**
     * 预存款支付
     * @param payVO
     * @param memberId
     */
    void balancePay(BalancePayVO payVO,Long memberId);
}
