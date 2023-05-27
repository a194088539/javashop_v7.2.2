package com.enation.app.javashop.mapper.distribution;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.distribution.dos.ShortUrlDO;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 短链接Manager的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/8/5
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ShortUrlMapper extends BaseMapper<ShortUrlDO> {
}
