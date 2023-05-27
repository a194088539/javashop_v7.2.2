package com.enation.app.javashop.service.goods;

import com.enation.app.javashop.model.goods.dos.SpecValuesDO;
import com.enation.app.javashop.model.goods.enums.Permission;

import java.util.List;

/**
 * 规格值业务层
 *
 * @author fk
 * @version v2.0
 * @since v7.0.0 2018-03-20 10:23:53
 */
public interface SpecValuesManager {

	/**
	 * 添加规格值
	 *
	 * @param specValues
	 *            规格值
	 * @return SpecValues 规格值
	 */
	SpecValuesDO add(SpecValuesDO specValues);

	/**
	 * 修改规格值
	 *
	 * @param specValues
	 *            规格值
	 * @param id
	 *            规格值主键
	 * @return SpecValues 规格值
	 */
	SpecValuesDO edit(SpecValuesDO specValues, Long id);

	/**
	 * 获取规格值
	 *
	 * @param id
	 *            规格值主键
	 * @return SpecValues 规格值
	 */
	SpecValuesDO getModel(Long id);

	/**
	 * 获取某规格的规格值
	 *
	 * @param specId 规格id
	 * @param permission 权限
	 * @return
	 */
	List<SpecValuesDO> listBySpecId(Long specId, Permission permission);

	/**
	 * 添加某规格的规格值
	 *
	 * @param specId 规格id
	 * @param valueList 规格值集合
	 * @return
	 */
	List<SpecValuesDO> saveSpecValue(Long specId, String[] valueList);

}
