package com.enation.app.javashop.model.payment.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dos.TradeDO;
import com.enation.app.javashop.model.trade.order.enums.TradeTypeEnum;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;


/**
 * 支付帐单实体
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-04-16 17:28:07
 *
 * update
 * @author: liuyulei
 * @create: 2019/12/27 10:08
 * @version:3.0
 * @since:7.1.4
 */

@TableName("es_payment_bill")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentBillDO implements Serializable {
			
    private static final long serialVersionUID = 4389253275250473L;

    /**
     * 主键id
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden=true)
    private Long billId;
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
     * 第三方平台返回交易号
     */
    @ApiModelProperty(name="return_trade_no",value="第三方平台返回交易号",required=false)
    private String returnTradeNo;
    /**
     * 是否已支付
     */
    @ApiModelProperty(name="is_pay",value="是否已支付",required=false)
    private Integer isPay;
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
     * 支付参数
     */
    @ApiModelProperty(name="pay_config",value="支付参数",required=false)
    private String payConfig;
    /**
     * 交易金额
     */
    private Double tradePrice;

    /**
     * 支付插件id
     */
    private String paymentPluginId;

    public PaymentBillDO() {
    }

    public PaymentBillDO(String subSn, String billSn, String returnTradeNo, Integer isPay, String serviceType, String paymentName, Double tradePrice, String paymentPluginId) {
        this.subSn = subSn;
        this.billSn = billSn;
        this.returnTradeNo = returnTradeNo;
        this.isPay = isPay;
        this.serviceType = serviceType;
        this.paymentName = paymentName;
        this.tradePrice = tradePrice;
        this.paymentPluginId = paymentPluginId;
    }

    public PaymentBillDO(TradeDO tradeDO) {
        this.subSn = tradeDO.getTradeSn();
        this.tradePrice = tradeDO.getTotalPrice();
        this.serviceType = TradeTypeEnum.TRADE.name();
        this.isPay = 0;
    }

    public PaymentBillDO(OrderDO orderDO) {
        this.subSn = orderDO.getTradeSn();
        this.tradePrice = orderDO.getNeedPayMoney();
        this.serviceType = TradeTypeEnum.ORDER.name();
        this.isPay = 0;
    }

    public  PaymentBillDO(String subSn,Double price,String type){
        this.subSn = subSn;
        this.tradePrice = price;
        this.serviceType = type;
        this.isPay = 0;
    }

    @PrimaryKeyField
    public Long getBillId() {
        return billId;
    }
    public void setBillId(Long billId) {
        this.billId = billId;
    }

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

    public String getReturnTradeNo() {
        return returnTradeNo;
    }

    public void setReturnTradeNo(String returnTradeNo) {
        this.returnTradeNo = returnTradeNo;
    }

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
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

    public String getPayConfig() {
        return payConfig;
    }

    public void setPayConfig(String payConfig) {
        this.payConfig = payConfig;
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

    @Override
    public String toString() {
        return "PaymentBillDO{" +
                "billId=" + billId +
                ", subSn='" + subSn + '\'' +
                ", billSn='" + billSn + '\'' +
                ", returnTradeNo='" + returnTradeNo + '\'' +
                ", isPay=" + isPay +
                ", serviceType='" + serviceType + '\'' +
                ", paymentName='" + paymentName + '\'' +
                ", payConfig='" + payConfig + '\'' +
                ", tradePrice=" + tradePrice +
                ", paymentPluginId='" + paymentPluginId + '\'' +
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
        PaymentBillDO that = (PaymentBillDO) o;

        return new EqualsBuilder()
                .append(billId, that.billId)
                .append(subSn, that.subSn)
                .append(billSn, that.billSn)
                .append(returnTradeNo, that.returnTradeNo)
                .append(isPay, that.isPay)
                .append(serviceType, that.serviceType)
                .append(paymentName, that.paymentName)
                .append(payConfig, that.payConfig)
                .append(tradePrice, that.tradePrice)
                .append(paymentPluginId, that.paymentPluginId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(billId)
                .append(subSn)
                .append(billSn)
                .append(returnTradeNo)
                .append(isPay)
                .append(serviceType)
                .append(paymentName)
                .append(payConfig)
                .append(tradePrice)
                .append(paymentPluginId)
                .toHashCode();
    }
}