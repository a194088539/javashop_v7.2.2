package com.enation.app.javashop.mapper.statistics;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.statistics.dto.GoodsData;
import org.apache.ibatis.annotations.CacheNamespace;


/**
 * 统计库商品数据Mapper
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/7/31
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GoodsDataMapper extends BaseMapper<GoodsData> {

}
