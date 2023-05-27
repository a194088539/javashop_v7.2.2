package com.enation.app.javashop.service.promotion.exchange;

import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeCat;

import java.util.List;

/**
 * 积分兑换分类业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-05-29 16:56:22
 */
public interface ExchangeCatManager	{

	/**
	 * 查询积分兑换分类列表
	 * @param parentId 父ID
	 * @return WebPage
	 */
	List<ExchangeCat> list(Long parentId);

	/**
	 * 添加积分兑换分类
	 * @param exchangeCat 积分兑换分类
	 * @return ExchangeCat 积分兑换分类
	 */
	ExchangeCat add(ExchangeCat exchangeCat);

	/**
	* 修改积分兑换分类
	* @param exchangeCat 积分兑换分类
	* @param id 积分兑换分类主键
	* @return ExchangeCat 积分兑换分类
	*/
	ExchangeCat edit(ExchangeCat exchangeCat, Long id);

	/**
	 * 删除积分兑换分类
	 * @param id 积分兑换分类主键
	 */
	void delete(Long id);

	/**
	 * 获取积分兑换分类
	 * @param id 积分兑换分类主键
	 * @return ExchangeCat  积分兑换分类
	 */
	ExchangeCat getModel(Long id);

}
