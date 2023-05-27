package com.enation.app.javashop.mapper.trade.deposite;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.trade.deposite.DepositeLogDO;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * DepositeLogMapper
 * @author fk
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface DepositeLogMapper extends BaseMapper<DepositeLogDO> {
}
