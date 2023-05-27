package com.enation.app.javashop.model.trade.order.vo;

import com.enation.app.javashop.model.member.dos.ReceiptHistory;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.trade.cart.vo.CouponVO;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.*;
import com.enation.app.javashop.model.trade.order.support.OrderSpecialStatus;
import com.enation.app.javashop.framework.util.CurrencyUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.List;

/**
 * 订单明细
 *
 * @author Snow create in 2018/5/15
 * @version v2.0
 * @since v7.0.0
 */
@ApiModel(description = "订单明细")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderDetailVO extends OrderDO {

    @ApiModelProperty(value = "订单操作允许情况")
    private OrderOperateAllowable orderOperateAllowableVO;

    @ApiModelProperty(value = "订单状态文字")
    private String orderStatusText;

    @ApiModelProperty(value = "付款状态文字")
    private String payStatusText;

    @ApiModelProperty(value = "发货状态文字")
    private String shipStatusText;

    @ApiModelProperty(value = "售后状态文字")
    private String serviceStatusText;

    @ApiModelProperty(value = "支付方式")
    private String paymentName;

    @ApiModelProperty(value = "sku列表")
    private List<OrderSkuVO> orderSkuList;

    @ApiModelProperty(value = "发票信息")
    private ReceiptHistory receiptHistory;

    @ApiModelProperty(value = "订单赠品列表")
    private List<FullDiscountGiftDO> giftList;

    @ApiModelProperty(value = "返现金额")
    private Double cashBack;

    @ApiModelProperty(value = "优惠券抵扣金额")
    private Double couponPrice;

    @ApiModelProperty(value = "赠送的积分")
    private Integer giftPoint;

    @ApiModelProperty(value = "赠送的优惠券")
    private CouponDO giftCoupon;


    @ApiModelProperty(value = "此订单使用的积分")
    private Integer usePoint;


    @ApiModelProperty(value = "满减金额")
    private Double fullMinus;

    /**
     * 拼团订单状态
     */
    @ApiModelProperty(value = "拼团订单状态")
    private String pingTuanStatus;
    /**
     * 订单是否支持原路退回
     * 未支付的订单为false
     */
    @ApiModelProperty(name = "is_retrace", value = "订单是否支持原路退回")
    private Boolean isRetrace;

    @ApiModelProperty(name = "balance", value = "预存款支付金额")
    private Double balance;

    @ApiModelProperty(name = "is_retrace_balance", value = "订单是否退款到预存款")
    private Boolean isRetraceBalance;

    @ApiModelProperty(name = "goods_original_total_price", value = "订单商品总价(商品原单价*购买数量)")
    private Double goodsOriginalTotalPrice;


    public ReceiptHistory getReceiptHistory() {
        return receiptHistory;
    }

    public void setReceiptHistory(ReceiptHistory receiptHistory) {
        this.receiptHistory = receiptHistory;
    }

    public OrderOperateAllowable getOrderOperateAllowableVO() {
        return orderOperateAllowableVO;
    }

    public void setOrderOperateAllowableVO(OrderOperateAllowable orderOperateAllowableVO) {
        this.orderOperateAllowableVO = orderOperateAllowableVO;
    }

    public String getPingTuanStatus() {
//        pingTuanStatus = "";
//        //已经付款的拼团订单的状态为待成团
//        if (OrderTypeEnum.pintuan.name().equals(this.getOrderType())) {
//            if (this.getPayStatus().equals(PayStatusEnum.PAY_NO.value())) {
//                if(OrderStatusEnum.CANCELLED.value().equals(this.getOrderStatus())){
//                    pingTuanStatus = "未成团";
//                }else{
//                    pingTuanStatus = "待成团";
//                }
//            } else if (OrderStatusEnum.PAID_OFF.value().equals(this.getOrderStatus())) {
//                pingTuanStatus = "待成团";
//            } else {
//                pingTuanStatus = "已成团";
//            }
//
//        }

        return pingTuanStatus;
    }

    public void setPingTuanStatus(String pingTuanStatus) {
        this.pingTuanStatus = pingTuanStatus;
    }

    public String getOrderStatusText() {

        //先从特殊的流程-状态显示 定义中读取，如果为空说明不是特殊的状态，直接显示为 状态对应的提示词
        orderStatusText = OrderSpecialStatus.getStatusText(getOrderType(), getPaymentType(), getOrderStatus());
        if (StringUtil.isEmpty(orderStatusText)) {
            orderStatusText = OrderStatusEnum.valueOf(getOrderStatus()).description();
        }

        return orderStatusText;
    }

    public void setOrderStatusText(String orderStatusText) {
        this.orderStatusText = orderStatusText;
    }

    public String getPayStatusText() {
        if (this.getPayStatus() != null) {
            PayStatusEnum payStatusEnum = PayStatusEnum.valueOf(this.getPayStatus());
            payStatusText = payStatusEnum.description();
        }
        return payStatusText;
    }

    public void setPayStatusText(String payStatusText) {
        this.payStatusText = payStatusText;
    }

    public String getShipStatusText() {
        if (this.getShipStatus() != null) {
            ShipStatusEnum shipStatusEnum = ShipStatusEnum.valueOf(this.getShipStatus());
            shipStatusText = shipStatusEnum.description();
        }
        return shipStatusText;
    }

    public void setShipStatusText(String shipStatusText) {
        this.shipStatusText = shipStatusText;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }


    public List<OrderSkuVO> getOrderSkuList() {
        return orderSkuList;
    }

    public void setOrderSkuList(List<OrderSkuVO> orderSkuList) {
        this.orderSkuList = orderSkuList;
    }

    public String getServiceStatusText() {
        if (this.getServiceStatus() != null) {
            OrderServiceStatusEnum serviceStatusEnum = OrderServiceStatusEnum.valueOf(this.getServiceStatus());
            serviceStatusText = serviceStatusEnum.description();
        }
        return serviceStatusText;
    }

    public void setServiceStatusText(String serviceStatusText) {
        this.serviceStatusText = serviceStatusText;
    }

    public List<FullDiscountGiftDO> getGiftList() {
        return giftList;
    }

    public void setGiftList(List<FullDiscountGiftDO> giftList) {
        this.giftList = giftList;
    }

    public Double getCashBack() {
        return cashBack;
    }

    public void setCashBack(Double cashBack) {
        this.cashBack = cashBack;
    }

    public Double getCouponPrice() {
        if (couponPrice == null) {
            return 0D;
        }
        return couponPrice;
    }

    public void setCouponPrice(Double couponPrice) {
        this.couponPrice = couponPrice;
    }

    public Integer getGiftPoint() {
        return giftPoint;
    }

    public void setGiftPoint(Integer giftPoint) {
        this.giftPoint = giftPoint;
    }

    public Integer getUsePoint() {
        return usePoint;
    }

    public void setUsePoint(Integer usePoint) {
        this.usePoint = usePoint;
    }

    public CouponDO getGiftCoupon() {
        return giftCoupon;
    }

    public void setGiftCoupon(CouponDO giftCoupon) {
        this.giftCoupon = giftCoupon;
    }

    public Double getFullMinus() {
        if (fullMinus == null) {
            return 0D;
        }
        return fullMinus;
    }

    public void setFullMinus(Double fullMinus) {
        this.fullMinus = fullMinus;
    }

    public Boolean getIsRetrace() {
        return isRetrace;
    }

    public void setIsRetrace(Boolean isRetrace) {
        this.isRetrace = isRetrace;
    }

    @Override
    public Double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Boolean getIsRetraceBalance() {
        if(this.getBalance()>0){
            return true;
        }
        return false;
    }

    public void setIsRetraceBalance(Boolean retraceBalance) {
        isRetraceBalance = retraceBalance;
    }

    public Double getGoodsOriginalTotalPrice() {
        return goodsOriginalTotalPrice;
    }

    public void setGoodsOriginalTotalPrice(Double goodsOriginalTotalPrice) {
        this.goodsOriginalTotalPrice = goodsOriginalTotalPrice;
    }

    @Override
    public String toString() {
        return "OrderDetailVO{" +
                "orderOperateAllowableVO=" + orderOperateAllowableVO +
                ", orderStatusText='" + orderStatusText + '\'' +
                ", payStatusText='" + payStatusText + '\'' +
                ", shipStatusText='" + shipStatusText + '\'' +
                ", serviceStatusText='" + serviceStatusText + '\'' +
                ", paymentName='" + paymentName + '\'' +
                ", orderSkuList=" + orderSkuList +
                ", receiptHistory=" + receiptHistory +
                ", giftList=" + giftList +
                ", cashBack=" + cashBack +
                ", couponPrice=" + couponPrice +
                ", giftPoint=" + giftPoint +
                ", giftCoupon=" + giftCoupon +
                ", usePoint=" + usePoint +
                ", fullMinus=" + fullMinus +
                ", pingTuanStatus='" + pingTuanStatus + '\'' +
                ", isRetrace=" + isRetrace +
                ", balance=" + balance +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderDetailVO that = (OrderDetailVO) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(orderOperateAllowableVO, that.orderOperateAllowableVO)
                .append(orderStatusText, that.orderStatusText)
                .append(payStatusText, that.payStatusText)
                .append(shipStatusText, that.shipStatusText)
                .append(serviceStatusText, that.serviceStatusText)
                .append(paymentName, that.paymentName)
                .append(orderSkuList, that.orderSkuList)
                .append(receiptHistory, that.receiptHistory)
                .append(giftList, that.giftList)
                .append(cashBack, that.cashBack)
                .append(couponPrice, that.couponPrice)
                .append(giftPoint, that.giftPoint)
                .append(giftCoupon, that.giftCoupon)
                .append(usePoint, that.usePoint)
                .append(fullMinus, that.fullMinus)
                .append(pingTuanStatus, that.pingTuanStatus)
                .append(isRetrace, that.isRetrace)
                .append(balance, that.balance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(orderOperateAllowableVO)
                .append(orderStatusText)
                .append(payStatusText)
                .append(shipStatusText)
                .append(serviceStatusText)
                .append(paymentName)
                .append(orderSkuList)
                .append(receiptHistory)
                .append(giftList)
                .append(cashBack)
                .append(couponPrice)
                .append(giftPoint)
                .append(giftCoupon)
                .append(usePoint)
                .append(fullMinus)
                .append(pingTuanStatus)
                .append(isRetrace)
                .append(balance)
                .toHashCode();
    }

}
