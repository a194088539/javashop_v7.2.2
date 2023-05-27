package com.enation.app.javashop.service.goods;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.dos.DraftGoodsDO;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.dto.GoodsDTO;
import com.enation.app.javashop.model.goods.vo.DraftGoodsVO;

/**
 * 草稿商品业务层
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-26 10:40:34
 */
public interface DraftGoodsManager	{

	/**
	 * 查询草稿商品列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @param keyword 关键字
	 * @param shopCatPath 店铺分组path
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize, String keyword, String shopCatPath);
	/**
	 * 添加草稿商品
	 * @param goodsVO 草稿商品
	 * @return DraftGoods 草稿商品
	 */
	DraftGoodsDO add(GoodsDTO goodsVO);

	/**
	* 修改草稿商品
	* @param goodsVo 草稿商品
	* @param id 草稿商品主键
	* @return DraftGoods 草稿商品
	*/
	DraftGoodsDO edit(GoodsDTO goodsVo, Long id);

	/**
	 * 删除草稿商品
	 * @param draftGoodsIds 草稿商品主键
	 */
	void delete(Long[] draftGoodsIds);

	/**
	 * 获取草稿商品
	 * @param id 草稿商品主键
	 * @return DraftGoods  草稿商品
	 */
	DraftGoodsDO getModel(Long id);
	/**
	 * 获取草稿商品
	 * @param id 草稿商品主键
	 * @return DraftGoods  草稿商品
	 */
	DraftGoodsVO getVO(Long id);
	/**
	 * 草稿商品上架
	 * @param goodsVO 上架商品对象
	 * @param draftGoodsId 草稿商品id
	 * @return
	 */
	GoodsDO addMarket(GoodsDTO goodsVO, Long draftGoodsId);

}
