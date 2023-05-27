package com.enation.app.javashop.service.promotion.groupbuy;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyGoodsDO;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyGoodsVO;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyQueryParam;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDTO;

import java.util.List;

/**
 * 团购商品业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 16:57:26
 */
public interface GroupbuyGoodsManager	{


	/**
	 * 商家查询团购商品列表
	 *
	 * @param param 查询参数
	 * @return WebPage
	 */
	WebPage listPage(GroupbuyQueryParam param);


	/**
	 * 买家查询团购商品列表
	 *
	 * @param param 查询参数
	 * @return WebPage
	 */
	WebPage listPageByBuyer(GroupbuyQueryParam param);


	/**
	 * 添加团购商品
	 *
	 * @param groupbuyGoods 团购商品
	 * @return GroupbuyGoods 团购商品
	 */
	GroupbuyGoodsDO add(GroupbuyGoodsDO groupbuyGoods);

	/**
	 * 修改团购商品
	 *
	 * @param groupbuyGoods 团购商品
	 * @param id            团购商品主键
	 * @return GroupbuyGoods 团购商品
	 */
	GroupbuyGoodsDO edit(GroupbuyGoodsDO groupbuyGoods, Long id);

	/**
	 * 删除团购商品
	 *
	 * @param id 团购商品主键
	 */
	void delete(Long id);


	/**
	 * 删除团购商品
	 *
	 * @param delSkuIds
	 */
	void deleteGoods(List<Long> delSkuIds);

	/**
	 * 获取团购商品
	 *
	 * @param gbId 团购商品主键
	 * @return GroupbuyGoods  团购商品
	 */
	GroupbuyGoodsVO getModel(Long gbId);


	/**
	 * 获取团购商品
	 *
	 * @param actId   团购活动ID
	 * @param goodsId 商品ID
	 * @return GroupbuyGoods  团购商品
	 */
	GroupbuyGoodsDO getModel(Long actId, Long goodsId);

	/**
	 * 获取团购商品
	 *
	 * @param actId   团购活动ID
	 * @param goodsId 商品ID
	 * @param skuId   skuid
	 * @return GroupbuyGoods  团购商品
	 */
	GroupbuyGoodsDO getModel(Long actId, Long goodsId, Long skuId);

	/**
	 * 验证操作权限<br/>
	 * 如有问题直接抛出权限异常
	 *
	 * @param id
	 */
	void verifyAuth(Long id);

	/**
	 * 修改审核状态
	 *  @param gbId
	 * @param status
	 */
	void updateStatus(Long gbId, Integer status);


	/**
	 * 扣减团购商品库存
	 *
	 * @param orderSn
	 * @param promotionDTOList
	 */
	boolean cutQuantity(String orderSn, List<PromotionDTO> promotionDTOList);

	/**
	 * 更新团购购买商品数量
	 *
	 * @param goodid    商品id
	 * @param num       购买数量
	 * @param productId skuId
	 */
	boolean renewBuyNum(Long goodid, Integer num, Long productId);


	/**
	 * 恢复团购商品库存
	 *
	 * @param orderSn
	 */
	void addQuantity(String orderSn);


	/**
	 * 查询团购商品信息和商品库存信息
	 *
	 * @param id
	 * @return
	 */
	GroupbuyGoodsVO getModelAndQuantity(Long id);


	/**
	 * 根据商品id，修改团购商品信息
	 *
	 * @param goodsIds
	 */
	void updateGoodsInfo(Long[] goodsIds);

	/**
	 * 回滚库存
	 *
	 * @param promotionDTOList
	 * @param orderSn
	 */
	void rollbackStock(List<PromotionDTO> promotionDTOList, String orderSn);




}
