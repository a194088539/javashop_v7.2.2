package com.enation.app.javashop.mapper.promotion.fulldiscount;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.fulldiscount.dos.FullDiscountGiftDO;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 满减满赠促销活动赠品Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface FullDiscountGiftMapper extends BaseMapper<FullDiscountGiftDO> {
}
