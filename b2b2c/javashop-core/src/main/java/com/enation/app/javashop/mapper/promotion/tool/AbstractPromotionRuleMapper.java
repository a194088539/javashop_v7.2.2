package com.enation.app.javashop.mapper.promotion.tool;

import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 活动规则检测mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-11
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface AbstractPromotionRuleMapper {

    /**
     * 查询某个时间段是否存第二件半价活动
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param sellerId 商家id
     * @param activityId 活动id
     * @return 返回活动数量
     */
    int selectHalfPriceCount(@Param("startTime") long startTime,
                             @Param("endTime") long endTime,
                             @Param("sellerId") Long sellerId,
                             @Param("activityId") Long activityId);

    /**
     * 查询某个时间段是否存在单品立减活动
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param sellerId 商家id
     * @param activityId 活动id
     * @return 返回活动数量
     */
    int selectMinusCount(@Param("startTime") long startTime,
                         @Param("endTime") long endTime,
                         @Param("sellerId") Long sellerId,
                         @Param("activityId") Long activityId);

    /**
     * 查询某个时间段是否存在满优惠活动
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param sellerId 商家id
     * @param activityId 活动id
     * @return 返回活动数量
     */
    int selectFullDiscountCount(@Param("startTime") long startTime,
                                @Param("endTime") long endTime,
                                @Param("sellerId") Long sellerId,
                                @Param("activityId") Long activityId);

    /**
     * 查询某个时间段是否存在团购活动
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param deleteStatus 删除状态
     * @param activityId 活动id
     * @return 返回活动数量
     */
    int selectGroupbuyActiveCount(@Param("startTime") long startTime,
                                  @Param("endTime") long endTime,
                                  @Param("deleteStatus") String deleteStatus,
                                  @Param("activityId") Long activityId);

    /**
     * 查询某个时间段是否存在限时抢购活动
     * @param startTime 开始时间
     * @param activityId 活动id
     * @return 返回活动数量
     */
    int selectSeckillCount(@Param("startTime") long startTime, @Param("activityId") Long activityId);
}
