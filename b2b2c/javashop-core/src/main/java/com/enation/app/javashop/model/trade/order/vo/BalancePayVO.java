package com.enation.app.javashop.model.trade.order.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @description: 预存款支付VO
 * @author: liuyulei
 * @create: 2020-01-01 11:58
 * @version:1.0
 * @since:7.1.5
 **/
@ApiModel( description = "预存款支付参数")
public class BalancePayVO implements Serializable {
    private static final long serialVersionUID = 974160879230069347L;

    @ApiModelProperty(value = "订单/交易编号" )
    private String sn;
    @ApiModelProperty(name = "need_pay",value = "待在线支付金额" )
    private Double needPay;

    @ApiModelProperty(name = "balance",value = "预存款余额" )
    private Double balance;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Double getNeedPay() {
        return needPay;
    }

    public void setNeedPay(Double needPay) {
        this.needPay = needPay;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BalancePayVO{" +
                "sn='" + sn + '\'' +
                ", needPay=" + needPay +
                ", balance=" + balance +
                '}';
    }
}
