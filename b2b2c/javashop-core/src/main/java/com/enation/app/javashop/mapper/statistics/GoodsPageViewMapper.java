package com.enation.app.javashop.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.statistics.dos.GoodsPageView;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 商品访问量统计Mapper
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/31
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GoodsPageViewMapper extends BaseMapper<GoodsPageView> {

}
