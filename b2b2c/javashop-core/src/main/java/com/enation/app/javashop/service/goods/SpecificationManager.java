package com.enation.app.javashop.service.goods;

import com.enation.app.javashop.framework.database.WebPage;

import java.util.List;

import com.enation.app.javashop.model.goods.dos.SpecificationDO;
import com.enation.app.javashop.model.goods.vo.SelectVO;
import com.enation.app.javashop.model.goods.vo.SpecificationVO;

/**
 * 规格项业务层
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-20 09:31:27
 */
public interface SpecificationManager {

	/**
	 * 查询规格项列表
	 *
	 * @param page
	 *            页码
	 * @param pageSize
	 *            每页数量
	 * @param keyword
	 * 			  关键字
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize, String keyword);

	/**
	 * 添加规格项
	 *
	 * @param specification
	 *            规格项
	 * @return Specification 规格项
	 */
	SpecificationDO add(SpecificationDO specification);

	/**
	 * 修改规格项
	 *
	 * @param specification
	 *            规格项
	 * @param id
	 *            规格项主键
	 * @return Specification 规格项
	 */
	SpecificationDO edit(SpecificationDO specification, Long id);

	/**
	 * 删除规格项
	 *
	 * @param ids
	 *            规格项主键
	 */
	void delete(Long[] ids);

	/**
	 * 获取规格项
	 *
	 * @param id
	 *            规格项主键
	 * @return Specification 规格项
	 */
	SpecificationDO getModel(Long id);

	/**
	 * 查询分类绑定的规格，系统规格
	 *
	 * @param categoryId 分类id
	 * @return
	 */
	List<SelectVO> getCatSpecification(Long categoryId);

	/**
	 * 商家自定义规格
	 *
	 * @param categoryId 分类id
	 * @param specName 规格名称
	 * @return
	 */
	SpecificationDO addSellerSpec(Long categoryId, String specName);

	/**
	 * 商家查询某分类的规格
	 *
	 * @param categoryId 分类id
	 * @return
	 */
	List<SpecificationVO> querySellerSpec(Long categoryId);

	/**
	 * 商家查询某分类的规格值是否存在
	 *
	 * @param sellerId 商家id
	 * @param specId 规格id
	 * @param specValue 规格值
	 * @return
	 */
	boolean flagSellerSpec(Long sellerId, Long specId, String specValue);

}
