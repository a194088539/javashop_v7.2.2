package com.enation.app.javashop.service.promotion.exchange;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.promotion.exchange.dto.ExchangeQueryParam;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionGoodsDTO;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeDO;

/**
 * 积分商品业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-21 11:47:18
 */
public interface ExchangeGoodsManager {


	/**
	 * 查询积分商品列表
	 * @param param
	 * @return
	 */
	WebPage list(ExchangeQueryParam param);

	/**
	 * 添加积分兑换
	 * @param exchangeSetting 积分兑换
	 * @param goodsDTO 商品DTO
	 * @return ExchangeSetting 积分兑换 */
	ExchangeDO add(ExchangeDO exchangeSetting, PromotionGoodsDTO goodsDTO);

	/**
	* 修改积分兑换
	* @param exchangeSetting 积分兑换
	* @param goodsDTO 商品DTO
	* @return ExchangeSetting 积分兑换
	*/
	ExchangeDO edit(ExchangeDO exchangeSetting, PromotionGoodsDTO goodsDTO);

	/**
	 * 删除积分兑换
	 * @param id 积分兑换主键
	 */
	void delete(Long id);

	/**
	 * 获取积分兑换
	 * @param id 积分兑换主键
	 * @return ExchangeSetting  积分兑换
	 */
	ExchangeDO getModel(Long id);

	/**
	 * 查询某个商品的积分兑换信息
	 * @param goodsId
	 * @return
	 */
    ExchangeDO getModelByGoods(Long goodsId);

	/**
	 * 查询某个积分分类的积分兑换信息
	 * @param categoryId
	 * @return
	 */
	ExchangeDO getModelByCategoryId(Long categoryId);

	/**
	 * 删除某个商品的积分信息
	 * @param goodsId
	 */
	void deleteByGoods(Long goodsId);
}
