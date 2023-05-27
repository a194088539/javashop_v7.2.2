package com.enation.app.javashop.service.goods;

import java.util.List;

import com.enation.app.javashop.model.goods.dos.GoodsParamsDO;
import com.enation.app.javashop.model.goods.vo.GoodsParamsGroupVO;

/**
 * 草稿商品参数表业务层
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-26 11:31:20
 */
public interface DraftGoodsParamsManager	{

	/**
	 * 添加草稿商品的参数集合
	 * @param goodsParamsList 商品参数集合
	 * @param draftGoodsId 草稿id
	 */
	void addParams(List<GoodsParamsDO> goodsParamsList, Long draftGoodsId);

	/**
	 * 查询分类关联的参数，同时返回已经添加的值
	 * @param categoryId 分类id
	 * @param draftGoodsId 草稿id
	 * @return
	 */
	List<GoodsParamsGroupVO> getParamByCatAndDraft(Long categoryId, Long draftGoodsId);

}
