package com.enation.app.javashop.model.trade.order.vo;

import com.enation.app.javashop.model.member.dos.ReceiptHistory;
import com.enation.app.javashop.model.trade.cart.vo.PriceDetailVO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * @description: 创建交易参数VO
 * @author: liuyulei
 * @create: 2020-03-18 10:31
 * @version:1.0
 * @since:7.2.0
 **/
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TradeParam implements Serializable {


    private static final long serialVersionUID = -9181995425971604676L;
    /**
     * 客户端类型
     */
    @ApiModelProperty(name="client",value = "客户端类型",required = true)
    private String client;


    /**
     * 交易价格VO
     */
    @ApiModelProperty(name="price",value = "交易价格VO",required = true)
    private PriceDetailVO price;

    /**
     * 联系人手机号
     */
    @ApiModelProperty(name="mobile",value = "手机号")
    private String mobile;

    /**
     * 联系人
     */
    @ApiModelProperty(name="ship_name",value = "联系人")
    private String shipName;

    /**
     * 收货地址id
     */
    @ApiModelProperty(name="addr_id",value = "收货地址id")
    private Long addrId;

    /**
     *备注
     */
    @ApiModelProperty(name="remark",value = "备注")
    private String remark;

    /**
     *收货地址街道id
     */
    @ApiModelProperty(name="receipt",value = "发票信息")
    private ReceiptHistory receipt;

    /**
     *支付类型
     */
    @ApiModelProperty(name="payment_type",value = "支付类型",required = true)
    private String paymentType;

    @ApiModelProperty(name="receive_time",value = "收货时间")
    private String receiveTime;

    @ApiModelProperty(name="member_id",value = "会员id")
    private Long memberId;

    @ApiModelProperty(name="member_name",value = "会员名称")
    private String memberName;

    @ApiModelProperty(value = "收货信息")
    private ConsigneeVO consignee;


    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public PriceDetailVO getPrice() {
        return price;
    }

    public void setPrice(PriceDetailVO price) {
        this.price = price;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public Long getAddrId() {
        return addrId;
    }

    public void setAddrId(Long addrId) {
        this.addrId = addrId;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public ReceiptHistory getReceipt() {
        return receipt;
    }

    public void setReceipt(ReceiptHistory receipt) {
        this.receipt = receipt;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }


    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public ConsigneeVO getConsignee() {
        return consignee;
    }

    public void setConsignee(ConsigneeVO consignee) {
        this.consignee = consignee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TradeParam that = (TradeParam) o;

        return new EqualsBuilder()
                .append(client, that.client)
                .append(price, that.price)
                .append(mobile, that.mobile)
                .append(shipName, that.shipName)
                .append(addrId, that.addrId)
                .append(remark, that.remark)
                .append(receipt, that.receipt)
                .append(paymentType, that.paymentType)
                .append(receiveTime, that.receiveTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(client)
                .append(price)
                .append(mobile)
                .append(shipName)
                .append(addrId)
                .append(remark)
                .append(receipt)
                .append(paymentType)
                .append(receiveTime)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "TradeParamVO{" +
                "client='" + client + '\'' +
                ", price=" + price +
                ", mobile='" + mobile + '\'' +
                ", shipName='" + shipName + '\'' +
                ", addrId=" + addrId +
                ", remark='" + remark + '\'' +
                ", receipt=" + receipt +
                ", paymentType='" + paymentType + '\'' +
                '}';
    }
}
