package com.enation.app.javashop.service.promotion.tool;

import com.enation.app.javashop.model.promotion.tool.vo.PromotionAbnormalGoods;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionDetailDTO;
import com.enation.app.javashop.model.promotion.tool.dto.PromotionGoodsDTO;
import com.enation.app.javashop.model.promotion.tool.vo.PromotionVO;

import java.util.List;

/**
 * 活动商品对照接口
 *
 * @author Snow create in 2018/3/21
 * @version v2.0
 * @since v7.0.0
 */
public interface PromotionGoodsManager {

    /**
     * 添加活动商品对照表
     *
     * @param list      商品列表
     * @param detailDTO 活动详情
     */
    void addAndCheck(List<PromotionGoodsDO> list, PromotionDetailDTO detailDTO);

    /**
     * 添加活动商品对照表
     *
     * @param list      商品列表
     * @param detailDTO 活动详情
     */
    void add(List<PromotionGoodsDTO> list, PromotionDetailDTO detailDTO);

    /**
     * 修改活动商品对照表
     *
     * @param list
     * @param detailDTO 活动详情
     */
    void edit(List<PromotionGoodsDTO> list, PromotionDetailDTO detailDTO);


    /**
     * 根据活动id和活动工具类型删除活动商品对照表
     *
     * @param activityId 活动id
     * @param promotionType 促销类型
     */
    void delete(Long activityId, String promotionType);

    /**
     * 根据活动id,活动工具类型和商品id删除活动商品对照表
     *
     * @param goodsId       商品id
     * @param activityId    活动id
     * @param promotionType 活动类型
     */
    void delete(Long goodsId, Long activityId, String promotionType);

    /**
     * 删除sku参与的活动(正在进行中或者未开始的促销活动)
     * @param delSkuIds 要商户的skuid
     */
    void delete(List<Long> delSkuIds);

    /**
     * 根据商品id读取商品参与的所有活动（有效的活动）
     *
     * @param goodsId 商品id
     * @return 返回活动的集合
     */
    List<PromotionVO> getPromotion(Long goodsId);

    /**
     * 根据活动ID和活动类型查出参与此活动的所有商品
     *
     * @param activityId    活动id
     * @param promotionType 活动类型
     * @return
     */
    List<PromotionGoodsDO> getPromotionGoods(Long activityId, String promotionType);

    /**
     * 清空key
     *
     * @param goodsId 商品id
     */
    void cleanCache(Long goodsId);

    /**
     * 重新写入缓存
     *
     * @param goodsId 商品id
     */
    void reputCache(Long goodsId);


    /**
     * 查询指定时间范围，是否有参与其他活动
     *
     * @param skuIds  商品skuid集合
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param promotionType   活动类型
     * @return
     */
    List<PromotionAbnormalGoods> checkPromotion(List<Long> skuIds, Long startTime, Long endTime, String promotionType);

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
