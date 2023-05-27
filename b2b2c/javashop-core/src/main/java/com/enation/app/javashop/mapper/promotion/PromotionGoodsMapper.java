package com.enation.app.javashop.mapper.promotion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.tool.dos.PromotionGoodsDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * PromotionGoods的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface PromotionGoodsMapper extends BaseMapper<PromotionGoodsDO> {

    /**
     * 查询某个商品在某个时段是否参加了团购
     * @param skuId
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 返回符合条件的记录数
     */
    int selectCountByTime(@Param("skuId") Long skuId, @Param("startTime") long startTime, @Param("endTime") long endTime);

    /**
     * 根据活动类型和当前时间获取促销活动商品信息集合
     * @param promotionType 促销活动类型
     * @param nowTime 当前时间
     * @return
     */
    List<PromotionGoodsDO> selectGoodsList(@Param("promotion_type") String promotionType, @Param("now_time") Long nowTime);

}
