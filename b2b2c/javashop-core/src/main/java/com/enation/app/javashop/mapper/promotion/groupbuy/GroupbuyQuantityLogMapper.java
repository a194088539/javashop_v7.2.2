package com.enation.app.javashop.mapper.promotion.groupbuy;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyQuantityLog;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 团购商品库存日志Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-11
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GroupbuyQuantityLogMapper extends BaseMapper<GroupbuyQuantityLog> {
}
