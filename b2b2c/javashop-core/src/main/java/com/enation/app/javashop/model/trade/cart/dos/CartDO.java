package com.enation.app.javashop.model.trade.cart.dos;

import com.enation.app.javashop.model.trade.cart.vo.CartSkuVO;
import com.enation.app.javashop.model.trade.cart.vo.PriceDetailVO;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车模型
 * @author Snow
 * @version 1.0
 * @since v7.0.0
 * 2018年03月20日14:21:39
 */

@ApiModel( description = "购物车")
@JsonNaming(value = PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CartDO implements Serializable {


	private static final long serialVersionUID = 1466001652922300536L;

	@ApiModelProperty(value = "卖家id" )
	private Long sellerId;

	@ApiModelProperty(value = "选中的配送方式id" )
	private Integer shippingTypeId;


	@ApiModelProperty(value = "选中的配送方式名称" )
	private String shippingTypeName;


	@ApiModelProperty(value = "卖家店名" )
	private String sellerName;


	@ApiModelProperty(value = "购物车重量" )
	private Double weight;

	@ApiModelProperty(value = "购物车价格")
	private PriceDetailVO price;

	@ApiModelProperty(value = "购物车中的产品列表" )
	private List<CartSkuVO> skuList;

	@ApiModelProperty(value = "赠品列表" )
	private String giftJson;

	@ApiModelProperty(value = "赠送积分")
	private Integer giftPoint;

	@ApiModelProperty(value = "是否失效：0:正常 1:已失效")
	private Integer invalid;

	public CartDO(){}

	/**
	 * 在构造器中初始化属主、产品列表、促销列表及优惠券列表
	 */
	public CartDO(Long sellerId, String sellerName){

		this.sellerId = sellerId;
		this.sellerName = sellerName;
		price = new PriceDetailVO();
		skuList = new ArrayList<CartSkuVO>();
		giftJson = "";
		giftPoint=0;
	}


	/**
	 * 清空优惠信息功能，不清空优惠券
	 */
	public void clearPromotion(){
		if(price!=null){
			price.clear();
		}
		giftJson = "";
		giftPoint=0;
	}

	public String getGiftJson() {
		return giftJson;
	}

	public void setGiftJson(String giftJson) {
		this.giftJson = giftJson;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public Integer getShippingTypeId() {
		return shippingTypeId;
	}

	public void setShippingTypeId(Integer shippingTypeId) {
		this.shippingTypeId = shippingTypeId;
	}

	public String getShippingTypeName() {
		return shippingTypeName;
	}

	public void setShippingTypeName(String shippingTypeName) {
		this.shippingTypeName = shippingTypeName;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public PriceDetailVO getPrice() {
		return price;
	}

	public void setPrice(PriceDetailVO price) {
		this.price = price;
	}

	public List<CartSkuVO> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<CartSkuVO> skuList) {
		this.skuList = skuList;
	}

	public Integer getGiftPoint() {
		return giftPoint;
	}

	public void setGiftPoint(Integer giftPoint) {
		this.giftPoint = giftPoint;
	}


	public Integer getInvalid() {
		return invalid;
	}


	public void setInvalid(Integer invalid) {
		this.invalid = invalid;
	}

	@Override
	public String toString() {
		return "CartDO{" +
				"sellerId=" + sellerId +
				", shippingTypeId=" + shippingTypeId +
				", shippingTypeName='" + shippingTypeName + '\'' +
				", sellerName='" + sellerName + '\'' +
				", weight=" + weight +
				", price=" + price +
				", skuList=" + skuList +
				", giftJson='" + giftJson + '\'' +
				", giftPoint=" + giftPoint +
				", invalid=" + invalid +
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
		CartDO that = (CartDO) o;

		return new EqualsBuilder()
				.append(sellerId, that.sellerId)
				.append(shippingTypeId, that.shippingTypeId)
				.append(shippingTypeName, that.shippingTypeName)
				.append(sellerName, that.sellerName)
				.append(weight, that.weight)
				.append(price, that.price)
				.append(skuList, that.skuList)
				.append(giftJson, that.giftJson)
				.append(giftPoint, that.giftPoint)
				.append(invalid, that.invalid)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(sellerId)
				.append(shippingTypeId)
				.append(shippingTypeName)
				.append(sellerName)
				.append(weight)
				.append(price)
				.append(skuList)
				.append(giftJson)
				.append(giftPoint)
				.append(invalid)
				.toHashCode();
	}
}
