package com.enation.app.javashop.model.payment.vo;

import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import com.enation.app.javashop.model.payment.enums.ClientType;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;

import java.io.Serializable;

/**
 * 支付账单，
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-04-11 16:06:57
 */
public class PayBill implements Serializable {


    private static final long serialVersionUID = -1772939823667009147L;
    /**
     * 编号（交易或订单编号，或其它要扩展的其它类型编号）
     */
    private String subSn;

    /**
     * 账单编号,要传递给第三方平台的，不要管，系统自动生成
     */
    private String billSn;

    /**
     * 要支付的金额
     */
    private Double orderPrice;

    /**
     * normal:正常的网页跳转
     * qr:二维码扫描
     */
    private String payMode;

    /**
     * 交易类型
     */
    private TradeTypeEnum tradeType;


    /**
     * 客户端类型
     */
    private ClientType clientType;

    /**
     * 支付插件
     */
    private String pluginId;

    public PayBill() {
    }

    public PayBill(PaymentBillDO paymentBill) {

        this.billSn = paymentBill.getBillSn();
        this.orderPrice = paymentBill.getTradePrice();
        this.pluginId = paymentBill.getPaymentPluginId();
        this.subSn = paymentBill.getSubSn();
        this.tradeType = TradeTypeEnum.valueOf(paymentBill.getServiceType());
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public String getBillSn() {
        return billSn;
    }

    public void setBillSn(String billSn) {
        this.billSn = billSn;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public TradeTypeEnum getTradeType() {
        return tradeType;
    }

    public void setTradeType(TradeTypeEnum tradeType) {
        this.tradeType = tradeType;
    }

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public String getSubSn() {
        return subSn;
    }

    public void setSubSn(String subSn) {
        this.subSn = subSn;
    }

    @Override
    public String toString() {
        return "PayBill{" +
                "subSn='" + subSn + '\'' +
                ", billSn='" + billSn + '\'' +
                ", orderPrice=" + orderPrice +
                ", payMode='" + payMode + '\'' +
                ", tradeType=" + tradeType +
                ", clientType=" + clientType +
                ", pluginId='" + pluginId + '\'' +
                '}';
    }


}
