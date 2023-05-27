package com.enation.app.javashop.model.trade.order.dto;

import com.enation.app.javashop.model.member.dos.ReceiptHistory;
import com.enation.app.javashop.model.promotion.coupon.vo.GoodsCouponPrice;
import com.enation.app.javashop.model.trade.cart.dos.CartDO;
import com.enation.app.javashop.model.trade.order.enums.OrderTypeEnum;
import com.enation.app.javashop.model.trade.order.vo.ConsigneeVO;
import com.enation.app.javashop.model.trade.order.vo.OrderParam;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.io.Serializable;
import java.util.List;

/**
 * 订单DTO
 *
 * @author kingapex
 * @version 1.0
 * @since v7.0.0 2017年3月22日下午9:28:30
 */
@SuppressWarnings("AlibabaPojoMustOverrideToString")
@ApiIgnore
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderDTO extends CartDO implements Serializable {

    private static final long serialVersionUID = 8206833000476657708L;

    @ApiModelProperty(value = "交易编号")
    private String tradeSn;

    @ApiModelProperty(value = "订单编号")
    private String sn;

    @ApiModelProperty(value = "收货信息")
    private ConsigneeVO consignee;

    @ApiModelProperty(value = "配送方式")
    private Integer shippingId;

    @ApiModelProperty(value = "支付方式")
    private String paymentType;

    @ApiModelProperty(value = "发货时间")
    private Long shipTime;

    @ApiModelProperty(value = "发货时间类型")
    private String receiveTime;

    @ApiModelProperty(value = "会员id")
    private Long memberId;

    @ApiModelProperty(value = "会员姓名")
    private String memberName;

    @ApiModelProperty(value = "订单备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;


    @ApiModelProperty(value = "配送方式名称")
    private String shippingType;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "付款状态")
    private String payStatus;

    @ApiModelProperty(value = "配送状态")
    private String shipStatus;

    @ApiModelProperty(value = "收货人姓名")
    private String shipName;

    @ApiModelProperty(value = "订单价格")
    private Double orderPrice;

    @ApiModelProperty(value = "配送费")
    private Double shippingPrice;

    @ApiModelProperty(value = "评论状态")
    private String commentStatus;

    @ApiModelProperty(value = "是否已经删除")
    private Integer disabled;

    @ApiModelProperty(value = "支付方式id")
    private Integer paymentMethodId;

    @ApiModelProperty(value = "支付插件id")
    private String paymentPluginId;

    @ApiModelProperty(value = "支付方式名称")
    private String paymentMethodName;

    @ApiModelProperty(value = "付款账号")
    private String paymentAccount;

    @ApiModelProperty(value = "商品数量")
    private Integer goodsNum;

    @ApiModelProperty(value = "发货仓库id")
    private Integer warehouseId;

    @ApiModelProperty(value = "取消原因")
    private String cancelReason;

    @ApiModelProperty(value = "收货地址省Id")
    private Integer shipProvinceId;

    @ApiModelProperty(value = "收货地址市Id")
    private Integer shipCityId;

    @ApiModelProperty(value = "收货地址区Id")
    private Integer shipRegionId;

    @ApiModelProperty(value = "收货地址街道Id")
    private Integer shipTownId;

    @ApiModelProperty(value = "收货省")
    private String shipProvince;

    @ApiModelProperty(value = "收货地址市")
    private String shipCity;

    @ApiModelProperty(value = "收货地址区")
    private String shipRegion;

    @ApiModelProperty(value = "收货地址街道")
    private String shipTown;

    @ApiModelProperty(value = "签收时间")
    private Long signingTime;

    @ApiModelProperty(value = "签收人姓名")
    private String theSign;

    @ApiModelProperty(value = "管理员备注")
    private String adminRemark;

    @ApiModelProperty(value = "收货地址id")
    private Long addressId;

    @ApiModelProperty(value = "应付金额")
    private Double needPayMoney;

    @ApiModelProperty(value = "发货单号")
    private String shipNo;

    @ApiModelProperty(value = "物流公司Id")
    private Integer logiId;

    @ApiModelProperty(value = "物流公司名称")
    private String logiName;

    @ApiModelProperty(value = "是否需要发票")
    private Integer needReceipt;

    @ApiModelProperty(value = "抬头")
    private String receiptTitle;

    @ApiModelProperty(value = "内容")
    private String receiptContent;

    @ApiModelProperty(value = "售后状态")
    private String serviceStatus;

    @ApiModelProperty(value = "订单来源")
    private String clientType;
    @ApiModelProperty(value = "发票信息")
    private ReceiptHistory receiptHistory;

    /**
     * @see OrderTypeEnum
     * 因增加拼团业务新增订单类型字段 kingapex 2019/1/28 on v7.1.0
     */
    @ApiModelProperty(value = "订单类型")
    private String orderType;


    /**
     * 订单的扩展数据
     * 为了增加订单的扩展性，个性化的业务可以将个性化数据（如拼团所差人数）存在此字段 kingapex 2019/1/28 on v7.1.0
     */
    @ApiModelProperty(value = "扩展数据",hidden = true)
    private String orderData;


    /**
     * 使用优惠券的商品
     */
    @ApiModelProperty(value = "使用优惠券的商品",hidden = true)
    private List<GoodsCouponPrice> goodsCouponPrices;

    @ApiModelProperty(value = "订单sku信息",hidden = true)
    private List<OrderSkuVO> orderSkuList;

    public void setOrderData(String orderData) {
        this.orderData = orderData;
    }

    /**
     * 无参构造器
     */
    public OrderDTO() {

    }


    /**
     * 用一个购物车购造订单
     *
     * @param cart
     */
    public OrderDTO(CartDO cart) {

        super(cart.getSellerId(), cart.getSellerName());

        // 初始化产品及优惠数据
        this.setWeight(cart.getWeight());
        this.setPrice(cart.getPrice());
        this.setSkuList(cart.getSkuList());
        this.setGiftJson(cart.getGiftJson());
        this.setGiftPoint(cart.getGiftPoint());
        this.orderType= OrderTypeEnum.NORMAL.name();

    }

    public OrderDTO(OrderParam orderParam){
        super(orderParam.getSellerId(), orderParam.getSellerName());

        // 初始化产品及优惠数据
        this.setWeight(orderParam.getWeight());
        this.setPrice(orderParam.getPrice());
        this.setGiftJson(orderParam.getGiftJson());
        this.setGiftPoint(orderParam.getGiftPoint());
        //购买的会员信息
        this.setMemberId(orderParam.getMemberId());
        this.setMemberName(orderParam.getMemberName());
        //交易号
        this.setTradeSn(orderParam.getTradeSn());

        this.setConsignee(orderParam.getConsignee());
        //设置配送方式
        this.setShippingId(orderParam.getShippingId());
        this.setShippingType(orderParam.getShippingType());

        //支付类型
        this.setPaymentType(orderParam.getPaymentType());
        //发票
        this.setNeedReceipt(orderParam.getNeedReceipt());
        this.setReceiptHistory(orderParam.getReceiptHistory());
        //收货时间
        this.setReceiveTime(orderParam.getReceiveTime());
        //订单备注
        this.setRemark(orderParam.getRemark());

        //订单来源
        this.setClientType(orderParam.getClientType());

        //支付方式
        this.setPaymentType(orderParam.getPaymentType());
        //备注
        this.setRemark(orderParam.getRemark());

        this.setOrderType(orderParam.getOrderType());

        this.setGoodsCouponPrices(orderParam.getCouponGoodsList());
    }

    public List<OrderSkuVO> getOrderSkuList() {
        return orderSkuList;
    }

    public void setOrderSkuList(List<OrderSkuVO> orderSkuList) {
        this.orderSkuList = orderSkuList;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "tradeSn='" + tradeSn + '\'' +
                ", sn='" + sn + '\'' +
                ", consignee=" + consignee +
                ", shippingId=" + shippingId +
                ", paymentType='" + paymentType + '\'' +
                ", shipTime=" + shipTime +
                ", receiveTime='" + receiveTime + '\'' +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", shippingType='" + shippingType + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", shipStatus='" + shipStatus + '\'' +
                ", shipName='" + shipName + '\'' +
                ", orderPrice=" + orderPrice +
                ", shippingPrice=" + shippingPrice +
                ", commentStatus='" + commentStatus + '\'' +
                ", disabled=" + disabled +
                ", paymentMethodId=" + paymentMethodId +
                ", paymentPluginId='" + paymentPluginId + '\'' +
                ", paymentMethodName='" + paymentMethodName + '\'' +
                ", paymentAccount='" + paymentAccount + '\'' +
                ", goodsNum=" + goodsNum +
                ", warehouseId=" + warehouseId +
                ", cancelReason='" + cancelReason + '\'' +
                ", shipProvinceId=" + shipProvinceId +
                ", shipCityId=" + shipCityId +
                ", shipRegionId=" + shipRegionId +
                ", shipTownId=" + shipTownId +
                ", shipProvince='" + shipProvince + '\'' +
                ", shipCity='" + shipCity + '\'' +
                ", shipRegion='" + shipRegion + '\'' +
                ", shipTown='" + shipTown + '\'' +
                ", signingTime=" + signingTime +
                ", theSign='" + theSign + '\'' +
                ", adminRemark='" + adminRemark + '\'' +
                ", addressId=" + addressId +
                ", needPayMoney=" + needPayMoney +
                ", shipNo='" + shipNo + '\'' +
                ", logiId=" + logiId +
                ", logiName='" + logiName + '\'' +
                ", needReceipt=" + needReceipt +
                ", receiptTitle='" + receiptTitle + '\'' +
                ", receiptContent='" + receiptContent + '\'' +
                ", serviceStatus='" + serviceStatus + '\'' +
                ", clientType='" + clientType + '\'' +
                ", receiptHistory=" + receiptHistory +
                ", orderType='" + orderType + '\'' +
                ", orderData='" + orderData + '\'' +
                ", goodsCouponPrices=" + goodsCouponPrices +
                ", orderSkuList=" + orderSkuList +
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
        OrderDTO that = (OrderDTO) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(tradeSn, that.tradeSn)
                .append(sn, that.sn)
                .append(consignee, that.consignee)
                .append(shippingId, that.shippingId)
                .append(paymentType, that.paymentType)
                .append(shipTime, that.shipTime)
                .append(receiveTime, that.receiveTime)
                .append(memberId, that.memberId)
                .append(memberName, that.memberName)
                .append(remark, that.remark)
                .append(createTime, that.createTime)
                .append(shippingType, that.shippingType)
                .append(orderStatus, that.orderStatus)
                .append(payStatus, that.payStatus)
                .append(shipStatus, that.shipStatus)
                .append(shipName, that.shipName)
                .append(orderPrice, that.orderPrice)
                .append(shippingPrice, that.shippingPrice)
                .append(commentStatus, that.commentStatus)
                .append(disabled, that.disabled)
                .append(paymentMethodId, that.paymentMethodId)
                .append(paymentPluginId, that.paymentPluginId)
                .append(paymentMethodName, that.paymentMethodName)
                .append(paymentAccount, that.paymentAccount)
                .append(goodsNum, that.goodsNum)
                .append(warehouseId, that.warehouseId)
                .append(cancelReason, that.cancelReason)
                .append(shipProvinceId, that.shipProvinceId)
                .append(shipCityId, that.shipCityId)
                .append(shipRegionId, that.shipRegionId)
                .append(shipTownId, that.shipTownId)
                .append(shipProvince, that.shipProvince)
                .append(shipCity, that.shipCity)
                .append(shipRegion, that.shipRegion)
                .append(shipTown, that.shipTown)
                .append(signingTime, that.signingTime)
                .append(theSign, that.theSign)
                .append(adminRemark, that.adminRemark)
                .append(addressId, that.addressId)
                .append(needPayMoney, that.needPayMoney)
                .append(shipNo, that.shipNo)
                .append(logiId, that.logiId)
                .append(logiName, that.logiName)
                .append(needReceipt, that.needReceipt)
                .append(receiptTitle, that.receiptTitle)
                .append(receiptContent, that.receiptContent)
                .append(serviceStatus, that.serviceStatus)
                .append(clientType, that.clientType)
                .append(receiptHistory, that.receiptHistory)
                .append(orderType, that.orderType)
                .append(orderData, that.orderData)
                .append(goodsCouponPrices, that.goodsCouponPrices)
                .append(orderSkuList, that.orderSkuList)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(tradeSn)
                .append(sn)
                .append(consignee)
                .append(shippingId)
                .append(paymentType)
                .append(shipTime)
                .append(receiveTime)
                .append(memberId)
                .append(memberName)
                .append(remark)
                .append(createTime)
                .append(shippingType)
                .append(orderStatus)
                .append(payStatus)
                .append(shipStatus)
                .append(shipName)
                .append(orderPrice)
                .append(shippingPrice)
                .append(commentStatus)
                .append(disabled)
                .append(paymentMethodId)
                .append(paymentPluginId)
                .append(paymentMethodName)
                .append(paymentAccount)
                .append(goodsNum)
                .append(warehouseId)
                .append(cancelReason)
                .append(shipProvinceId)
                .append(shipCityId)
                .append(shipRegionId)
                .append(shipTownId)
                .append(shipProvince)
                .append(shipCity)
                .append(shipRegion)
                .append(shipTown)
                .append(signingTime)
                .append(theSign)
                .append(adminRemark)
                .append(addressId)
                .append(needPayMoney)
                .append(shipNo)
                .append(logiId)
                .append(logiName)
                .append(needReceipt)
                .append(receiptTitle)
                .append(receiptContent)
                .append(serviceStatus)
                .append(clientType)
                .append(receiptHistory)
                .append(orderType)
                .append(orderData)
                .append(goodsCouponPrices)
                .append(orderSkuList)
                .toHashCode();
    }

    public String getTradeSn() {
        return tradeSn;
    }

    public void setTradeSn(String tradeSn) {
        this.tradeSn = tradeSn;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public ConsigneeVO getConsignee() {
        return consignee;
    }

    public void setConsignee(ConsigneeVO consignee) {
        this.consignee = consignee;
    }

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public Long getShipTime() {
        return shipTime;
    }

    public void setShipTime(Long shipTime) {
        this.shipTime = shipTime;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
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

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Double getShippingPrice() {
        return shippingPrice;
    }

    public void setShippingPrice(Double shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getPaymentPluginId() {
        return paymentPluginId;
    }

    public void setPaymentPluginId(String paymentPluginId) {
        this.paymentPluginId = paymentPluginId;
    }

    public String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }

    public String getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Integer getShipProvinceId() {
        return shipProvinceId;
    }

    public void setShipProvinceId(Integer shipProvinceId) {
        this.shipProvinceId = shipProvinceId;
    }

    public Integer getShipCityId() {
        return shipCityId;
    }

    public void setShipCityId(Integer shipCityId) {
        this.shipCityId = shipCityId;
    }

    public Integer getShipRegionId() {
        return shipRegionId;
    }

    public void setShipRegionId(Integer shipRegionId) {
        this.shipRegionId = shipRegionId;
    }

    public Integer getShipTownId() {
        return shipTownId;
    }

    public void setShipTownId(Integer shipTownId) {
        this.shipTownId = shipTownId;
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

    public String getShipRegion() {
        return shipRegion;
    }

    public void setShipRegion(String shipRegion) {
        this.shipRegion = shipRegion;
    }

    public String getShipTown() {
        return shipTown;
    }

    public void setShipTown(String shipTown) {
        this.shipTown = shipTown;
    }

    public Long getSigningTime() {
        return signingTime;
    }

    public void setSigningTime(Long signingTime) {
        this.signingTime = signingTime;
    }

    public String getTheSign() {
        return theSign;
    }

    public void setTheSign(String theSign) {
        this.theSign = theSign;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(String adminRemark) {
        this.adminRemark = adminRemark;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Double getNeedPayMoney() {
        return needPayMoney;
    }

    public void setNeedPayMoney(Double needPayMoney) {
        this.needPayMoney = needPayMoney;
    }

    public String getShipNo() {
        return shipNo;
    }

    public void setShipNo(String shipNo) {
        this.shipNo = shipNo;
    }

    public Integer getLogiId() {
        return logiId;
    }

    public void setLogiId(Integer logiId) {
        this.logiId = logiId;
    }

    public String getLogiName() {
        return logiName;
    }

    public void setLogiName(String logiName) {
        this.logiName = logiName;
    }

    public Integer getNeedReceipt() {
        return needReceipt;
    }

    public void setNeedReceipt(Integer needReceipt) {
        this.needReceipt = needReceipt;
    }

    public String getReceiptTitle() {
        return receiptTitle;
    }

    public void setReceiptTitle(String receiptTitle) {
        this.receiptTitle = receiptTitle;
    }

    public String getReceiptContent() {
        return receiptContent;
    }

    public void setReceiptContent(String receiptContent) {
        this.receiptContent = receiptContent;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public ReceiptHistory getReceiptHistory() {
        return receiptHistory;
    }

    public void setReceiptHistory(ReceiptHistory receiptHistory) {
        this.receiptHistory = receiptHistory;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderData() {
        return orderData;
    }

    public List<GoodsCouponPrice> getGoodsCouponPrices() {
        return goodsCouponPrices;
    }

    public void setGoodsCouponPrices(List<GoodsCouponPrice> goodsCouponPrices) {
        this.goodsCouponPrices = goodsCouponPrices;
    }
}
