package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goods.dos.DraftGoodsSkuDO;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * DraftGoodsSkuDO的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface DraftGoodsSkuMapper extends BaseMapper<DraftGoodsSkuDO> {
}