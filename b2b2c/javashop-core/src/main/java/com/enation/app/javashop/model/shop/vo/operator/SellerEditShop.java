package com.enation.app.javashop.model.shop.vo.operator;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 * 卖家操作店铺
 * @author zhangjiping
 * @version v1.0
 * @since v7.0
 * 2018年3月27日 上午10:29:48
 */
public class SellerEditShop {

	@ApiModelProperty(value = "卖家id" )
	private Long sellerId;

	@ApiModelProperty(value = "操作")
	private String operator;


	public SellerEditShop() {

	}

	public SellerEditShop(Long sellerId, String operator) {
		super();
		this.sellerId = sellerId;
		this.operator = operator;
	}

	public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}


}
