package com.enation.app.javashop.mapper.trade.pintuan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.pintuan.PintuanOrder;
import com.enation.app.javashop.model.promotion.pintuan.PintuanOrderDetailVo;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * PintuanOrderMapper接口
 * @author fk
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface PintuanOrderMapper extends BaseMapper<PintuanOrder> {

    /**
     * 根据订单号查询主订单的相关信息
     * @param orderSn 订单编号
     * @return
     */
    PintuanOrderDetailVo getMainOrderBySn(@Param("order_sn") String orderSn);

}
