package com.enation.app.javashop.service.goods;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.dos.ParametersDO;

/**
 * 参数业务层
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-20 16:14:31
 */
public interface ParametersManager {

	/**
	 * 查询参数列表
	 *
	 * @param page
	 *            页码
	 * @param pageSize
	 *            每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);

	/**
	 * 添加参数
	 *
	 * @param parameters
	 *            参数
	 * @return Parameters 参数
	 */
	ParametersDO add(ParametersDO parameters);

	/**
	 * 修改参数
	 *
	 * @param parameters
	 *            参数
	 * @param id
	 *            参数主键
	 * @return Parameters 参数
	 */
	ParametersDO edit(ParametersDO parameters, Long id);

	/**
	 * 删除参数
	 *
	 * @param id
	 *            参数主键
	 */
	void delete(Long id);

	/**
	 * 获取参数
	 *
	 * @param id
	 *            参数主键
	 * @return Parameters 参数
	 */
	ParametersDO getModel(Long id);

	/**
	 * 参数排序
	 *  @param paramId 参数id
	 * @param sortType 上移 up 下移 down
     */
	void paramSort(Long paramId, String sortType);

	/**
	 * 删除参数，使用参数组
	 *
	 * @param groupId 参数组id
	 */
	void deleteByGroup(Long groupId);

}
