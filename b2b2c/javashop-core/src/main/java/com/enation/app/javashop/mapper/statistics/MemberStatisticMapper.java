package com.enation.app.javashop.mapper.statistics;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 会员统计mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-03
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MemberStatisticMapper {

    /**
     * 查询每个会员的下单总数
     * @param page 分页数据
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param sellerId 商家id
     * @return
     */
    IPage<Map<String,Object>> selectMemberOrderQuantityPage(Page page,
                                        @Param("startTime") long startTime,
                                        @Param("endTime") long endTime,
                                        @Param("orderStatus") String orderStatus,
                                        @Param("payStatus") String payStatus,
                                        @Param("sellerId") long sellerId);

    /**
     * 查询每个会员的下单商品总数
     * @param page 分页数据
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param sellerId 商家id
     * @return
     */
    IPage<Map<String,Object>> selectMemberGoodsNumPage(Page page,
                                   @Param("startTime") long startTime,
                                   @Param("endTime") long endTime,
                                   @Param("orderStatus") String orderStatus,
                                   @Param("payStatus") String payStatus,
                                   @Param("sellerId") long sellerId);

    /**
     * 查询每个会员的下单商品总价格
     * @param page 分页数据
     * @param startTime 订单开始时间
     * @param endTime 订单结束时间
     * @param orderStatus 订单状态
     * @param payStatus 支付状态
     * @param sellerId 商家id
     * @return
     */
    IPage<Map<String,Object>> selectMemberMoneyPage(Page page,
                                @Param("startTime") long startTime,
                                @Param("endTime") long endTime,
                                @Param("orderStatus") String orderStatus,
                                @Param("payStatus") String payStatus,
                                @Param("sellerId") long sellerId);
}
