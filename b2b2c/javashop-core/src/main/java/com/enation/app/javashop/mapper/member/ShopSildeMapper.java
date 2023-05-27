package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.shop.dos.ShopSildeDO;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 店铺幻灯片Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-07
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ShopSildeMapper extends BaseMapper<ShopSildeDO> {
}
