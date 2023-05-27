package com.enation.app.javashop.model.trade.order.vo;

import com.alipay.api.domain.OrderItem;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.model.trade.complain.enums.ComplainSkuStatusEnum;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.enums.*;
import com.enation.app.javashop.model.trade.order.support.OrderSpecialStatus;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.framework.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 订单列表项
 *
 * @author Snow create in 2018/5/10
 * @version v2.0
 * @since v7.0.0
 */
public class OrderLineVO implements Serializable {

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "订单编号")
    private String sn;

    @ApiModelProperty(value = "卖家名称")
    private Long sellerId;

    @ApiModelProperty(value = "卖家名称")
    private String sellerName;

    @ApiModelProperty(value = "配送方式")
    private String shippingType;

    @ApiModelProperty(value = "支付方式")
    private String paymentName;

    @ApiModelProperty(value = "订单状态文字")
    private String orderStatusText;

    @ApiModelProperty(value = "付款状态文字")
    private String payStatusText;

    @ApiModelProperty(value = "货运状态文字")
    private String shipStatusText;

    @ApiModelProperty(value = "订单状态值")
    private String orderStatus;

    @ApiModelProperty(value = "付款状态值")
    private String payStatus;

    @ApiModelProperty(value = "货运状态值")
    private String shipStatus;

    @ApiModelProperty(value = "评论状态")
    private String commentStatus;

    @ApiModelProperty(value = "订单操作允许情况")
    private OrderOperateAllowable orderOperateAllowableVO;

    @ApiModelProperty(value = "支付类型")
    private String paymentType;

    @ApiModelProperty(value = "收货人姓名")
    private String shipName;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "实际发货时间")
    private Long shipTime;

    @ApiModelProperty(value = "订单总价")
    private Double orderAmount;

    @ApiModelProperty(value = "运费")
    private Double shippingAmount;

    @ApiModelProperty(value = "售后状态")
    private String serviceStatus;

    @ApiModelProperty(value = "产品列表")
    private List<OrderSkuVO> skuList;

    @ApiModelProperty(value = "优惠券列表")
    private List<CouponDO> couponList;

    @ApiModelProperty(value = "赠品列表")
    private List<FullDiscountGiftDO> giftList;

    @ApiModelProperty(value = "订单项 非数据库字段")
    private List<OrderItem> itemList;

    @ApiModelProperty(value = "订单来源")
    private String clientType;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "会员昵称")
    private String memberName;

    @ApiModelProperty(value = "此商品需要提示给顾客的优惠标签")
    private List<String> promotionTags;


    @ApiModelProperty(value = "自动取消剩余秒数，如果已经超时会为0")
    private Long cancelLeftTime;

    public Long getCancelLeftTime() {
        return cancelLeftTime;
    }

    public void setCancelLeftTime(Long cancelLeftTime) {
        this.cancelLeftTime = cancelLeftTime;
    }

    /**
     * @see OrderTypeEnum
     * 因增加拼团业务新增订单类型字段 kingapex 2019/1/28
     */
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    /**
     * 因增加拼团业务新增订单类型字段 kingapex 2019/1/28
     */
    @ApiModelProperty(value = "还差几人成团，如果为0则应该不显示")
    private int waitingGroupNums;

    /**
     * 拼团订单状态
     */
    @ApiModelProperty(value = "拼团订单状态")
    private String pingTuanStatus;

    @ApiModelProperty(value = "交易编号")
    private String tradeSn;

    @ApiModelProperty(value = "优惠金额")
    private Double discountPrice;

    @ApiModelProperty(value = "付款方式")
    private String paymentMethodName;

    @ApiModelProperty(value = "支付时间")
    private Long paymentTime;

    @ApiModelProperty(value = "已支付金额")
    private Double payMoney;

    @ApiModelProperty(value = "收货人手机")
    private String shipMobile;

    @ApiModelProperty(value = "配送地区-省份")
    private String shipProvince;

    @ApiModelProperty(value = "配送地区-城市")
    private String shipCity;

    @ApiModelProperty(value = "配送地区-区(县)")
    private String shipCounty;

    @ApiModelProperty(value = "配送街道")
    private String shipTown;

    @ApiModelProperty(value = "收货地址")
    private String shipAddr;

    @ApiModelProperty(value = "物流公司名称")
    private String logiName;

    @ApiModelProperty(value = "发货单号")
    private String shipNo;

    @ApiModelProperty(value = "签收时间")
    private Long signingTime;

    @ApiModelProperty(value = "完成时间")
    private Long completeTime;

    @ApiModelProperty(value = "订单取消原因")
    private String cancelReason;

    @ApiModelProperty(value = "订单备注")
    private String remark;
    /**
     * 订单是否支持原路退回
     * 未支付的订单为false
     */
    @ApiModelProperty(name = "is_retrace", value = "订单是否支持原路退回")
    private Boolean isRetrace;

    @ApiModelProperty(name = "is_retrace_balance", value = "订单是否退款到预存款")
    private Boolean isRetraceBalance;

    @ApiModelProperty(name = "need_receipt", value = "是否需要发票,0：否，1：是")
    private Integer needReceipt;

    @ApiModelProperty(name = "balance", value = "订单预存款支付金额")
    private Double balance;


    public List<String> getPromotionTags() {
        return promotionTags;
    }

    public void setPromotionTags(List<String> promotionTags) {
        this.promotionTags = promotionTags;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }

    public String getOrderStatusText() {
        return orderStatusText;
    }

    public void setOrderStatusText(String orderStatusText) {
        this.orderStatusText = orderStatusText;
    }

    public String getPayStatusText() {
        return payStatusText;
    }

    public void setPayStatusText(String payStatusText) {
        this.payStatusText = payStatusText;
    }

    public String getShipStatusText() {
        return shipStatusText;
    }

    public void setShipStatusText(String shipStatusText) {
        this.shipStatusText = shipStatusText;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getShipStatus() {
        return shipStatus;
    }

    public void setShipStatus(String shipStatus) {
        this.shipStatus = shipStatus;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public OrderOperateAllowable getOrderOperateAllowableVO() {
        return orderOperateAllowableVO;
    }

    public void setOrderOperateAllowableVO(OrderOperateAllowable orderOperateAllowableVO) {
        this.orderOperateAllowableVO = orderOperateAllowableVO;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getShipTime() {
        return shipTime;
    }

    public void setShipTime(Long shipTime) {
        this.shipTime = shipTime;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Double getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(Double shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public List<OrderSkuVO> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<OrderSkuVO> skuList) {
        this.skuList = skuList;
    }

    public List<CouponDO> getCouponList() {
        return couponList;
    }

    public void setCouponList(List<CouponDO> couponList) {
        this.couponList = couponList;
    }

    public List<FullDiscountGiftDO> getGiftList() {
        return giftList;
    }

    public void setGiftList(List<FullDiscountGiftDO> giftList) {
        this.giftList = giftList;
    }

    public List<OrderItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<OrderItem> itemList) {
        this.itemList = itemList;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
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

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getWaitingGroupNums() {
        return waitingGroupNums;
    }

    public void setWaitingGroupNums(int waitingGroupNums) {
        this.waitingGroupNums = waitingGroupNums;
    }

    public String getPingTuanStatus() {
        return pingTuanStatus;
    }

    public void setPingTuanStatus(String pingTuanStatus) {
        this.pingTuanStatus = pingTuanStatus;
    }

    public String getTradeSn() {
        return tradeSn;
    }

    public void setTradeSn(String tradeSn) {
        this.tradeSn = tradeSn;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public Long getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Long paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Double getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Double payMoney) {
        this.payMoney = payMoney;
    }

    public String getShipMobile() {
        return shipMobile;
    }

    public void setShipMobile(String shipMobile) {
        this.shipMobile = shipMobile;
    }

    public String getShipProvince() {
        return shipProvince;
    }

    public void setShipProvince(String shipProvince) {
        this.shipProvince = shipProvince;
    }

    public String getShipCity() {
        return shipCity;
    }

    public void setShipCity(String shipCity) {
        this.shipCity = shipCity;
    }

    public String getShipCounty() {
        return shipCounty;
    }

    public void setShipCounty(String shipCounty) {
        this.shipCounty = shipCounty;
    }

    public String getShipTown() {
        return shipTown;
    }

    public void setShipTown(String shipTown) {
        this.shipTown = shipTown;
    }

    public String getShipAddr() {
        return shipAddr;
    }

    public void setShipAddr(String shipAddr) {
        this.shipAddr = shipAddr;
    }

    public String getLogiName() {
        return logiName;
    }

    public void setLogiName(String logiName) {
        this.logiName = logiName;
    }

    public String getShipNo() {
        return shipNo;
    }

    public void setShipNo(String shipNo) {
        this.shipNo = shipNo;
    }

    public Long getSigningTime() {
        return signingTime;
    }

    public void setSigningTime(Long signingTime) {
        this.signingTime = signingTime;
    }

    public Long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Long completeTime) {
        this.completeTime = completeTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getIsRetrace() {
        return isRetrace;
    }

    public void setIsRetrace(Boolean isRetrace) {
        this.isRetrace = isRetrace;
    }

    public Boolean getIsRetraceBalance() {
        return isRetraceBalance;
    }

    public void setIsRetraceBalance(Boolean retraceBalance) {
        isRetraceBalance = retraceBalance;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "OrderLineVO{" +
                "orderId=" + orderId +
                ", sn='" + sn + '\'' +
                ", sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", shippingType='" + shippingType + '\'' +
                ", paymentName='" + paymentName + '\'' +
                ", orderStatusText='" + orderStatusText + '\'' +
                ", payStatusText='" + payStatusText + '\'' +
                ", shipStatusText='" + shipStatusText + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", shipStatus='" + shipStatus + '\'' +
                ", commentStatus='" + commentStatus + '\'' +
                ", orderOperateAllowableVO=" + orderOperateAllowableVO +
                ", paymentType='" + paymentType + '\'' +
                ", shipName='" + shipName + '\'' +
                ", createTime=" + createTime +
                ", shipTime=" + shipTime +
                ", orderAmount=" + orderAmount +
                ", shippingAmount=" + shippingAmount +
                ", serviceStatus='" + serviceStatus + '\'' +
                ", skuList=" + skuList +
                ", couponList=" + couponList +
                ", giftList=" + giftList +
                ", itemList=" + itemList +
                ", clientType='" + clientType + '\'' +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", promotionTags=" + promotionTags +
                ", cancelLeftTime=" + cancelLeftTime +
                ", orderType='" + orderType + '\'' +
                ", waitingGroupNums=" + waitingGroupNums +
                ", pingTuanStatus='" + pingTuanStatus + '\'' +
                ", tradeSn='" + tradeSn + '\'' +
                ", discountPrice=" + discountPrice +
                ", paymentMethodName='" + paymentMethodName + '\'' +
                ", paymentTime=" + paymentTime +
                ", payMoney=" + payMoney +
                ", shipMobile='" + shipMobile + '\'' +
                ", shipProvince='" + shipProvince + '\'' +
                ", shipCity='" + shipCity + '\'' +
                ", shipCounty='" + shipCounty + '\'' +
                ", shipTown='" + shipTown + '\'' +
                ", shipAddr='" + shipAddr + '\'' +
                ", logiName='" + logiName + '\'' +
                ", shipNo='" + shipNo + '\'' +
                ", signingTime=" + signingTime +
                ", completeTime=" + completeTime +
                ", cancelReason='" + cancelReason + '\'' +
                ", remark='" + remark + '\'' +
                ", isRetrace=" + isRetrace +
                ", isRetraceBalance=" + isRetraceBalance +
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
        OrderLineVO that = (OrderLineVO) o;

        return new EqualsBuilder()
                .append(waitingGroupNums, that.waitingGroupNums)
                .append(orderId, that.orderId)
                .append(sn, that.sn)
                .append(sellerId, that.sellerId)
                .append(sellerName, that.sellerName)
                .append(shippingType, that.shippingType)
                .append(paymentName, that.paymentName)
                .append(orderStatusText, that.orderStatusText)
                .append(payStatusText, that.payStatusText)
                .append(shipStatusText, that.shipStatusText)
                .append(orderStatus, that.orderStatus)
                .append(payStatus, that.payStatus)
                .append(shipStatus, that.shipStatus)
                .append(commentStatus, that.commentStatus)
                .append(orderOperateAllowableVO, that.orderOperateAllowableVO)
                .append(paymentType, that.paymentType)
                .append(shipName, that.shipName)
                .append(createTime, that.createTime)
                .append(shipTime, that.shipTime)
                .append(orderAmount, that.orderAmount)
                .append(shippingAmount, that.shippingAmount)
                .append(serviceStatus, that.serviceStatus)
                .append(skuList, that.skuList)
                .append(couponList, that.couponList)
                .append(giftList, that.giftList)
                .append(itemList, that.itemList)
                .append(clientType, that.clientType)
                .append(memberId, that.memberId)
                .append(memberName, that.memberName)
                .append(promotionTags, that.promotionTags)
                .append(cancelLeftTime, that.cancelLeftTime)
                .append(orderType, that.orderType)
                .append(pingTuanStatus, that.pingTuanStatus)
                .append(tradeSn, that.tradeSn)
                .append(discountPrice, that.discountPrice)
                .append(paymentMethodName, that.paymentMethodName)
                .append(paymentTime, that.paymentTime)
                .append(payMoney, that.payMoney)
                .append(shipMobile, that.shipMobile)
                .append(shipProvince, that.shipProvince)
                .append(shipCity, that.shipCity)
                .append(shipCounty, that.shipCounty)
                .append(shipTown, that.shipTown)
                .append(shipAddr, that.shipAddr)
                .append(logiName, that.logiName)
                .append(shipNo, that.shipNo)
                .append(signingTime, that.signingTime)
                .append(completeTime, that.completeTime)
                .append(cancelReason, that.cancelReason)
                .append(remark, that.remark)
                .append(isRetrace, that.isRetrace)
                .append(isRetraceBalance, that.isRetraceBalance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(orderId)
                .append(sn)
                .append(sellerId)
                .append(sellerName)
                .append(shippingType)
                .append(paymentName)
                .append(orderStatusText)
                .append(payStatusText)
                .append(shipStatusText)
                .append(orderStatus)
                .append(payStatus)
                .append(shipStatus)
                .append(commentStatus)
                .append(orderOperateAllowableVO)
                .append(paymentType)
                .append(shipName)
                .append(createTime)
                .append(shipTime)
                .append(orderAmount)
                .append(shippingAmount)
                .append(serviceStatus)
                .append(skuList)
                .append(couponList)
                .append(giftList)
                .append(itemList)
                .append(clientType)
                .append(memberId)
                .append(memberName)
                .append(promotionTags)
                .append(cancelLeftTime)
                .append(orderType)
                .append(waitingGroupNums)
                .append(pingTuanStatus)
                .append(tradeSn)
                .append(discountPrice)
                .append(paymentMethodName)
                .append(paymentTime)
                .append(payMoney)
                .append(shipMobile)
                .append(shipProvince)
                .append(shipCity)
                .append(shipCounty)
                .append(shipTown)
                .append(shipAddr)
                .append(logiName)
                .append(shipNo)
                .append(signingTime)
                .append(completeTime)
                .append(cancelReason)
                .append(remark)
                .append(isRetrace)
                .append(isRetraceBalance)
                .toHashCode();
    }

    /**
     * 空构造器
     */
    public OrderLineVO() {

    }


    /**
     * 参数赋值构造器
     *
     * @param orderDO
     */
    public OrderLineVO(OrderDO orderDO) {
        this.orderId = orderDO.getOrderId();
        this.sn = orderDO.getSn();
        this.sellerId = orderDO.getSellerId();
        this.sellerName = orderDO.getSellerName();
        this.shippingType = orderDO.getShippingType();
        this.paymentName = orderDO.getPaymentMethodName();

        //先从特殊的流程-状态显示 定义中读取，如果为空说明不是特殊的状态，直接显示为 状态对应的提示词
        orderStatusText = OrderSpecialStatus.getStatusText(orderDO.getOrderType(), orderDO.getPaymentType(), orderDO.getOrderStatus());
        if (StringUtil.isEmpty(orderStatusText)) {
            orderStatusText = OrderStatusEnum.valueOf(orderDO.getOrderStatus()).description();
        }

        this.payStatusText = PayStatusEnum.valueOf(orderDO.getPayStatus()).description();
        this.shipStatusText = ShipStatusEnum.valueOf(orderDO.getShipStatus()).description();

        this.orderStatus = orderDO.getOrderStatus();
        this.payStatus = orderDO.getPayStatus();
        this.shipStatus = orderDO.getShipStatus();
        this.commentStatus = orderDO.getCommentStatus();
        this.serviceStatus = orderDO.getServiceStatus();

        this.shipName = orderDO.getShipName();
        this.paymentType = orderDO.getPaymentType();

        this.createTime = orderDO.getCreateTime();
        if (orderDO.getShipTime() != null) {
            this.shipTime = orderDO.getShipTime();
        }
        this.orderAmount = orderDO.getOrderPrice();
        this.shippingAmount = orderDO.getShippingPrice();
        this.paymentType = orderDO.getPaymentType();

        this.skuList = JsonUtil.jsonToList(orderDO.getItemsJson(), OrderSkuVO.class);
        this.clientType = orderDO.getClientType();

        this.memberId = orderDO.getMemberId();
        this.memberName = orderDO.getMemberName();
        this.orderType = orderDO.getOrderType();


        //遍历订单中所有的商品
        for (OrderSkuVO skuVO : skuList) {
            //设置商品的可操作状态
            skuVO.setGoodsOperateAllowableVO(new GoodsOperateAllowable(ShipStatusEnum.valueOf(this.shipStatus),
                    OrderServiceStatusEnum.valueOf(skuVO.getServiceStatus()), PayStatusEnum.valueOf(this.payStatus),
                    skuVO.getComplainStatus()==null?ComplainSkuStatusEnum.EXPIRED:ComplainSkuStatusEnum.valueOf(skuVO.getComplainStatus())));

        }

        //设置订单的可操作状态
        this.orderOperateAllowableVO = new OrderOperateAllowable(orderDO);

        this.tradeSn = orderDO.getTradeSn();
        this.discountPrice = orderDO.getDiscountPrice();
        this.paymentMethodName = orderDO.getPaymentMethodName();
        this.paymentTime = orderDO.getPaymentTime();
        this.payMoney = orderDO.getPayMoney();
        this.shipMobile = orderDO.getShipMobile();
        this.shipProvince = orderDO.getShipProvince();
        this.shipCity = orderDO.getShipCity();
        this.shipCounty = orderDO.getShipCounty();
        this.shipTown = orderDO.getShipTown();
        this.shipAddr = orderDO.getShipAddr();
        this.logiName = orderDO.getLogiName();
        this.shipNo = orderDO.getShipNo();
        this.signingTime = orderDO.getSigningTime();
        this.completeTime = orderDO.getCompleteTime();
        this.cancelReason = orderDO.getCancelReason();
        this.remark = orderDO.getRemark();
        this.needReceipt = orderDO.getNeedReceipt();
    }

}
