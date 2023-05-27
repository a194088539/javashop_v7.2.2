package com.enation.app.javashop.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kingapex
 * @version 1.0
 * @since 7.1.0
 * 2020/7/1
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface UserMapper extends BaseMapper<User> {
    List<User> customerSqlSegment(@Param("ew") Wrapper ew);
}
