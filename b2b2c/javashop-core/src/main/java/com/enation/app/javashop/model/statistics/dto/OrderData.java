package com.enation.app.javashop.model.statistics.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * 统计订单数据
 * @author chopper
 * @version v1.0
 * @since v7.0
 * 2018/3/22 下午11:50
 * @Description:
 */

@TableName("es_sss_order_data")
public class OrderData implements Serializable {
	@ApiModelProperty(value = "主键id")
	@TableId(type= IdType.ASSIGN_ID)
	private Long id;

	@ApiModelProperty(value = "店铺id")
	private Long sellerId;

	@ApiModelProperty(value = "店铺")
	private String sellerName;

	@ApiModelProperty(value = "会员id")
	private Long buyerId;

	@ApiModelProperty(value = "购买人")
	private String buyerName;

	@ApiModelProperty(value = "订单编号")
	private String sn;


	@ApiModelProperty(value = "订单状态")
	private String orderStatus;

	@ApiModelProperty(value = "付款状态")
	private String payStatus;

	@ApiModelProperty(value = "订单金额")
	private Double orderPrice;

	@ApiModelProperty(value = "商品数量")
	private Integer goodsNum;

	@ApiModelProperty(value = "省id")
	private Long shipProvinceId;

	@ApiModelProperty(value = "区id")
	private Long shipCityId;

	@ApiModelProperty(value = "创建时间")
	private Long createTime;

	public OrderData() {

	}

	public OrderData(Map<String,Object> order) {
		this.setSn((String)order.get("sn"));
		this.setSellerId((Long)order.get("seller_id"));
		this.setSellerName((String)order.get("seller_name"));
		this.setBuyerName((String)order.get("buyer_name"));
		this.setBuyerId((Long)order.get("buyer_id"));
		this.setOrderStatus((String)order.get("order_status"));
		this.setPayStatus((String)order.get("pay_status"));
		this.setOrderPrice((Double)order.get("order_price"));
		this.setGoodsNum((Integer) order.get("goods_num"));
		this.setCreateTime((Long)order.get("create_time"));
		this.setShipProvinceId((Long)order.get("ship_province_id"));
		this.setShipCityId((Long)order.get("ship_city_id"));
	}
	public OrderData(OrderDO order) {
		this.setSn(order.getSn());
		this.setSellerName(order.getSellerName());
		this.setSellerId(order.getSellerId());
		this.setPayStatus(order.getPayStatus());
		this.setOrderStatus(order.getOrderStatus());
		this.setBuyerName(order.getMemberName());
		this.setBuyerId(order.getMemberId());
		this.setCreateTime(order.getCreateTime());
		this.setGoodsNum(order.getGoodsNum());
		this.setOrderPrice(order.getOrderPrice());
		this.setShipCityId(order.getShipCityId());
		this.setShipProvinceId(order.getShipProvinceId());
	}


	@Override
	public String toString() {
		return "OrderData{" +
				" sellerId=" + sellerId +
				", sellerName=" + sellerName +
				", buyerId=" + buyerId +
				", buyerName=" + buyerName +
				", sn='" + sn + '\'' +
				", orderStatus='" + orderStatus + '\'' +
				", payStatus='" + payStatus + '\'' +
				", orderPrice=" + orderPrice +
				", goodsNum=" + goodsNum +
				", shipProvinceid=" + shipProvinceId +
				", shipCityid=" + shipCityId +
				", createTime=" + createTime +
				'}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	public Long getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Long buyerId) {
		this.buyerId = buyerId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
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

	public Double getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(Double orderPrice) {
		this.orderPrice = orderPrice;
	}

	public Integer getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(Integer goodsNum) {
		this.goodsNum = goodsNum;
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

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
}
