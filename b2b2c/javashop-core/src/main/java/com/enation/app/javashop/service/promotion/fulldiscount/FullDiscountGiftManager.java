package com.enation.app.javashop.service.promotion.fulldiscount;

import com.enation.app.javashop.model.goods.enums.QuantityType;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;

/**
 * 满优惠赠品业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-30 17:34:46
 */
public interface FullDiscountGiftManager	{

	/**
	 * 查询满优惠赠品列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @param keyWord 	关键字
	 * @return
	 */
	WebPage list(long page, long pageSize, String keyWord);

	/**
	 * 添加满优惠赠品
	 * @param fullDiscountGift 满优惠赠品
	 * @return FullDiscountGift 满优惠赠品
	 */
	FullDiscountGiftDO add(FullDiscountGiftDO fullDiscountGift);

	/**
	* 修改满优惠赠品
	* @param fullDiscountGift 满优惠赠品
	* @param id 满优惠赠品主键
	* @return FullDiscountGift 满优惠赠品
	*/
	FullDiscountGiftDO edit(FullDiscountGiftDO fullDiscountGift, Long id);

	/**
	 * 删除满优惠赠品
	 * @param id 满优惠赠品主键
	 */
	void delete(Long id);

	/**
	 * 获取满优惠赠品
	 * @param id 满优惠赠品主键
	 * @return FullDiscountGift  满优惠赠品
	 */
	FullDiscountGiftDO getModel(Long id);


	/**
	 * 验证操作权限<br/>
	 * 如有问题直接抛出权限异常
	 * @param id
	 */
	void verifyAuth(Long id);

	/**
	 * 增加赠品库存
	 * @param giftDOList
	 * @return
	 */
	boolean addGiftQuantity(List<FullDiscountGiftDO> giftDOList);

	/**
	 * 增加赠品可用库存
	 * @param giftDOList
	 * @return
	 */
	boolean addGiftEnableQuantity(List<FullDiscountGiftDO> giftDOList);


	/**
	 * 减少赠品库存
	 * @param giftDOList
	 * @param type
	 * @return
	 */
	boolean reduceGiftQuantity(List<FullDiscountGiftDO> giftDOList, QuantityType type);

	/**
	 * 获取商家所有赠品数据集合
	 * @return
	 */
	List<FullDiscountGiftDO> listAll();


	/**
	 * 验证赠品是否在活动时间内
	 * @param giftId
	 */
	void verifyGift(Long giftId);
}
