package com.enation.app.javashop.service.goodssearch;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goodssearch.CustomWords;

/**
 * 自定义分词表业务层
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-06-20 16:08:07
 *
 * * update by liuyulei 2019-05-27
 */
public interface CustomWordsManager	{

	/**
	 * 查询自定义分词表列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize, String keywords);
	/**
	 * 添加自定义分词表
	 * @param customWords 自定义分词表
	 * @return CustomWords 自定义分词表
	 */
	CustomWords add(CustomWords customWords);

	/**
	* 修改自定义分词表
	* @param customWords 自定义分词表
	* @param id 自定义分词表主键
	* @return CustomWords 自定义分词表
	*/
	CustomWords edit(CustomWords customWords, Long id);

	/**
	 * 删除自定义分词表
	 * @param id 自定义分词表主键
	 */
	void delete(Long id);

	/**
	 * 获取自定义分词表
	 * @param id 自定义分词表主键
	 * @return CustomWords  自定义分词表
	 */
	CustomWords getModel(Long id);

	/**
	 * 部署替换
	 * @return
	 */
    String deploy();


	/**
	 * 判断关键字是否存在
	 * @param keyword 关键字
	 * @return
	 */
	boolean isExist(String keyword);
}
