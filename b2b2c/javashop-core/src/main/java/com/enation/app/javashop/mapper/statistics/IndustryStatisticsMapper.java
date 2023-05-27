package com.enation.app.javashop.mapper.statistics;

import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 行业分析统计mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-07-31
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface IndustryStatisticsMapper {

    /**
     * 查询每个行业的订单数
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectOrderQuantityList(@Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                                      @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                      @Param("startTime") long startTime,
                                                      @Param("endTime") long endTime,
                                                      @Param("sellerId") Long sellerId);

    /**
     * 查询每个行业的下单商品总数
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectGoodsNumList(@Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                                 @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                 @Param("startTime") long startTime,
                                                 @Param("endTime") long endTime,
                                                 @Param("sellerId") Long sellerId);

    /**
     * 查询每个行业的下单商品总金额
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectOrderMoneyList(@Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                                   @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                   @Param("startTime") long startTime,
                                                   @Param("endTime") long endTime,
                                                   @Param("sellerId") Long sellerId);

    /**
     * 查询有销量商品数
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param categoryId 分类id
     * @param sellerId 商家id
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @return
     */
    Integer selectSoldGoodsNum(@Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                               @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                               @Param("categoryId") Long categoryId,
                               @Param("sellerId") Long sellerId,
                               @Param("orderStatus") String orderStatus,
                               @Param("payStatus") String payStatus);

    /**
     * 查询商品数和总价格
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param categoryId 分类id
     * @param sellerId 商家id
     * @return
     */
    Map selectSoldNum(@Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                      @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                      @Param("categoryId") Long categoryId,
                      @Param("sellerId") Long sellerId);

    /**
     * 查询平均价格
     * @param categoryId 分类id
     * @param sellerId 商家id
     * @return
     */
    String selectAvgPrice(@Param("categoryId") Long categoryId, @Param("sellerId") Long sellerId);
}
