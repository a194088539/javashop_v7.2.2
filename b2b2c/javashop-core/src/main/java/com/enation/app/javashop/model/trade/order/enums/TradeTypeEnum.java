package com.enation.app.javashop.model.trade.order.enums;

/**
 * 交易类型
 * @author Snow
 * @version 1.0
 * @since v7.0.0
 * 2017年4月5日下午5:12:55
 */
public enum TradeTypeEnum {

	/** 订单 */
	ORDER("订单"),

	/** 交易 */
	TRADE("交易"),

	/**
	 * 充值
	 */
	RECHARGE("充值"),

	/**
	 * 调试器类型（程序调试用，不会人为用到）
	 */
	debugger("调试器")
	;

	private String description;

	TradeTypeEnum(String description){
		  this.description=description;
	}
}
