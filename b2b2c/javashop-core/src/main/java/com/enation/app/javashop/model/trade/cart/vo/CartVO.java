package com.enation.app.javashop.model.trade.cart.vo;

import com.enation.app.javashop.model.shop.dto.ShipTemplateChildDTO;
import com.enation.app.javashop.model.trade.cart.dos.CartDO;
import com.enation.app.javashop.model.trade.cart.enums.CartType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 购物车展示Vo
 * @author Snow
 * @since v6.4
 * @version v1.0
 * 2017年08月23日14:22:48
 */

@ApiModel(description = "购物车展示Vo")
public class CartVO extends CartDO implements Serializable{


	private static final long serialVersionUID = 6382186311779188645L;
	/**
	 * 把Cart.SkuList 数据 根据促销活动压入到此集合中。
	 */
	@ApiModelProperty(value = "促销活动集合（包含商品")
	private List<CartPromotionVo>  promotionList;

	@ApiModelProperty(value = "已参与的的促销活动提示，直接展示给客户")
	private String promotionNotice;

	/**
	 * 购物车与运费dto的map映射
	 * key为skuid value 为模版
	 */
	private Map<Long, ShipTemplateChildDTO> shipTemplateChildMap;

	@ApiModelProperty(value = "购物车页展示时，店铺内的商品是否全选状态.1为店铺商品全选状态,0位非全选")
	private Integer checked;

	/**
	 * 购物车类型：购物车页面和或结算页
	 */
	private CartType cartType;


	public CartVO(){

	}

	/**
	 * 父类的构造器
	 * @param sellerId
	 * @param sellerName
	 */
	public CartVO(Long sellerId, String sellerName, CartType cartType) {
		this.cartType = cartType;
		super.setWeight(0D);
		super.setSellerId(sellerId);
		super.setSellerName(sellerName);
		super.setPrice(new PriceDetailVO());
		super.setSkuList(new ArrayList<CartSkuVO>());
		super.setGiftJson("");
		this.setChecked(1);
		this.setInvalid(0);
	}


	public List<CartPromotionVo> getPromotionList() {
		return promotionList;
	}

	public void setPromotionList(List<CartPromotionVo> promotionList) {
		this.promotionList = promotionList;
	}

	public Integer getChecked() {
		return checked;
	}

	public void setChecked(Integer checked) {
		this.checked = checked;
	}

	public Map<Long, ShipTemplateChildDTO> getShipTemplateChildMap() {
		return shipTemplateChildMap;
	}

	public String getPromotionNotice() {
		return promotionNotice;
	}

	public void setPromotionNotice(String promotionNotice) {
		this.promotionNotice = promotionNotice;
	}

	public void setShipTemplateChildMap(Map<Long, ShipTemplateChildDTO> shipTemplateChildMap) {
		this.shipTemplateChildMap = shipTemplateChildMap;
	}

	public CartType getCartType() {
		return cartType;
	}

	public void setCartType(CartType cartType) {
		this.cartType = cartType;
	}

	@Override
	public String toString() {
		return "CartVO{" +
				"promotionList=" + promotionList +
				", promotionNotice='" + promotionNotice + '\'' +
				", shipTemplateChildMap=" + shipTemplateChildMap +
				", checked=" + checked +
				", cartType=" + cartType +
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
		CartVO that = (CartVO) o;

		return new EqualsBuilder()
				.appendSuper(super.equals(o))
				.append(promotionList, that.promotionList)
				.append(promotionNotice, that.promotionNotice)
				.append(shipTemplateChildMap, that.shipTemplateChildMap)
				.append(checked, that.checked)
				.append(cartType, that.cartType)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.appendSuper(super.hashCode())
				.append(promotionList)
				.append(promotionNotice)
				.append(shipTemplateChildMap)
				.append(checked)
				.append(cartType)
				.toHashCode();
	}
}
