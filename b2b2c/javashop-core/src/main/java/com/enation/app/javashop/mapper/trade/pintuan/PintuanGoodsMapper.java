package com.enation.app.javashop.mapper.trade.pintuan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.pintuan.PinTuanGoodsVO;
import com.enation.app.javashop.model.promotion.pintuan.Pintuan;
import com.enation.app.javashop.model.promotion.pintuan.PintuanGoodsDO;
import com.enation.app.javashop.model.promotion.pintuan.PintuanGoodsDTO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;

/**
 * PintuanGoodsMapper接口
 * @author fk
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface PintuanGoodsMapper extends BaseMapper<PintuanGoodsDO> {

    /**
     * 获取拼团商品集合
     * @param skuId sku
     * @param now 当前时间
     * @param time 时间
     * @return
     */
    List<PinTuanGoodsVO> getDetail(@Param("sku_id") Long skuId, @Param("now_time") Long now, @Param("time") Long time);


    /**
     * 查询时间轴以外的商品
     * @param map 条件
     * @return
     */
    List<PintuanGoodsDTO> queryPromotionGoods(@Param("param") Map map);


    /**
     * 删除拼团商品，
     * @param delSkuIds
     * @param dateline
     */
    void deletePinTuanGoods(@Param("sku_ids") List<Long> delSkuIds, @Param("date_time") long dateline);

    /**
     * 查询拼团商品
     * @param skuId skuId
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    PintuanGoodsDO selectPintuanGoods(@Param("sku_id") Long skuId, @Param("start_time")long startTime, @Param("end_time")long endTime);

    /**
     * 根据商品id查询数据库中的拼团商品
     * @param goodsId 商品id
     * @param start 开始时间
     * @param end 结束时间
     * @return
     */
    List<PinTuanGoodsVO> selectPintuanGoodsByGoodsId(@Param("goods_id") Long goodsId, @Param("start_time") Long start, @Param("end_time") Long end);

    /**
     * 查询拼团商品VO集合
     * @param pinTuanId 拼团id
     * @return
     */
    List<PinTuanGoodsVO> selectPinTuanGoodsVOList(@Param("pintuan_id") Long pinTuanId);
}
