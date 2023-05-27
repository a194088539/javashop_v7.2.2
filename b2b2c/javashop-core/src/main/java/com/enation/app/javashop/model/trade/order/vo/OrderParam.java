package com.enation.app.javashop.model.trade.order.vo;

import com.enation.app.javashop.model.member.dos.ReceiptHistory;
import com.enation.app.javashop.model.promotion.coupon.vo.GoodsCouponPrice;
import com.enation.app.javashop.model.trade.cart.vo.PriceDetailVO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
*
* @description: 创建订单参数VO
* @author: liuyulei
* @create: 2020/3/18 11:13
* @version:1.0
* @since: 7.2.0
**/
@ApiModel
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OrderParam  implements Serializable {


	private static final long serialVersionUID = -1031196845416789581L;
	/**
	 * 交易编号
	 */
	@ApiModelProperty(name="trade_sn",value = "交易编号")
	private String tradeSn;

	/**
	 * 商品列表
	 */
	@ApiModelProperty(name="sku_param",value = "商品列表")
	private List<OrderSkuVO> skuParam;

	@ApiModelProperty(value = "配送方式")
	private Integer shippingId;

	@ApiModelProperty(value = "支付方式")
	private String paymentType;

	/**
	 * 商家id
	 */
	@ApiModelProperty(name="seller_id",value = "商家id")
	private Long sellerId;

	@ApiModelProperty(name="seller_name",value = "商家名称")
	private String sellerName;

	@ApiModelProperty(value = "配送方式名称")
	private String shippingType;

	@ApiModelProperty(value = "商品数量")
	private Integer goodsNum;


	@ApiModelProperty(value = "是否需要发票")
	private Integer needReceipt;

	@ApiModelProperty(value = "订单来源")
	private String clientType;

	/**
	 * 订单类型
	 */
	@ApiModelProperty(name="order_type",value = "订单类型")
	private String orderType;

	@ApiModelProperty(value = "重量" )
	private Double weight;

	@ApiModelProperty(value = "赠品列表" )
	private String giftJson;

	@ApiModelProperty(value = "赠送积分")
	private Integer giftPoint;

	@ApiModelProperty(value = "优惠券抵扣金额")
	private Double couponTotalPrice;

	@ApiModelProperty(value = "使用优惠券的商品")
	private List<GoodsCouponPrice> couponGoodsList;

	@ApiModelProperty(value = "是否为站点优惠券")
	private  Boolean isSiteCoupon;

	@ApiModelProperty(value = "发票信息")
	private ReceiptHistory receiptHistory;

	@ApiModelProperty(value = "收货信息")
	private ConsigneeVO consignee;

	/**
	 * 会员ID
	 */
	@ApiModelProperty(name = "member_id", value = "会员ID", required = false)
	private Long memberId;
	/**
	 * 买家账号
	 */
	@ApiModelProperty(name = "member_name", value = "买家账号", required = false)
	private String memberName;

	/**
	 * 收货时间
	 */
	@ApiModelProperty(name = "receive_time", value = "收货时间", required = false)
	private String receiveTime;

	/**
	 * 订单备注
	 */
	@ApiModelProperty(name = "remark", value = "订单备注", required = false)
	private String remark;

	@ApiModelProperty(name = "price", value = "订单价格信息", required = false)
	private PriceDetailVO price;

	public String getTradeSn() {
		return tradeSn;
	}

	public void setTradeSn(String tradeSn) {
		this.tradeSn = tradeSn;
	}

	public List<OrderSkuVO> getSkuParam() {
		return skuParam;
	}

	public void setSkuParam(List<OrderSkuVO> skuParam) {
		this.skuParam = skuParam;
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

	public String getShippingType() {
		return shippingType;
	}

	public void setShippingType(String shippingType) {
		this.shippingType = shippingType;
	}



	public Integer getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(Integer goodsNum) {
		this.goodsNum = goodsNum;
	}


	public Integer getNeedReceipt() {
		return needReceipt;
	}

	public void setNeedReceipt(Integer needReceipt) {
		this.needReceipt = needReceipt;
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

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public String getGiftJson() {
		return giftJson;
	}

	public void setGiftJson(String giftJson) {
		this.giftJson = giftJson;
	}

	public Integer getGiftPoint() {
		return giftPoint;
	}

	public void setGiftPoint(Integer giftPoint) {
		this.giftPoint = giftPoint;
	}

	public Double getCouponTotalPrice() {
		return couponTotalPrice;
	}

	public void setCouponTotalPrice(Double couponTotalPrice) {
		this.couponTotalPrice = couponTotalPrice;
	}

	public List<GoodsCouponPrice> getCouponGoodsList() {
		return couponGoodsList;
	}

	public void setCouponGoodsList(List<GoodsCouponPrice> couponGoodsList) {
		this.couponGoodsList = couponGoodsList;
	}

	public Boolean getIsSiteCoupon() {
		return isSiteCoupon;
	}

	public void setIsSiteCoupon(Boolean siteCoupon) {
		isSiteCoupon = siteCoupon;
	}

	public ReceiptHistory getReceiptHistory() {
		return receiptHistory;
	}

	public void setReceiptHistory(ReceiptHistory receiptHistory) {
		this.receiptHistory = receiptHistory;
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

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public PriceDetailVO getPrice() {
		return price;
	}

	public void setPrice(PriceDetailVO price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "OrderParam{" +
				"tradeSn='" + tradeSn + '\'' +
				", skuParam=" + skuParam +
				", shippingId=" + shippingId +
				", paymentType='" + paymentType + '\'' +
				", sellerId=" + sellerId +
				", sellerName='" + sellerName + '\'' +
				", shippingType='" + shippingType + '\'' +
				", goodsNum=" + goodsNum +
				", needReceipt=" + needReceipt +
				", clientType='" + clientType + '\'' +
				", orderType='" + orderType + '\'' +
				", weight=" + weight +
				", giftJson='" + giftJson + '\'' +
				", giftPoint=" + giftPoint +
				", couponTotalPrice=" + couponTotalPrice +
				", couponGoodsList=" + couponGoodsList +
				", isSiteCoupon=" + isSiteCoupon +
				", receiptHistory=" + receiptHistory +
				", consignee=" + consignee +
				", memberId=" + memberId +
				", memberName='" + memberName + '\'' +
				", receiveTime='" + receiveTime + '\'' +
				", remark='" + remark + '\'' +
				'}';
	}


}
