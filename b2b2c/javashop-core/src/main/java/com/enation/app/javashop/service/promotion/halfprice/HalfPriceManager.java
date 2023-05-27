package com.enation.app.javashop.service.promotion.halfprice;

import com.enation.app.javashop.model.promotion.halfprice.dos.HalfPriceDO;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.framework.database.WebPage;

/**
 * 第二件半价业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-23 19:53:42
 */
public interface HalfPriceManager	{

	/**
	 * 查询第二件半价列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @param keywords 关键字
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize, String keywords);
	/**
	 * 添加第二件半价
	 * @param halfPrice 第二件半价
	 * @return HalfPrice 第二件半价
	 */
	HalfPriceDO add(HalfPriceVO halfPrice);

	/**
	* 修改第二件半价
	* @param halfPrice 第二件半价
	* @param id 第二件半价主键
	* @return HalfPrice 第二件半价
	*/
	HalfPriceDO edit(HalfPriceVO halfPrice, Long id);

	/**
	 * 删除第二件半价
	 * @param id 第二件半价主键
	 */
	void delete(Long id);

	/**
	 * 获取第二件半价
	 * @param id 第二件半价主键
	 * @return HalfPrice  第二件半价
	 */
	HalfPriceVO getFromDB(Long id);

	/**
	 * 验证操作权限<br/>
	 * 如有问题直接抛出权限异常
	 * @param id
	 */
	void verifyAuth(Long id);

}
