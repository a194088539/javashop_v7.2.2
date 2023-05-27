package com.enation.app.javashop.model.base.message;

import com.enation.app.javashop.model.payment.dos.PaymentBillDO;

import java.io.Serializable;


/**
 * 支付账单变化消息
 *
 * @author fk
 * @version v2.0
 * @since v7.2.1
 * 2020年3月11日 上午9:52:13
 */
public class PaymentBillStatusChangeMsg implements Serializable {

    /**
     * 账单信息
     */
    private PaymentBillDO bill;

    /**
     * 回调回来的支付金额
     */
    private Double payPrice;

    /**
     * 账单的支付状态
     */
    private String status;

    public static final String SUCCESS = "SUCCESS";

    public static final String FAIL = "FAIL";

    public PaymentBillStatusChangeMsg() {
    }

    public PaymentBillStatusChangeMsg(PaymentBillDO bill,Double payPrice) {

        this.bill = bill;
        this.payPrice = payPrice;
    }

    public PaymentBillDO getBill() {
        return bill;
    }

    public void setBill(PaymentBillDO bill) {
        this.bill = bill;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(Double payPrice) {
        this.payPrice = payPrice;
    }
}
