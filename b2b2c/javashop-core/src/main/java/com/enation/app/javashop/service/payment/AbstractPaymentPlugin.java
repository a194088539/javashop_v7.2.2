package com.enation.app.javashop.service.payment;

import com.enation.app.javashop.model.base.DomainHelper;
import com.enation.app.javashop.model.payment.enums.ClientType;
import com.enation.app.javashop.model.payment.vo.PayBill;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 支付插件父类<br>
 * 具有读取配置的能力
 *
 * @author kingapex
 * @version 1.0
 * @since pangu1.0
 * 2017年4月3日下午11:38:38
 */
public abstract class AbstractPaymentPlugin {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 测试环境 0  生产环境  1
     */
    protected int isTest = 0;


    public static final String SUCCESS = "SUCCESS";

    public static final String FAIL = "FAIL";

    public static final String REFUND_ERROR_MESSAGE = "{REFUND_ERROR_MESSAGE}";


    @Autowired
    private PaymentMethodManager paymentMethodManager;

    @Autowired
    private PaymentBillManager paymentBillManager;

    @Autowired
    private DomainHelper domainHelper;



    /**
     * 获取插件的配置方式
     *
     * @return
     */
    protected Map<String, String> getConfig(ClientType clientType) {
        return paymentMethodManager.getConfig(clientType.getDbColumn(), this.getPluginId());
    }


    /**
     * 获取插件id
     *
     * @return
     */
    protected abstract String getPluginId();


    /**
     * 获取同步通知url
     *
     * @param bill 交易
     * @return
     */
    protected String getReturnUrl(PayBill bill) {
        String tradeType = bill.getTradeType().name();
        String payMode = bill.getPayMode();
        return domainHelper.getCallback() + "/payment/return/" + tradeType + "/" + payMode + "/" + this.getPluginId();
    }

    /**
     * 获取异步通知url
     *
     * @param tradeType
     * @return
     */
    protected String getCallBackUrl(TradeTypeEnum tradeType, ClientType clientType) {
        return domainHelper.getCallback() + "/payment/callback/" + tradeType + "/" + this.getPluginId() + "/" + clientType;
    }

    /**
     * 支付回调后执行方法
     *
     * @param billSn        支付账号单
     * @param returnTradeNo 第三方平台回传支付单号
     * @param payPrice
     */
    protected void paySuccess(String billSn, String returnTradeNo, double payPrice) {
        //调用账单接口完成相关交易及流程的状态变更
        this.paymentBillManager.paySuccess(billSn, returnTradeNo,  payPrice);
    }


}
