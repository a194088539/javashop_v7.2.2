package com.enation.app.javashop.service.pagedata;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.pagedata.HotKeyword;

import java.util.List;

/**
 * 热门关键字业务层
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-04 10:43:23
 */
public interface HotKeywordManager	{

	/**
	 * 查询热门关键字列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);
	/**
	 * 添加热门关键字
	 * @param hotKeyword 热门关键字
	 * @return HotKeyword 热门关键字
	 */
	HotKeyword add(HotKeyword hotKeyword);

	/**
	* 修改热门关键字
	* @param hotKeyword 热门关键字
	* @param id 热门关键字主键
	* @return HotKeyword 热门关键字
	*/
	HotKeyword edit(HotKeyword hotKeyword, Long id);

	/**
	 * 删除热门关键字
	 * @param id 热门关键字主键
	 */
	void delete(Long id);

	/**
	 * 获取热门关键字
	 * @param id 热门关键字主键
	 * @return HotKeyword  热门关键字
	 */
	HotKeyword getModel(Long id);

	/**
	 * 查询热门关键字
	 * @param num
	 * @return
	 */
	List<HotKeyword> listByNum(Integer num);
}
