package com.enation.app.javashop.service.goods;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.dos.ParameterGroupDO;
import com.enation.app.javashop.model.goods.vo.ParameterGroupVO;

import java.util.List;

/**
 * 参数组业务层
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-20 16:14:17
 */
public interface ParameterGroupManager {

	/**
	 * 查询参数组列表
	 *
	 * @param page
	 *            页码
	 * @param pageSize
	 *            每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);

	/**
	 * 添加参数组
	 *
	 * @param parameterGroup
	 *            参数组
	 * @return ParameterGroup 参数组
	 */
	ParameterGroupDO add(ParameterGroupDO parameterGroup);

	/**
	 * 修改参数组
	 *
	 * @param groupName
	 *            参数组
	 * @param id
	 *            参数组主键
	 * @return ParameterGroup 参数组
	 */
	ParameterGroupDO edit(String groupName, Long id);

	/**
	 * 删除参数组
	 *
	 * @param id
	 *            参数组主键
	 */
	void delete(Long id);

	/**
	 * 获取参数组
	 *
	 * @param id
	 *            参数组主键
	 * @return ParameterGroup 参数组
	 */
	ParameterGroupDO getModel(Long id);

	/**
	 * 查询分类关联的参数组，包括参数
	 *
	 * @param categoryId 分类id
	 * @return
	 */
	List<ParameterGroupVO> getParamsByCategory(Long categoryId);

	/**
	 * 参数组上移或者下移
	 *  @param groupId 参数租id
	 * @param sortType 上移 up  下移 down
     */
	void groupSort(Long groupId, String sortType);

}
