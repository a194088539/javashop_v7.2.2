package com.enation.app.javashop.mapper.statistics;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单统计mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-03
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface OrderStatisticsMapper {

    /**
     * 查询订单数据
     * @param page 分页数据
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @param orderStatus 订单状态
     * @return
     */
    IPage<Map<String,Object>> selectOrderPage(Page page,
                          @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                          @Param("startTime") long startTime,
                          @Param("endTime") long endTime,
                          @Param("sellerId") Long sellerId,
                          @Param("orderStatus") String orderStatus);

    /**
     * 查询订单数据
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @param orderStatus 订单状态
     * @param circle 时间格式（每年/每月）
     * @return
     */
    List<Map<String, Object>> selectOrderNumList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                 @Param("startTime") long startTime,
                                                 @Param("endTime") long endTime,
                                                 @Param("sellerId") Long sellerId,
                                                 @Param("orderStatus") String orderStatus,
                                                 @Param("circle") String circle);

    /**
     * 查询订单数据,下单总金额
     * @param page 分页数据
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @return
     */
    IPage<Map<String,Object>> selectSalesMoneyPage(Page page,
                               @Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                               @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                               @Param("startTime") long startTime,
                               @Param("endTime") long endTime);

    /**
     * 查询退货数据
     * @param page 分页数据
     * @param tableNameEsSssRefundData es_sss_refund_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @return
     */
    IPage<Map<String,Object>> selectAfterSalesMoneyPage(Page page,
                                    @Param("tableName_es_sss_refund_data") String tableNameEsSssRefundData,
                                    @Param("startTime") long startTime,
                                    @Param("endTime") long endTime);

    /**
     * 查询订单总金额，退还总金额
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param tableNameEsSssRefundData es_sss_refund_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @return
     */
    Map selectSalesMoneyTotal(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                              @Param("tableName_es_sss_refund_data") String tableNameEsSssRefundData,
                              @Param("startTime")long startTime,
                              @Param("endTime")long endTime);

    /**
     * 查询每个地区的买家数量
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectOrderRegionMemberList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                          @Param("startTime") long startTime,
                                                          @Param("endTime") long endTime,
                                                          @Param("sellerId") Long sellerId);

    /**
     * 查询每个地区的订单数量
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectOrderRegionNumList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                       @Param("startTime") long startTime,
                                                       @Param("endTime") long endTime,
                                                       @Param("sellerId") Long sellerId);

    /**
     * 查询每个地区的下单金额
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectOrderRegionMoneyList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                         @Param("startTime") long startTime,
                                                         @Param("endTime") long endTime,
                                                         @Param("sellerId") Long sellerId);

    /**
     * 查询每个地区的买家数量，下单总金额，订单数量
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectOrderRegionFormList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                        @Param("startTime") long startTime,
                                                        @Param("endTime") long endTime,
                                                        @Param("sellerId") Long sellerId);

    /**
     * 查询订单价格
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectUnitPriceList(@Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                                  @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                  @Param("startTime") long startTime,
                                                  @Param("endTime") long endTime,
                                                  @Param("sellerId") Long sellerId);

    /**
     * 查询每小时的订单数量
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectUnitTimeList(@Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                 @Param("startTime") long startTime,
                                                 @Param("endTime") long endTime,
                                                 @Param("sellerId") Long sellerId);

    /**
     * 查询退款金额，退款时间
     * @param tableNameEsSssRefundData es_sss_refund_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param circle 时间格式（每年/每月）
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectReturnMoneyList(@Param("tableName_es_sss_refund_data") String tableNameEsSssRefundData,
                                                    @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                    @Param("startTime") long startTime,
                                                    @Param("endTime") long endTime,
                                                    @Param("circle") String circle,
                                                    @Param("sellerId") Long sellerId);
}
