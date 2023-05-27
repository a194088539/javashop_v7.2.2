package com.enation.app.javashop.service.goods;

import java.util.List;

import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;

/**
 * 草稿商品sku业务层
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-26 11:38:06
 */
public interface DraftGoodsSkuManager	{

	/**
	 * 添加sku规格列表
     * @param skuList sku集合
     * @param draftGoodsId 草稿商品id
     */
	void add(List<GoodsSkuVO> skuList, Long draftGoodsId);

	/**
	 * 查询草稿箱的sku列表
	 * @param draftGoodsId 草稿商品id
	 * @return
	 */
	List<GoodsSkuVO> getSkuList(Long draftGoodsId);

}
