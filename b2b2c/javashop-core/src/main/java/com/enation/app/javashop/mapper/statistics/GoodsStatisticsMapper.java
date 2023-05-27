package com.enation.app.javashop.mapper.statistics;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品统计mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-07-31
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GoodsStatisticsMapper {

    /**
     * 价格销量统计
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param categoryId 分类id
     * @param sellerId 商家id
     * @return
     */
    List<Map<String, Object>> selectPriceSalesList(@Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                                   @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                                   @Param("startTime") long startTime,
                                                   @Param("endTime") long endTime,
                                                   @Param("categoryId") Long categoryId,
                                                   @Param("sellerId") Long sellerId);

    /**
     * 分页查询每个商品的下单总金额
     * @param page 分页数据
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param categoryId 分类id
     * @param sellerId 商家id
     * @return
     */
    IPage<Map<String,Object>> selectHotSalesMoneyPage(IPage page,
                                  @Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                  @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                  @Param("startTime") long startTime,
                                  @Param("endTime") long endTime,
                                  @Param("categoryId") Long categoryId,
                                  @Param("sellerId") Long sellerId);

    /**
     * 查询每个商品的下单总数
     * @param page 分页数据
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param categoryId 分类id
     * @param sellerId 商家id
     * @return
     */
    IPage<Map<String,Object>> selectHotSalesNumPage(IPage page,
                                @Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                @Param("startTime") long startTime,
                                @Param("endTime") long endTime,
                                @Param("categoryId") Long categoryId,
                                @Param("sellerId") Long sellerId);

    /**
     * 查询每个商品的下单总数，下单商品总数，总价格
     * @param page 分页数据
     * @param tableNameEsSssOrderGoodsData es_sss_order_goods_data表或对应的年份表
     * @param tableNameEsSssOrderData es_sss_order_data表或对应的年份表
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param goodsName 商品名称
     * @param categoryId 分类id
     * @param sellerId 商家id
     * @return
     */
    IPage<Map<String,Object>> selectSaleDetailsPage(IPage page,
                                @Param("tableName_es_sss_order_goods_data") String tableNameEsSssOrderGoodsData,
                                @Param("tableName_es_sss_order_data") String tableNameEsSssOrderData,
                                @Param("startTime") long startTime,
                                @Param("endTime") long endTime,
                                @Param("goodsName") String goodsName,
                                @Param("categoryId") Long categoryId,
                                @Param("sellerId") Long sellerId);

    /**
     * 查询商品名称，价格，收藏数量，商家名称
     * @param page 分页数据
     * @param tableNameEsSssGoodsData es_sss_goods_data表或对应的年份表
     * @param tableNameEsSssShopData es_sss_shop_data表或对应的年份表
     * @param categoryId 分类id
     * @param sellerId 商家id
     * @return
     */
    IPage<Map<String,Object>> selectGoodsCollectPage(IPage page,
                                 @Param("tableName_es_sss_goods_data") String tableNameEsSssGoodsData,
                                 @Param("tableName_es_sss_shop_data") String tableNameEsSssShopData,
                                 @Param("categoryId") Long categoryId,
                                 @Param("sellerId") Long sellerId);
}
