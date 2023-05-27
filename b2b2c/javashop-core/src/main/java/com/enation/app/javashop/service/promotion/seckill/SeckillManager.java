package com.enation.app.javashop.service.promotion.seckill;

import com.enation.app.javashop.model.promotion.seckill.dto.SeckillAuditParam;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillQueryParam;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillGoodsVO;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillVO;
import com.enation.app.javashop.framework.database.WebPage;

/**
 * 限时抢购入库业务层
 * @author Snow
 * @version v7.0.0
 * @since v7.0.0
 * 2018-03-21 10:32:36
 */
public interface SeckillManager	{

	/**
	 * 查询限时抢购入库列表
	 * @param param 搜索参数
	 * @return WebPage
	 */
	WebPage list(SeckillQueryParam param);

	/**
	 * 添加限时抢购入库
	 * @param seckill 限时抢购入库
	 * @return Seckill 限时抢购入库
	 */
	SeckillVO add(SeckillVO seckill);

	/**
	* 修改限时抢购入库
	* @param seckill 限时抢购入库
	* @param id 限时抢购入库主键
	* @return Seckill 限时抢购入库
	*/
	SeckillVO edit(SeckillVO seckill, Long id);

	/**
	 * 删除限时抢购入库
	 * @param id 限时抢购入库主键
	 */
	void delete(Long id);

	/**
	 * 获取限时抢购入库
	 * @param id 限时抢购入库主键
	 * @return Seckill  限时抢购入库
	 */
	SeckillVO getModel(Long id);

	/**
	 * 根据商品ID，读取限时秒杀的活动信息
	 * @param goodsId
	 * @return
	 */
	SeckillGoodsVO getSeckillGoods(Long goodsId);

	/**
	 * 根据商品ID，skuID,读取限时秒杀的活动信息
	 * @param goodsId
	 * @param skuId
	 * @return
	 */
	SeckillGoodsVO getSeckillSku(Long goodsId, Long skuId);

	/**
	 * 批量审核商品
	 * @param param
	 */
	void batchAuditGoods(SeckillAuditParam param);

	/**
	 * 商家报名限时抢购活动
	 * @param sellerId
	 * @param seckillId
	 */
	void sellerApply(Long sellerId, Long seckillId);

	/**
	 * 关闭某限时抢购
	 * @param id
	 */
    void close(Long id);
}
