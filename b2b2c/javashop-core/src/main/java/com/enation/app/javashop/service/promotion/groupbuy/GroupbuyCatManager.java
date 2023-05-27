package com.enation.app.javashop.service.promotion.groupbuy;

import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyCatDO;
import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;

/**
 * 团购分类业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 16:08:03
 */
public interface GroupbuyCatManager	{

	/**
	 * 读取团购分类——分页
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	WebPage list(Long pageNo, Long pageSize);

	/**
	 * 查询团购分类列表
	 * @param parentId 分类父id
	 * @return WebPage
	 */
	List<GroupbuyCatDO>  getList(Long parentId);

	/**
	 * 添加团购分类
	 * @param groupbuyCat 团购分类
	 * @return GroupbuyCat 团购分类
	 */
	GroupbuyCatDO add(GroupbuyCatDO groupbuyCat);

	/**
	* 修改团购分类
	* @param groupbuyCat 团购分类
	* @param id 团购分类主键
	* @return GroupbuyCat 团购分类
	*/
	GroupbuyCatDO edit(GroupbuyCatDO groupbuyCat, Long id);

	/**
	 * 删除团购分类
	 * @param id 团购分类主键
	 */
	void delete(Long id);

	/**
	 * 获取团购分类
	 * @param id 团购分类主键
	 * @return GroupbuyCat  团购分类
	 */
	GroupbuyCatDO getModel(Long id);

}
