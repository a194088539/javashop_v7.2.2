package com.enation.app.javashop.mapper.trade.pintuan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.pintuan.Pintuan;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * PintuanMapper接口
 * @author fk
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface PintuanMapper extends BaseMapper<Pintuan> {
}
