package com.enation.app.javashop.mapper.trade.complain;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.trade.complain.dos.OrderComplainCommunication;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * OrderComplainCommunicationMapper
 * @author fk
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface OrderComplainCommunicationMapper extends BaseMapper<OrderComplainCommunication> {
}
