package com.enation.app.javashop.mapper.promotion.exchange;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.exchange.dos.ExchangeCat;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 积分兑换商品分类Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ExchangeCatMapper extends BaseMapper<ExchangeCat> {
}
