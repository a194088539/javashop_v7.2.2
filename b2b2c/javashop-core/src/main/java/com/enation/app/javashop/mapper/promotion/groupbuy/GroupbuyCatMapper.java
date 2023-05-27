package com.enation.app.javashop.mapper.promotion.groupbuy;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyCatDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 团购分类Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GroupbuyCatMapper extends BaseMapper<GroupbuyCatDO> {

    /**
     * 查询团购分类已被团购活动或者团购商品占用的数量
     * @param catId 团购分类ID
     * @param time 当前时间
     * @return
     */
    Integer selectCatCount(@Param("cat_id") Long catId, @Param("time") Long time);
}
