package com.enation.app.javashop.service.promotion.fulldiscount;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.promotion.fulldiscount.vo.FullDiscountVO;

/**
 * 满优惠活动业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-21 10:42:06
 */
public interface FullDiscountManager	{

	/**
	 * 查询满优惠活动列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @param keywords 关键字
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize, String keywords);

	/**
	 * 添加满优惠活动
	 * @param fullDiscount 满优惠活动
	 * @return FullDiscount 满优惠活动
	 */
	FullDiscountVO add(FullDiscountVO fullDiscount);

	/**
	* 修改满优惠活动
	* @param fullDiscount 满优惠活动
	* @param id 满优惠活动主键
	* @return FullDiscount 满优惠活动
	*/
	FullDiscountVO edit(FullDiscountVO fullDiscount, Long id);

	/**
	 * 删除满优惠活动
	 * @param id 满优惠活动主键
	 */
	void delete(Long id);

	/**
	 * 从数据库获取促销信息
	 * @param fdId 满优惠活动ID
	 * @return 满优惠活动实体
	 */
	FullDiscountVO getModel(Long fdId);


	/**
	 * 验证操作权限<br/>
	 * 如有问题直接抛出权限异常
	 * @param id
	 */
	void verifyAuth(Long id);



}
