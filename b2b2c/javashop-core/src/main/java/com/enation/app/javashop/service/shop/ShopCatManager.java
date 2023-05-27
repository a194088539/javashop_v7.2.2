package com.enation.app.javashop.service.shop;

import com.enation.app.javashop.model.shop.dos.ShopCatDO;

import java.util.List;

/**
 * 店铺分组业务层
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-24 11:18:37
 */
public interface ShopCatManager	{

	/**
	 * 查询店铺分组列表
	 * @param shopId 店铺ID
	 * @return
	 */
	List list(Long shopId,String display);
	/**
	 * 添加店铺分组
	 * @param shopCatDO 店铺分组
	 * @return ShopCatDO 店铺分组
	 */
	ShopCatDO add(ShopCatDO shopCatDO);

	/**
	* 修改店铺分组
	* @param shopCatDO 店铺分组
	* @param id 店铺分组主键
	* @return ShopCatDO 店铺分组
	*/
	ShopCatDO edit(ShopCatDO shopCatDO, Long id);

	/**
	 * 删除店铺分组
	 * @param id 店铺分组主键
	 */
	void delete(Long id);

	/**
	 * 获取店铺分组
	 * @param id 店铺分组主键
	 * @return ShopCatDO  店铺分组
	 */
	ShopCatDO getModel(Long id);

	/**
	 * 获取当前分组所有的子(包括当前分组)
	 * @param catPath 店铺分组标识
	 * @return 子类集合
	 */
	List getChildren(String catPath);

}
