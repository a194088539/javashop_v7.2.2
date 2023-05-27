package com.enation.app.javashop.mapper.trade.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.trade.order.dos.OrderLogDO;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 订单日志mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-07
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface OrderLogMapper extends BaseMapper<OrderLogDO> {

}
