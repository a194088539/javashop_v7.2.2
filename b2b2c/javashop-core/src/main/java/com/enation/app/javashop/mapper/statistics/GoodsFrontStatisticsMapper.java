package com.enation.app.javashop.mapper.statistics;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商家中心，商品分析统计mapper
 *
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-07-31
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GoodsFrontStatisticsMapper {

    /**
     * 查询某一时间段商品统计库数据
     * @param sellerId 商家id
     * @param catPath 分类路径
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @return
     */
    List<Map<String, Object>> selectBySellerIdAndCatPath(@Param("sellerId") Long sellerId,
                                                         @Param("catPath") String catPath,
                                                         @Param("startTime") long startTime,
                                                         @Param("endTime") long endTime);

    /**
     * 分页查询商品详情
     * @param page 分页数据
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param sellerId 商家id
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param goodsName 商品名称
     * @param catPath 分类路径
     * @return
     */
    IPage<Map<String,Object>> selectGoodsStatisticsPage(IPage page,
                                    @Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                    @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                    @Param("sellerId") Long sellerId,
                                    @Param("startTime") long startTime,
                                    @Param("endTime") long endTime,
                                    @Param("goodsName") String goodsName,
                                    @Param("catPath") String catPath);

    /**
     * 分页查询商品下单金额
     * @param page 分页数据
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param sellerId 商家id
     * @param orderStatus 订单状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @return
     */
    IPage<Map<String,Object>> selectGoodsOrderPriceTopPage(IPage page,
                                       @Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                       @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                       @Param("sellerId") Long sellerId,
                                       @Param("orderStatus") String orderStatus,
                                       @Param("startTime") long startTime,
                                       @Param("endTime") long endTime);

    /**
     * 分页查询下单商品数量
     * @param page 分页数据
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param sellerId 商家id
     * @param orderStatus 订单状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @return
     */
    IPage<Map<String,Object>> selectGoodsNumTopPage(IPage page,
                                @Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                @Param("sellerId") Long sellerId,
                                @Param("orderStatus") String orderStatus,
                                @Param("startTime") long startTime,
                                @Param("endTime") long endTime);

    /**
     * 查询下单商品金额
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param sellerId 商家id
     * @param orderStatus 订单状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param topNum 查询的数量
     * @return
     */
    List<Map<String, Object>> selectGoodsOrderPriceTopList(@Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                                           @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                           @Param("sellerId") Long sellerId,
                                                           @Param("orderStatus") String orderStatus,
                                                           @Param("startTime") long startTime,
                                                           @Param("endTime") long endTime,
                                                           @Param("topNum") int topNum);

    /**
     * 查询商品购买数量
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param sellerId 商家id
     * @param orderStatus 订单状态
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param topNum 查询的数量
     * @return
     */
    List<Map<String, Object>> selectGoodsNumTopList(@Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                                    @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                    @Param("sellerId") Long sellerId,
                                                    @Param("orderStatus") String orderStatus,
                                                    @Param("startTime") long startTime,
                                                    @Param("endTime") long endTime,
                                                    @Param("topNum") int topNum);

}
