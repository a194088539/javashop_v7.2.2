package com.enation.app.javashop.mapper.statistics;

import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 平台后台 流量分析mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-03
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface PageViewStatisticMapper {

    /**
     * 查询每年或每月的店铺访问总量
     * @param tableNameEsSssShopPv es_sss_shop_pv表或对应的年份表
     * @param type 按年或月查询
     * @param year 年份
     * @param month 月份
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectVsNum(@Param("tableName_es_sss_shop_pv") String tableNameEsSssShopPv,
                                          @Param("type") String type,
                                          @Param("year") Integer year,
                                          @Param("month") Integer month,
                                          @Param("sellerId") Long sellerId);

    /**
     * 查询每个商品每年或每月的访问总量
     * @param tableNameEsSssGoodsPv es_sss_goods_pv表或对应的年份表
     * @param type 按年或月查询
     * @param year 年份
     * @param month 月份
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> countGoods(@Param("tableName_es_sss_goods_pv") String tableNameEsSssGoodsPv,
                                         @Param("type") String type,
                                         @Param("year") Integer year,
                                         @Param("month") Integer month,
                                         @Param("sellerId") Long sellerId);
}
