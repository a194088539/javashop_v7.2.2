package com.enation.app.javashop.service.promotion.seckill;

import com.enation.app.javashop.framework.database.WebPage;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillQueryParam;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillGoodsVO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDTO;

import java.util.List;
import java.util.Map;

/**
 * 限时抢购申请业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-04-02 17:30:09
 */
public interface SeckillGoodsManager {

	/**
	 * 查询限时抢购申请列表
	 * @param queryParam 查询参数
	 * @return WebPage
	 */
	WebPage list(SeckillQueryParam queryParam);


	/**
	 * 删除限时抢购申请
	 * @param id 限时抢购申请主键
	 */
	void delete(Long id);

	/**
	 * 获取限时抢购申请
	 * @param id 限时抢购申请主键
	 * @return SeckillApply  限时抢购申请
	 */
	SeckillApplyDO getModel(Long id);


	/**
	 * 添加限时抢购申请
	 * @param list
	 */
	void addApply(List<SeckillApplyDO> list);

	/**
	 * 增加已销售库存数量
	 * @param promotionDTOList
	 * @return
	 */
	boolean addSoldNum(List<PromotionDTO> promotionDTOList);

	/**
	 * 读取当天限时抢购活动的商品
	 * @return
	 */
	Map<Integer, List<SeckillGoodsVO>> getSeckillGoodsList();


	/**
	 * 将商品压入缓存
	 * @param startTime
	 * @param rangeTime
	 * @param goodsVO
	 */
	void addRedis(Long startTime,Integer rangeTime,SeckillGoodsVO goodsVO);


	/**
	 * 根据时刻读取限时抢购商品列表
	 * @param rangeTime
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	List getSeckillGoodsList(Integer rangeTime, Long pageNo, Long pageSize);

	/**
	 * 回滚库存
	 * @param promotionDTOList
	 */
	void rollbackStock(List<PromotionDTO> promotionDTOList);

	/**
	 * 删除限时抢购商品
	 * @param delSkuIds
	 */
	void deleteSeckillGoods(List<Long> delSkuIds);

	/**
	 * 根据限时抢购ID删除商品
	 * @param seckillId
	 */
	void deleteBySeckillId(Long seckillId);

	/**
	 * 根据限时抢购促销活动id和商品申请状态获取申请参与活动的商品集合
	 * @param seckillId 限时抢购促销活动id
	 * @param status 申请状态,APPLY:申请中,PASS:已通过,FAIL:已驳回
	 * @return
	 */
	List<SeckillApplyDO> getGoodsList(Long seckillId, String status);
}
