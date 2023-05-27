package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.shop.dos.ShopCatDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 店铺分组Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-28
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ShopCatMapper extends BaseMapper<ShopCatDO> {

    /**
     * 获取当前分组所有的子分组(包括当前分组)
     * @param wrapper
     * @return
     */
    List<Map> getChildren(@Param("ew") QueryWrapper wrapper);

}
