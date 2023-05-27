package com.enation.app.javashop.model.trade.order.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.framework.database.annotation.Column;
import com.enation.app.javashop.framework.database.annotation.PrimaryKeyField;
import com.enation.app.javashop.framework.util.JsonUtil;
import com.enation.app.javashop.model.trade.cart.vo.PriceDetailVO;
import com.enation.app.javashop.model.trade.complain.enums.ComplainSkuStatusEnum;
import com.enation.app.javashop.model.trade.order.dto.OrderDTO;
import com.enation.app.javashop.model.trade.order.enums.*;
import com.enation.app.javashop.model.trade.order.vo.ConsigneeVO;
import com.enation.app.javashop.model.trade.order.vo.OrderParam;
import com.enation.app.javashop.model.trade.order.vo.OrderSkuVO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;


/**
 * 订单表实体
 *
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-05-10 16:13:37
 */
@TableName("es_order")
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderDO implements Serializable {

    private static final long serialVersionUID = 3154292647603519L;

    /**
     * 主键ID
     */
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(hidden = true)
    private Long orderId;

    /**
     * 交易编号
     */
    @Column(name = "trade_sn")
    @ApiModelProperty(name = "trade_sn", value = "交易编号", required = false)
    private String tradeSn;
    /**
     * 订单编号
     */
    @Column(name = "sn")
    @ApiModelProperty(name = "sn", value = "订单编号", required = false)
    private String sn;
    /**
     * 店铺ID
     */
    @Column(name = "seller_id")
    @ApiModelProperty(name = "seller_id", value = "店铺ID", required = false)
    private Long sellerId;
    /**
     * 店铺名称
     */
    @Column(name = "seller_name")
    @ApiModelProperty(name = "seller_name", value = "店铺名称", required = false)
    private String sellerName;
    /**
     * 会员ID
     */
    @Column(name = "member_id")
    @ApiModelProperty(name = "member_id", value = "会员ID", required = false)
    private Long memberId;
    /**
     * 买家账号
     */
    @Column(name = "member_name")
    @ApiModelProperty(name = "member_name", value = "买家账号", required = false)
    private String memberName;
    /**
     * 订单状态
     */
    @Column(name = "order_status")
    @ApiModelProperty(name = "order_status", value = "订单状态", required = false)
    private String orderStatus;
    /**
     * 付款状态
     */
    @Column(name = "pay_status")
    @ApiModelProperty(name = "pay_status", value = "付款状态", required = false)
    private String payStatus;
    /**
     * 货运状态
     */
    @Column(name = "ship_status")
    @ApiModelProperty(name = "ship_status", value = "货运状态", required = false)
    private String shipStatus;
    /**
     * 配送方式ID
     */
    @Column(name = "shipping_id")
    @ApiModelProperty(name = "shipping_id", value = "配送方式ID", required = false)
    private Integer shippingId;
    /**
     * 评论是否完成
     */
    @Column(name = "comment_status")
    @ApiModelProperty(name = "comment_status", value = "评论是否完成", required = false)
    private String commentStatus;
    /**
     * 配送方式
     */
    @Column(name = "shipping_type")
    @ApiModelProperty(name = "shipping_type", value = "配送方式", required = false)
    private String shippingType;
    /**
     * 支付方式id
     */
    @Column(name = "payment_method_id")
    @ApiModelProperty(name = "payment_method_id", value = "支付方式id", required = false)
    private Long paymentMethodId;
    /**
     * 支付插件id
     */
    @Column(name = "payment_plugin_id")
    @ApiModelProperty(name = "payment_plugin_id", value = "支付插件id", required = false)
    private String paymentPluginId;
    /**
     * 支付方式名称
     */
    @Column(name = "payment_method_name")
    @ApiModelProperty(name = "payment_method_name", value = "支付方式名称", required = false)
    private String paymentMethodName;
    /**
     * 支付方式类型
     */
    @Column(name = "payment_type")
    @ApiModelProperty(name = "payment_type", value = "支付方式类型", required = false)
    private String paymentType;
    /**
     * 支付时间
     */
    @Column(name = "payment_time")
    @ApiModelProperty(name = "payment_time", value = "支付时间", required = false)
    private Long paymentTime;
    /**
     * 已支付金额
     */
    @Column(name = "pay_money")
    @ApiModelProperty(name = "pay_money", value = "已支付金额", required = false)
    private Double payMoney;
    /**
     * 收货人姓名
     */
    @Column(name = "ship_name")
    @ApiModelProperty(name = "ship_name", value = "收货人姓名", required = false)
    private String shipName;
    /**
     * 收货地址
     */
    @Column(name = "ship_addr")
    @ApiModelProperty(name = "ship_addr", value = "收货地址", required = false)
    private String shipAddr;
    /**
     * 收货人邮编
     */
    @Column(name = "ship_zip")
    @ApiModelProperty(name = "ship_zip", value = "收货人邮编", required = false)
    private String shipZip;
    /**
     * 收货人手机
     */
    @Column(name = "ship_mobile")
    @ApiModelProperty(name = "ship_mobile", value = "收货人手机", required = false)
    private String shipMobile;
    /**
     * 收货人电话
     */
    @Column(name = "ship_tel")
    @ApiModelProperty(name = "ship_tel", value = "收货人电话", required = false)
    private String shipTel;
    /**
     * 收货时间
     */
    @Column(name = "receive_time")
    @ApiModelProperty(name = "receive_time", value = "收货时间", required = false)
    private String receiveTime;
    /**
     * 配送地区-省份ID
     */
    @Column(name = "ship_province_id")
    @ApiModelProperty(name = "ship_province_id", value = "配送地区-省份ID", required = false)
    private Long shipProvinceId;

    /**
     * 配送地区-城市ID
     */
    @Column(name = "ship_city_id")
    @ApiModelProperty(name = "ship_city_id", value = "配送地区-城市ID", required = false)
    private Long shipCityId;

    /**
     * 配送地区-区(县)ID
     */
    @Column(name = "ship_county_id")
    @ApiModelProperty(name = "ship_county_id", value = "配送地区-区(县)ID", required = false)
    private Long shipCountyId;

    /**
     * 配送街道id
     */
    @Column(name = "ship_town_id")
    @ApiModelProperty(name = "ship_town_id", value = "配送街道id", required = false)
    private Long shipTownId;
    /**
     * 配送地区-省份
     */
    @Column(name = "ship_province")
    @ApiModelProperty(name = "ship_province", value = "配送地区-省份", required = false)
    private String shipProvince;
    /**
     * 配送地区-城市
     */
    @Column(name = "ship_city")
    @ApiModelProperty(name = "ship_city", value = "配送地区-城市", required = false)
    private String shipCity;
    /**
     * 配送地区-区(县)
     */
    @Column(name = "ship_county")
    @ApiModelProperty(name = "ship_county", value = "配送地区-区(县)", required = false)
    private String shipCounty;
    /**
     * 配送街道
     */
    @Column(name = "ship_town")
    @ApiModelProperty(name = "ship_town", value = "配送街道", required = false)
    private String shipTown;
    /**
     * 订单总额
     */
    @Column(name = "order_price")
    @ApiModelProperty(name = "order_price", value = "订单总额", required = false)
    private Double orderPrice;
    /**
     * 商品总额
     */
    @Column(name = "goods_price")
    @ApiModelProperty(name = "goods_price", value = "商品总额", required = false)
    private Double goodsPrice;
    /**
     * 配送费用
     */
    @Column(name = "shipping_price")
    @ApiModelProperty(name = "shipping_price", value = "配送费用", required = false)
    private Double shippingPrice;
    /**
     * 优惠金额
     */
    @Column(name = "discount_price")
    @ApiModelProperty(name = "discount_price", value = "优惠金额", required = false)
    private Double discountPrice;
    /**
     * 是否被删除
     */
    @Column(name = "disabled")
    @ApiModelProperty(name = "disabled", value = "是否被删除", required = false)
    private Integer disabled;
    /**
     * 订单商品总重量
     */
    @Column(name = "weight")
    @ApiModelProperty(name = "weight", value = "订单商品总重量", required = false)
    private Double weight;
    /**
     * 商品数量
     */
    @Column(name = "goods_num")
    @ApiModelProperty(name = "goods_num", value = "商品数量", required = false)
    private Integer goodsNum;
    /**
     * 订单备注
     */
    @Column(name = "remark")
    @ApiModelProperty(name = "remark", value = "订单备注", required = false)
    private String remark;
    /**
     * 订单取消原因
     */
    @Column(name = "cancel_reason")
    @ApiModelProperty(name = "cancel_reason", value = "订单取消原因", required = false)
    private String cancelReason;
    /**
     * 签收人
     */
    @Column(name = "the_sign")
    @ApiModelProperty(name = "the_sign", value = "签收人", required = false)
    private String theSign;

    /**
     * 转换 List<OrderSkuVO>
     */
    @Column(name = "items_json")
    @ApiModelProperty(name = "items_json", value = "货物列表json", required = false)
    private String itemsJson;

    @Column(name = "warehouse_id")
    @ApiModelProperty(name = "warehouse_id", value = "发货仓库ID", required = false)
    private Integer warehouseId;

    @Column(name = "need_pay_money")
    @ApiModelProperty(name = "need_pay_money", value = "应付金额", required = false)
    private Double needPayMoney;

    @Column(name = "ship_no")
    @ApiModelProperty(name = "ship_no", value = "发货单号", required = false)
    private String shipNo;

    /**
     * 收货地址ID
     */
    @Column(name = "address_id")
    @ApiModelProperty(name = "address_id", value = "收货地址ID", required = false)
    private Long addressId;
    /**
     * 管理员备注
     */
    @Column(name = "admin_remark")
    @ApiModelProperty(name = "admin_remark", value = "管理员备注", required = false)
    private Integer adminRemark;
    /**
     * 物流公司ID
     */
    @Column(name = "logi_id")
    @ApiModelProperty(name = "logi_id", value = "物流公司ID", required = false)
    private Long logiId;
    /**
     * 物流公司名称
     */
    @Column(name = "logi_name")
    @ApiModelProperty(name = "logi_name", value = "物流公司名称", required = false)
    private String logiName;

    /**
     * 完成时间
     */
    @Column(name = "complete_time")
    @ApiModelProperty(name = "complete_time", value = "完成时间", required = false)
    private Long completeTime;
    /**
     * 订单创建时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(name = "create_time", value = "订单创建时间", required = false)
    private Long createTime;

    /**
     * 签收时间
     */
    @Column(name = "signing_time")
    @ApiModelProperty(name = "signing_time", value = "签收时间", required = false)
    private Long signingTime;

    /**
     * 送货时间
     */
    @Column(name = "ship_time")
    @ApiModelProperty(name = "ship_time", value = "送货时间", required = false)
    private Long shipTime;

    /**
     * 支付方式返回的交易号
     */
    @Column(name = "pay_order_no")
    @ApiModelProperty(name = "pay_order_no", value = "支付方式返回的交易号", required = false)
    private String payOrderNo;

    /**
     * 售后状态
     */
    @Column(name = "service_status")
    @ApiModelProperty(name = "service_status", value = "售后状态", required = false)
    private String serviceStatus;

    /**
     * 结算状态
     */
    @Column(name = "bill_status")
    @ApiModelProperty(name = "bill_status", value = "结算状态", required = false)
    private Integer billStatus;

    /**
     * 结算单号
     */
    @Column(name = "bill_sn")
    @ApiModelProperty(name = "bill_sn", value = "结算单号", required = false)
    private String billSn;

    /**
     * 订单来源
     */
    @Column(name = "client_type")
    @ApiModelProperty(name = "client_type", value = "订单来源", required = false)
    private String clientType;

    @Column(name = "need_receipt")
    @ApiModelProperty(name = "need_receipt", value = "是否需要发票,0：否，1：是")
    private Integer needReceipt;

    /**
     * @see OrderTypeEnum
     * 因增加拼团业务新增订单类型字段 kingapex 2019/1/28 on v7.1.0
     */
    @ApiModelProperty(value = "订单类型")
    @Column(name = "order_type")
    private String orderType;


    /**
     * 订单的扩展数据
     * 为了增加订单的扩展性，个性化的业务可以将个性化数据（如拼团所差人数）存在此字段 kingapex 2019/1/28 on v7.1.0
     */
    @ApiModelProperty(value = "扩展数据",hidden = true)
    @Column(name = "order_data")
    private String orderData;

    /**
     * 预存款抵扣金额
     */
    @ApiModelProperty(value = "预存款抵扣金额",hidden = true)
    @Column(name = "balance")
    private Double balance;


    @PrimaryKeyField
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
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

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
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

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShipAddr() {
        return shipAddr;
    }

    public void setShipAddr(String shipAddr) {
        this.shipAddr = shipAddr;
    }

    public String getShipZip() {
        return shipZip;
    }

    public void setShipZip(String shipZip) {
        this.shipZip = shipZip;
    }

    public String getShipMobile() {
        return shipMobile;
    }

    public void setShipMobile(String shipMobile) {
        this.shipMobile = shipMobile;
    }

    public String getShipTel() {
        return shipTel;
    }

    public void setShipTel(String shipTel) {
        this.shipTel = shipTel;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Long getShipProvinceId() {
        return shipProvinceId;
    }

    public void setShipProvinceId(Long shipProvinceId) {
        this.shipProvinceId = shipProvinceId;
    }

    public Long getShipCityId() {
        return shipCityId;
    }

    public void setShipCityId(Long shipCityId) {
        this.shipCityId = shipCityId;
    }

    public Long getShipCountyId() {
        return shipCountyId;
    }

    public void setShipCountyId(Long shipCountyId) {
        this.shipCountyId = shipCountyId;
    }

    public Long getShipTownId() {
        return shipTownId;
    }

    public void setShipTownId(Long shipTownId) {
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

    public Double getOrderPrice() {
        if (orderPrice == null) {
            orderPrice = 0.0d;
        }
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Double getGoodsPrice() {
        if (goodsPrice == null) {
            goodsPrice = 0.0d;
        }
        return goodsPrice;
    }

    public void setGoodsPrice(Double goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Double getShippingPrice() {
        if (shippingPrice == null) {
            shippingPrice = 0.0d;
        }
        return shippingPrice;
    }

    public void setShippingPrice(Double shippingPrice) {
        this.shippingPrice = shippingPrice;
    }

    public Double getDiscountPrice() {
        if (discountPrice == null) {
            discountPrice = 0.0d;
        }
        return discountPrice;
    }

    public Double getBalance() {
        if (balance == null) {
            balance = 0.0d;
        }
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getOrderData() {
        return orderData;
    }

    public void setOrderData(String orderData) {
        this.orderData = orderData;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getTheSign() {
        return theSign;
    }

    public void setTheSign(String theSign) {
        this.theSign = theSign;
    }

    public String getItemsJson() {
        return itemsJson;
    }

    public void setItemsJson(String itemsJson) {
        this.itemsJson = itemsJson;
    }

    public Integer getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
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

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Integer getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(Integer adminRemark) {
        this.adminRemark = adminRemark;
    }

    public Long getLogiId() {
        return logiId;
    }

    public void setLogiId(Long logiId) {
        this.logiId = logiId;
    }

    public String getLogiName() {
        return logiName;
    }

    public void setLogiName(String logiName) {
        this.logiName = logiName;
    }

    public Long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Long completeTime) {
        this.completeTime = completeTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getSigningTime() {
        return signingTime;
    }

    public void setSigningTime(Long signingTime) {
        this.signingTime = signingTime;
    }

    public Long getShipTime() {
        return shipTime;
    }

    public void setShipTime(Long shipTime) {
        this.shipTime = shipTime;
    }

    public String getPayOrderNo() {
        return payOrderNo;
    }

    public void setPayOrderNo(String payOrderNo) {
        this.payOrderNo = payOrderNo;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Integer getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(Integer billStatus) {
        this.billStatus = billStatus;
    }

    public String getBillSn() {
        return billSn;
    }

    public void setBillSn(String billSn) {
        this.billSn = billSn;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getNeedReceipt() {
        if (needReceipt == null) {
            needReceipt = 0;
        }
        return needReceipt;
    }

    public void setNeedReceipt(Integer needReceipt) {
        this.needReceipt = needReceipt;
    }

    public Integer getShippingId() {
        return shippingId;
    }

    public void setShippingId(Integer shippingId) {
        this.shippingId = shippingId;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    @Override
    public String toString() {
        return "OrderDO{" +
                "orderId=" + orderId +
                ", tradeSn='" + tradeSn + '\'' +
                ", sn='" + sn + '\'' +
                ", sellerId=" + sellerId +
                ", sellerName='" + sellerName + '\'' +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", shipStatus='" + shipStatus + '\'' +
                ", shippingId=" + shippingId +
                ", commentStatus='" + commentStatus + '\'' +
                ", shippingType='" + shippingType + '\'' +
                ", paymentMethodId='" + paymentMethodId + '\'' +
                ", paymentPluginId='" + paymentPluginId + '\'' +
                ", paymentMethodName='" + paymentMethodName + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", paymentTime=" + paymentTime +
                ", payMoney=" + payMoney +
                ", shipName='" + shipName + '\'' +
                ", shipAddr='" + shipAddr + '\'' +
                ", shipZip='" + shipZip + '\'' +
                ", shipMobile='" + shipMobile + '\'' +
                ", shipTel='" + shipTel + '\'' +
                ", receiveTime='" + receiveTime + '\'' +
                ", shipProvinceId=" + shipProvinceId +
                ", shipCityId=" + shipCityId +
                ", shipCountyId=" + shipCountyId +
                ", shipTownId=" + shipTownId +
                ", shipProvince='" + shipProvince + '\'' +
                ", shipCity='" + shipCity + '\'' +
                ", shipCounty='" + shipCounty + '\'' +
                ", shipTown='" + shipTown + '\'' +
                ", orderPrice=" + orderPrice +
                ", goodsPrice=" + goodsPrice +
                ", shippingPrice=" + shippingPrice +
                ", discountPrice=" + discountPrice +
                ", disabled=" + disabled +
                ", weight=" + weight +
                ", goodsNum=" + goodsNum +
                ", remark='" + remark + '\'' +
                ", cancelReason='" + cancelReason + '\'' +
                ", theSign='" + theSign + '\'' +
                ", itemsJson='" + itemsJson + '\'' +
                ", warehouseId=" + warehouseId +
                ", needPayMoney=" + needPayMoney +
                ", shipNo='" + shipNo + '\'' +
                ", addressId=" + addressId +
                ", adminRemark=" + adminRemark +
                ", logiId=" + logiId +
                ", logiName='" + logiName + '\'' +
                ", completeTime=" + completeTime +
                ", createTime=" + createTime +
                ", signingTime=" + signingTime +
                ", shipTime=" + shipTime +
                ", payOrderNo='" + payOrderNo + '\'' +
                ", serviceStatus='" + serviceStatus + '\'' +
                ", billStatus=" + billStatus +
                ", billSn='" + billSn + '\'' +
                ", clientType='" + clientType + '\'' +
                ", needReceipt=" + needReceipt +
                ", orderType='" + orderType + '\'' +
                ", orderData='" + orderData + '\'' +
                ", balance=" + balance +
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
        OrderDO that = (OrderDO) o;

        return new EqualsBuilder()
                .append(orderId, that.orderId)
                .append(tradeSn, that.tradeSn)
                .append(sn, that.sn)
                .append(sellerId, that.sellerId)
                .append(sellerName, that.sellerName)
                .append(memberId, that.memberId)
                .append(memberName, that.memberName)
                .append(orderStatus, that.orderStatus)
                .append(payStatus, that.payStatus)
                .append(shipStatus, that.shipStatus)
                .append(shippingId, that.shippingId)
                .append(commentStatus, that.commentStatus)
                .append(shippingType, that.shippingType)
                .append(paymentMethodId, that.paymentMethodId)
                .append(paymentPluginId, that.paymentPluginId)
                .append(paymentMethodName, that.paymentMethodName)
                .append(paymentType, that.paymentType)
                .append(paymentTime, that.paymentTime)
                .append(payMoney, that.payMoney)
                .append(shipName, that.shipName)
                .append(shipAddr, that.shipAddr)
                .append(shipZip, that.shipZip)
                .append(shipMobile, that.shipMobile)
                .append(shipTel, that.shipTel)
                .append(receiveTime, that.receiveTime)
                .append(shipProvinceId, that.shipProvinceId)
                .append(shipCityId, that.shipCityId)
                .append(shipCountyId, that.shipCountyId)
                .append(shipTownId, that.shipTownId)
                .append(shipProvince, that.shipProvince)
                .append(shipCity, that.shipCity)
                .append(shipCounty, that.shipCounty)
                .append(shipTown, that.shipTown)
                .append(orderPrice, that.orderPrice)
                .append(goodsPrice, that.goodsPrice)
                .append(shippingPrice, that.shippingPrice)
                .append(discountPrice, that.discountPrice)
                .append(disabled, that.disabled)
                .append(weight, that.weight)
                .append(goodsNum, that.goodsNum)
                .append(remark, that.remark)
                .append(cancelReason, that.cancelReason)
                .append(theSign, that.theSign)
                .append(itemsJson, that.itemsJson)
                .append(warehouseId, that.warehouseId)
                .append(needPayMoney, that.needPayMoney)
                .append(shipNo, that.shipNo)
                .append(addressId, that.addressId)
                .append(adminRemark, that.adminRemark)
                .append(logiId, that.logiId)
                .append(logiName, that.logiName)
                .append(completeTime, that.completeTime)
                .append(createTime, that.createTime)
                .append(signingTime, that.signingTime)
                .append(shipTime, that.shipTime)
                .append(payOrderNo, that.payOrderNo)
                .append(serviceStatus, that.serviceStatus)
                .append(billStatus, that.billStatus)
                .append(billSn, that.billSn)
                .append(clientType, that.clientType)
                .append(needReceipt, that.needReceipt)
                .append(orderType, that.orderType)
                .append(orderData, that.orderData)
                .append(balance, that.balance)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(orderId)
                .append(tradeSn)
                .append(sn)
                .append(sellerId)
                .append(sellerName)
                .append(memberId)
                .append(memberName)
                .append(orderStatus)
                .append(payStatus)
                .append(shipStatus)
                .append(shippingId)
                .append(commentStatus)
                .append(shippingType)
                .append(paymentMethodId)
                .append(paymentPluginId)
                .append(paymentMethodName)
                .append(paymentType)
                .append(paymentTime)
                .append(payMoney)
                .append(shipName)
                .append(shipAddr)
                .append(shipZip)
                .append(shipMobile)
                .append(shipTel)
                .append(receiveTime)
                .append(shipProvinceId)
                .append(shipCityId)
                .append(shipCountyId)
                .append(shipTownId)
                .append(shipProvince)
                .append(shipCity)
                .append(shipCounty)
                .append(shipTown)
                .append(orderPrice)
                .append(goodsPrice)
                .append(shippingPrice)
                .append(discountPrice)
                .append(disabled)
                .append(weight)
                .append(goodsNum)
                .append(remark)
                .append(cancelReason)
                .append(theSign)
                .append(itemsJson)
                .append(warehouseId)
                .append(needPayMoney)
                .append(shipNo)
                .append(addressId)
                .append(adminRemark)
                .append(logiId)
                .append(logiName)
                .append(completeTime)
                .append(createTime)
                .append(signingTime)
                .append(shipTime)
                .append(payOrderNo)
                .append(serviceStatus)
                .append(billStatus)
                .append(billSn)
                .append(clientType)
                .append(needReceipt)
                .append(orderType)
                .append(orderData)
                .append(balance)
                .toHashCode();
    }

    public OrderDO() {

    }

    /**
     * 带初始化参数的构造器
     *
     * @param orderDTO
     */
    public OrderDO(OrderDTO orderDTO) {

        this.receiveTime = orderDTO.getReceiveTime();
        this.sn = orderDTO.getSn();
        this.shipTime = orderDTO.getShipTime();
        this.paymentType = orderDTO.getPaymentType();

        // 收货人
        ConsigneeVO consignee = orderDTO.getConsignee();
        if(consignee!=null){
            shipName = consignee.getName();
            shipAddr = consignee.getAddress();

            this.addressId = consignee.getConsigneeId();
            this.shipMobile = consignee.getMobile();
            this.shipTel = consignee.getTelephone();

            this.shipProvince = consignee.getProvince();
            this.shipCity = consignee.getCity();
            this.shipCounty = consignee.getCounty();
            this.shipTown = consignee.getTown();

            this.shipProvinceId = consignee.getProvinceId();
            this.shipCityId = consignee.getCityId();
            this.shipCountyId = consignee.getCountyId();
            this.shipTownId = consignee.getTownId();
        }


        // 价格
        PriceDetailVO priceDetail = orderDTO.getPrice();
        this.orderPrice = orderDTO.getOrderPrice();
        this.goodsPrice = priceDetail.getGoodsPrice();
        this.shippingPrice = priceDetail.getFreightPrice();
        this.discountPrice = priceDetail.getDiscountPrice();
        this.needPayMoney = orderDTO.getNeedPayMoney();
        this.weight = orderDTO.getWeight();
        this.shippingId = orderDTO.getShippingId();
        this.balance = 0D;

        // 卖家
        this.sellerId = orderDTO.getSellerId();
        this.sellerName = orderDTO.getSellerName();

        // 买家
        this.memberId = orderDTO.getMemberId();
        this.memberName = orderDTO.getMemberName();

        // 创建时间
        this.createTime = orderDTO.getCreateTime();

        // 初始化状态
        this.shipStatus = ShipStatusEnum.SHIP_NO.value();
        this.orderStatus = OrderStatusEnum.NEW.value();
        this.payStatus = PayStatusEnum.PAY_NO.value();
        this.commentStatus = CommentStatusEnum.UNFINISHED.value();
        this.serviceStatus = OrderServiceStatusEnum.NOT_APPLY.value();
        this.disabled = 0;

        List<OrderSkuVO> orderSkuVOList = orderDTO.getOrderSkuList();
        for (OrderSkuVO orderSkuVO : orderSkuVOList) {
            orderSkuVO.setActualPayTotal(orderSkuVO.getSubtotal());
            orderSkuVO.setComplainStatus(ComplainSkuStatusEnum.NO_APPLY.name());
        }

        // 产品列表
        this.itemsJson = JsonUtil.objectToJson(orderSkuVOList);

        // 备注
        this.remark = orderDTO.getRemark();

        //发票
        this.needReceipt = orderDTO.getNeedReceipt();

        //订单来源
        this.clientType = orderDTO.getClientType();
        this.orderType = orderDTO.getOrderType();

    }

    /**
     * 带初始化参数的构造器
     * @param orderParam
     */
    public OrderDO(OrderParam orderParam){

    }

}
