package com.enation.app.javashop.service.system;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.base.dos.SensitiveWords;

import java.util.List;

/**
 * 敏感词业务层
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-08-02 11:30:59
 */
public interface SensitiveWordsManager	{

	/**
	 * 查询敏感词列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @param keyword
     * @return WebPage
	 */
	WebPage list(long page, long pageSize, String keyword);
	/**
	 * 添加敏感词
	 * @param sensitiveWords 敏感词
	 * @return SensitiveWords 敏感词
	 */
	SensitiveWords add(SensitiveWords sensitiveWords);

	/**
	* 修改敏感词
	* @param sensitiveWords 敏感词
	* @param id 敏感词主键
	* @return SensitiveWords 敏感词
	*/
	SensitiveWords edit(SensitiveWords sensitiveWords, Long id);

	/**
	 * 删除敏感词
	 * @param id 敏感词主键
	 */
	void delete(Long id);

	/**
	 * 获取敏感词
	 * @param id 敏感词主键
	 * @return SensitiveWords  敏感词
	 */
	SensitiveWords getModel(Long id);

	/**
	 * 查询需要过滤的敏感词汇
	 * @return
	 */
	List<String> listWords();

}
