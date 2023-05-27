package com.enation.app.javashop.model.payment.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @description: 支付账单VO
 * @author: liuyulei
 * @create: 2019-12-27 10:23
 * @version:1.0
 * @since:7.1.4
 **/
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentBillVO implements Serializable {
    private static final long serialVersionUID = -2694305846531958299L;
    /**
     * 交易单号
     */
    @ApiModelProperty(name="sub_sn",value="单号",required=false)
    private String subSn;
    /**
     * 支付账单编号（提交给第三方平台单号）
     */
    @ApiModelProperty(name="bill_sn",value="提交给第三方平台单号",required=false)
    private String billSn;
    /**
     * 交易类型
     */
    @ApiModelProperty(name="service_type",value="交易类型",required=false)
    private String serviceType;
    /**
     * 支付方式名称
     */
    @ApiModelProperty(name="payment_name",value="支付方式名称",required=false)
    private String paymentName;
    /**
     * 交易金额
     */
    @ApiModelProperty(name="trade_price",value="交易金额",required=false)
    private Double tradePrice;

    /**
     * 支付插件id
     */
    @ApiModelProperty(name="payment_plugin_id",value="支付插件id",required=false)
    private String paymentPluginId;

    /**
     * 支付方式
     */
    @ApiModelProperty(name="pay_mode",value="支付方式",required=false)
    private String payMode;

    /**
     * 客户端类型
     */
    @ApiModelProperty(name="client_type",value="客户端类型",required=false)
    private String clientType;


    /**
     * 支付配置
     */
    @ApiModelProperty(name="pay_config",value="客户端类型",required=false)
    private String payConfig;

    public String getSubSn() {
        return subSn;
    }

    public void setSubSn(String subSn) {
        this.subSn = subSn;
    }

    public String getBillSn() {
        return billSn;
    }

    public void setBillSn(String billSn) {
        this.billSn = billSn;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public Double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(Double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public String getPaymentPluginId() {
        return paymentPluginId;
    }

    public void setPaymentPluginId(String paymentPluginId) {
        this.paymentPluginId = paymentPluginId;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getPayConfig() {
        return payConfig;
    }

    public void setPayConfig(String payConfig) {
        this.payConfig = payConfig;
    }

    @Override
    public String toString() {
        return "PaymentBillVO{" +
                "subSn='" + subSn + '\'' +
                ", billSn='" + billSn + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", paymentName='" + paymentName + '\'' +
                ", tradePrice=" + tradePrice +
                ", paymentPluginId='" + paymentPluginId + '\'' +
                ", payMode='" + payMode + '\'' +
                ", clientType='" + clientType + '\'' +
                ", payConfig='" + payConfig + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentBillVO that = (PaymentBillVO) o;

        return new EqualsBuilder()
                .append(subSn, that.subSn)
                .append(billSn, that.billSn)
                .append(serviceType, that.serviceType)
                .append(paymentName, that.paymentName)
                .append(tradePrice, that.tradePrice)
                .append(paymentPluginId, that.paymentPluginId)
                .append(payMode, that.payMode)
                .append(clientType, that.clientType)
                .append(payConfig, that.payConfig)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subSn)
                .append(billSn)
                .append(serviceType)
                .append(paymentName)
                .append(tradePrice)
                .append(paymentPluginId)
                .append(payMode)
                .append(clientType)
                .append(payConfig)
                .toHashCode();
    }

}
