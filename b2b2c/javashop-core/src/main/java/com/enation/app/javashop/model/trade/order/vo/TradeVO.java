package com.enation.app.javashop.model.trade.order.vo;

import com.enation.app.javashop.model.member.dos.ReceiptHistory;
import com.enation.app.javashop.model.trade.cart.vo.PriceDetailVO;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 交易VO
 * @author Snow create in 2018/4/9
 * @version v2.0
 * @since v7.0.0
 */
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TradeVO implements Serializable {

    private static final long serialVersionUID = -8580648928412433120L;

    @ApiModelProperty(name="trade_sn",value = "交易编号")
    private String tradeSn;

    @ApiModelProperty(value = "会员id")
    private Long memberId;

    @ApiModelProperty(value = "会员昵称")
    private String memberName;

    @ApiModelProperty(value = "支付方式")
    private String paymentType;

    @ApiModelProperty(value = "价格信息")
    private PriceDetailVO priceDetail;

    @ApiModelProperty(value = "收货人信息")
    private ConsigneeVO consignee;

    @ApiModelProperty(value = "客户端类型")
    private String clientType;

    @ApiModelProperty(name="receipt",value = "发票信息")
    private ReceiptHistory receipt;


    /**
     *备注
     */
    @ApiModelProperty(name="remark",value = "备注")
    private String remark;

    @ApiModelProperty(name="receive_time",value = "收货时间")
    private String receiveTime;

    @ApiModelProperty(name="order_list",value = "订单列表")
    private List<OrderDTO> orderList;



    public String getTradeSn() {
        return tradeSn;
    }

    public void setTradeSn(String tradeSn) {
        this.tradeSn = tradeSn;
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

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public PriceDetailVO getPriceDetail() {
        return priceDetail;
    }

    public void setPriceDetail(PriceDetailVO priceDetail) {
        this.priceDetail = priceDetail;
    }

    public ConsigneeVO getConsignee() {
        return consignee;
    }

    public void setConsignee(ConsigneeVO consignee) {
        this.consignee = consignee;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public ReceiptHistory getReceipt() {
        return receipt;
    }

    public void setReceipt(ReceiptHistory receipt) {
        this.receipt = receipt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }


    public List<OrderDTO> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<OrderDTO> orderList) {
        this.orderList = orderList;
    }


    @Override
    public String toString() {
        return "TradeVO{" +
                "tradeSn='" + tradeSn + '\'' +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", priceDetail=" + priceDetail +
                ", consignee=" + consignee +
                ", clientType='" + clientType + '\'' +
                ", receipt=" + receipt +
                ", remark='" + remark + '\'' +
                ", receiveTime='" + receiveTime + '\'' +
                ", orderList=" + orderList +
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
        TradeVO that = (TradeVO) o;

        return new EqualsBuilder()
                .append(tradeSn, that.tradeSn)
                .append(memberId, that.memberId)
                .append(memberName, that.memberName)
                .append(paymentType, that.paymentType)
                .append(priceDetail, that.priceDetail)
                .append(consignee, that.consignee)
                .append(clientType, that.clientType)
                .append(receipt, that.receipt)
                .append(remark, that.remark)
                .append(receiveTime, that.receiveTime)
                .append(orderList, that.orderList)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(tradeSn)
                .append(memberId)
                .append(memberName)
                .append(paymentType)
                .append(priceDetail)
                .append(consignee)
                .append(clientType)
                .append(receipt)
                .append(remark)
                .append(receiveTime)
                .append(orderList)
                .toHashCode();
    }

}
