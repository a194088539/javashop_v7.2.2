package com.enation.app.javashop.service.shop;

import java.util.List;

import com.enation.app.javashop.model.shop.dos.ShipTemplateDO;
import com.enation.app.javashop.model.shop.vo.ShipTemplateSellerVO;
import com.enation.app.javashop.model.shop.vo.ShipTemplateVO;

/**
 * 运费模版业务层
 * @author zjp
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-28 21:44:49
 */
public interface ShipTemplateManager{


	/**
	 * 新增店铺运费模板
	 * @param tamplate 运费模板信息
	 * @return
	 */
	ShipTemplateDO save(ShipTemplateSellerVO tamplate);

	/**
	 * 修改店铺运费模板
	 * @param template 运费模板信息
	 * @return
	 */
	ShipTemplateDO edit(ShipTemplateSellerVO template);


	/**
	 * 获取商家运送方式
	 * @param sellerId 商家ID
	 * @return
	 */
	List<ShipTemplateSellerVO> getStoreTemplate(Long sellerId);


	/**
	 * 获取商家运送方式
	 * @param templateId 运费模板ID
	 * @return
	 */
	ShipTemplateVO getFromCache(Long templateId);

	/**
	 * 删除店铺运费模板
     * @param templateId 运费模板ID
     */
	void delete(Long templateId);

	/**
	 * 数据库中查询一个运费模板VO
	 * @param templateId 运费模板ID
	 * @return
	 */
	ShipTemplateSellerVO getFromDB(Long templateId);

	/**
	 * 获取运费模板的脚本
	 * @param id 运费模板ID
	 * @return
	 */
	List<String> getScripts(Long id);

	/**
	 * 新增运费模板的时候，生成script缓存到redis
	 *
	 * @param shipTemplateVO 运费模板
	 */
	List<String> cacheShipTemplateScript(ShipTemplateVO shipTemplateVO);
}
