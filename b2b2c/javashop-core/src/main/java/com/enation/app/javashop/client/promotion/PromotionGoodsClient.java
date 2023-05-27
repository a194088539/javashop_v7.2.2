package com.enation.app.javashop.client.promotion;

import com.enation.app.javashop.model.promotion.fulldiscount.vo.FullDiscountVO;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.model.promotion.minus.vo.MinusVO;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDTO;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionVO;

import java.util.List;

/**
 * 促销活动客户端
 *
 * @author zh
 * @version v7.0
 * @date 19/3/28 上午11:10
 * @since v7.0
 */
public interface PromotionGoodsClient {
    /**
     * 删除促销活动商品
     *
     * @param goodsId    商品id
     * @param type       活动类型
     * @param activityId 活动id
     */
    void delPromotionGoods(Long goodsId, String type, Long activityId);


    /**
     * 删除促销活动商品
     *
     * @param delSkuIds
     */
    void delPromotionGoods(List<Long> delSkuIds);


    /**
     * 根据商品id读取商品参与的所有活动（有效的活动）
     *
     * @param goodsId
     * @return 返回活动的集合
     */
    List<PromotionVO> getPromotion(Long goodsId);

    /**
     * 回归秒杀库存
     *
     * @param promotionDTOList
     */
    void rollbackSeckillStock(List<PromotionDTO> promotionDTOList);

    /**
     * 增加已销售库存数量
     *
     * @param promotionDTOList
     * @return
     */
    boolean addSeckillSoldNum(List<PromotionDTO> promotionDTOList);

    /**
     * 根据限时抢购促销活动id和商品申请状态获取申请参与活动的商品集合
     *
     * @param seckillId 限时抢购促销活动id
     * @param status    申请状态,APPLY:申请中,PASS:已通过,FAIL:已驳回
     * @return
     */
    List<SeckillApplyDO> getSeckillGoodsList(Long seckillId, String status);

    /**
     * 回滚团购库存
     *
     * @param promotionDTOList
     * @param orderSn
     */
    void rollbackGroupbuyStock(List<PromotionDTO> promotionDTOList, String orderSn);

    /**
     * 扣减团购商品库存
     *
     * @param orderSn
     * @param promotionDTOList
     */
    boolean cutGroupbuyQuantity(String orderSn, List<PromotionDTO> promotionDTOList);

    /**
     * 根据商品id，修改团购商品信息
     *
     * @param goodsIds
     */
    void updateGroupbuyGoodsInfo(Long[] goodsIds);

    /**
     * 恢复团购商品库存
     *
     * @param orderSn
     */
    void addGroupbuyQuantity(String orderSn);

    /**
     * 更新团购购买商品数量
     *
     * @param goodid 商品id
     * @param num    购买数量
     */
    boolean renewGroupbuyBuyNum(Long goodid, Integer num, Long productId);

    /**
     * 根据活动ID和活动类型查出参与此活动的所有商品
     *
     * @param activityId
     * @param promotionType
     * @return
     */
    List<PromotionGoodsDO> getPromotionGoods(Long activityId, String promotionType);

    /**
     * 从数据库获取促销信息
     *
     * @param fdId 满优惠活动ID
     * @return 满优惠活动实体
     */
    FullDiscountVO getFullDiscountModel(Long fdId);

    /**
     * 根据id获取单品立减商品
     *
     * @param minusId 单品立减活动Id
     * @return MinusVO
     */
    MinusVO getMinusFromDB(Long minusId);

    /**
     * 获取第二件半价
     *
     * @param id 第二件半价主键
     * @return HalfPrice  第二件半价
     */
    HalfPriceVO getHalfPriceFromDB(Long id);

    /**
     * 根据活动类型和当前时间获取促销活动商品信息集合
     * @param promotionType 活动类型
     * @param nowTime 当前时间
     * @return
     */
    List<PromotionGoodsDO> getPromotionGoodsList(String promotionType, Long nowTime);

    /**
     * 修改促销商品表中的活动结束时间
     * @param endTime 结束时间
     * @param pgId 促销商品ID
     */
    void updatePromotionEndTime(Long endTime, Long pgId);

}
