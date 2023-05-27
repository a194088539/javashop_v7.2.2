package com.enation.app.javashop.model.payment.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author fk
 * @version v2.0
 * @Description: 调用支付使用参数
 * @date 2018/4/1616:54
 * @since v7.0.0
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PayParam {

    @ApiModelProperty(value = "单号，订单号或者交易号", hidden = true)
    private String sn;

    @ApiModelProperty(value = "支付插件id", name = "payment_plugin_id", required = true)
    private String paymentPluginId;

    @ApiModelProperty(value = "支付模式，正常normal， 二维码 qr,枚举类型PaymentPatternEnum", name = "pay_mode", required = true)
    private String payMode;

    @ApiModelProperty(value = "调用客户端PC,WAP,NATIVE,REACT", name = "client_type", required = true, allowableValues = "PC,WAP,NATIVE,REACT")
    private String clientType;

    @ApiModelProperty(value = "交易类型，TRADE，ORDER,RECHARGE", name = "trade_type", allowableValues = "TRADE,ORDER,RECHARGE")
    private String tradeType;

    @ApiModelProperty(value = "支付金额", hidden = true)
    private Double price;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
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

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
