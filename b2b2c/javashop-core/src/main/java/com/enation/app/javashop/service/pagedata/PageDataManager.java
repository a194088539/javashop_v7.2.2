package com.enation.app.javashop.service.pagedata;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.pagedata.PageData;

/**
 * 楼层业务层
 * @author fk
 * @version v1.0
 * @since v7.0.0
 * 2018-05-21 16:39:22
 */
public interface PageDataManager {

	/**
	 * 查询楼层列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);
	/**
	 * 添加楼层
	 * @param page 楼层
	 * @return PageData 楼层
	 */
	PageData add(PageData page);

	/**
	* 修改楼层
	* @param page 楼层
	* @param id 楼层主键
	* @return PageData 楼层
	*/
	PageData edit(PageData page, Long id);

	/**
	 * 删除楼层
	 * @param id 楼层主键
	 */
	void delete(Long id);

	/**
	 * 获取楼层
	 * @param id 楼层主键
	 * @return PageData  楼层
	 */
	PageData getModel(Long id);

	/**
	 * 查询数据
	 * @return
	 * @param clientType
	 * @param pageType
	 */
    PageData queryPageData(String clientType, String pageType);

	/**
	 * 根据类型修改楼层
	 * @param pageData
	 * @return
	 */
	PageData editByType(PageData pageData);

	/**
	 * 根据类型查询数据
	 * @param clientType
	 * @param pageType
	 * @return
	 */
	PageData getByType(String clientType, String pageType);
}
