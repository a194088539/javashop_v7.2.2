package com.enation.app.javashop.service.goods;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.goods.dos.TagsDO;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import com.enation.app.javashop.model.goods.vo.TagGoodsNum;

import java.util.List;

/**
 * 商品标签业务层
 * @author fk
 * @version v2.0
 * @since v7.0.0
 * 2018-03-28 14:49:36
 */
public interface TagsManager	{

	/**
	 * 查询商品标签列表
	 * @param page 页码
	 * @param pageSize 每页数量
	 * @return WebPage
	 */
	WebPage list(long page, long pageSize);


	/**
	 * 增加店铺标签
	 * @param sellerId 店铺标签
	 */
	void addShopTags(Long sellerId);

	/**
	 * 查询某标签下的商品
	 * @param tagId 标签ID
	 * @param pageNo 每页
	 * @param pageSize 每页数量
	 * @return
	 */
	WebPage queryTagGoods(Long tagId, Long pageNo, Long pageSize);


	/**
	 * 保存标签商品
	 * @param tagId 标签id
	 * @param goodsIds 商品id数组
	 * @return
	 */
	void saveTagGoods(Long tagId, Long[] goodsIds);


	/**
	 * 查询某个卖家的标签商品
	 * @param sellerId 卖家id
	 * @param num  数量
	 * @param mark 标签关键字
	 * @return
	 */
	List<GoodsSelectLine> queryTagGoods(Long sellerId, Integer num, String mark);

	/**
	 * 查询一个标签
	 * @param id 标签id
	 * @return
	 */
	TagsDO getModel(Long id);

	/**
	 * 查询某个店铺的标签商品数量
	 * @param shopId 店铺id
	 * @return
	 */
    TagGoodsNum queryGoodsNumByShopId(Long shopId);
}
