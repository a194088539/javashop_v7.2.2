package com.enation.app.javashop.mapper.trade.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.trade.order.dos.OrderDO;
import com.enation.app.javashop.model.trade.order.dto.OrderQueryParam;
import com.enation.app.javashop.model.trade.order.vo.OrderDetailVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 订单查询业务mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-08
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface OrderQueryMapper {

    /**
     * 按条件分页查询订单列表
     * @param page 分页参数
     * @param paramDTO 查询参数
     * @return
     */
    IPage<OrderDO> selectOrderQueryPage(Page page, @Param("param") OrderQueryParam paramDTO);

    /**
     * 按条件查询订单导出列表
     * @param paramDTO 查询参数
     * @return
     */
    List<OrderDO> selectExportList(OrderQueryParam paramDTO);

    /**
     * 根据订单编号读取订单列表
     * @param tradeSn 订单编号
     * @return
     */
    List<OrderDetailVO> getOrderByTradeSn(String tradeSn);

    /**
     * 获取几个月之内购买过相关商品的订单数据
     * @param beforeMonthDateline  几个月之前的时间戳
     * @param goodsId  商品id
     * @param memberId 会员id
     * @return
     */
    List<OrderDO> listOrderByGoods(@Param("beforeMonthDateline") long beforeMonthDateline,
                                   @Param("goodsId") Long goodsId,
                                   @Param("memberId") Long memberId);

    /**
     * 根据订单编号查询货物相关信息
     * @param orderSn 订单编号
     * @return
     */
    List<Map> getItemsPromotionTypeandNum(String orderSn);
}
