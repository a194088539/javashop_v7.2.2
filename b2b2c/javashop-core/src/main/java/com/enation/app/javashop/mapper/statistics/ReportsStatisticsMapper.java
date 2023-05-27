package com.enation.app.javashop.mapper.statistics;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商家中心 运营报告mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-03
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ReportsStatisticsMapper {

    /**
     * 查询每月或每天的下单金额
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param circle 时间格式（每年/每月）
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectSalesMoneyList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                   @Param("circle") String circle,
                                                   @Param("orderStatus") String orderStatus,
                                                   @Param("payStatus") String payStatus,
                                                   @Param("sellerId") Long sellerId);

    /**
     * 查询每月或每天的订单数量
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param circle 时间格式（每年/每月）
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectSalesNumList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                 @Param("circle") String circle,
                                                 @Param("orderStatus") String orderStatus,
                                                 @Param("payStatus") String payStatus,
                                                 @Param("sellerId") Long sellerId);

    /**
     * 查询订单数据
     * @param page 分页数据
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    IPage<Map<String,Object>> selectSalesPage(IPage page,
                          @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                          @Param("orderStatus") String orderStatus,
                          @Param("payStatus") String payStatus,
                          @Param("startTime") long startTime,
                          @Param("endTime") long endTime,
                          @Param("sellerId") Long sellerId);

    /**
     * 查询订单数量和订单金额
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    Map<String, Object> selectSalesSummaryMap(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                              @Param("orderStatus") String orderStatus,
                                              @Param("payStatus") String payStatus,
                                              @Param("startTime") long startTime,
                                              @Param("endTime") long endTime,
                                              @Param("sellerId") Long sellerId);

    /**
     * 查询每个省份的1.下单会员数 2.下单金额 3.下单量
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param sqlDifference 查询1.下单会员数 2.下单金额 3.下单量
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectRegionsMemberNumList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                         @Param("sqlDifference") String sqlDifference,
                                                         @Param("orderStatus") String orderStatus,
                                                         @Param("payStatus") String payStatus,
                                                         @Param("startTime") long startTime,
                                                         @Param("endTime") long endTime,
                                                         @Param("sellerId") Long sellerId);

    /**
     * 查询订单信息
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectOrderDistributionList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                          @Param("orderStatus") String orderStatus,
                                                          @Param("payStatus") String payStatus,
                                                          @Param("startTime") long startTime,
                                                          @Param("endTime") long endTime,
                                                          @Param("sellerId") Long sellerId);

    /**
     * 查询每小时的订单数量
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectPurchasePeriodList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                       @Param("orderStatus") String orderStatus,
                                                       @Param("payStatus") String payStatus,
                                                       @Param("startTime") long startTime,
                                                       @Param("endTime") long endTime,
                                                       @Param("sellerId") Long sellerId);
}
