package com.enation.app.javashop.mapper.statistics;

import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 店铺概况管理mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-03
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ShopProfileStatisticsMapper {

    /**
     * 查询订单总金额，买家总数，订单总数，下单商品总数
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    Map<String, Object> selectDataList(@Param("orderStatus") String orderStatus,
                                       @Param("payStatus") String payStatus,
                                       @Param("startTime") long startTime,
                                       @Param("endTime") long endTime,
                                       @Param("sellerId") Long sellerId);

    /**
     * 查询商品总数，收藏总数
     * @param sellerId 商家id
     * @return
     */
    Map<String, Object> selectGoodsDataList(@Param("sellerId") Long sellerId);

    /**
     * 查询商家被收藏数量
     * @param sellerId
     * @return
     */
    List<Map<String, Object>> selectShopDataList(@Param("sellerId") Long sellerId);

}
