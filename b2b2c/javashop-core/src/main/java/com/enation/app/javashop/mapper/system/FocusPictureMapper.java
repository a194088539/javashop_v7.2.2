package com.enation.app.javashop.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.pagedata.FocusPicture;
import org.apache.ibatis.annotations.CacheNamespace;


/**
 * 焦点图的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface FocusPictureMapper extends BaseMapper<FocusPicture> {
}
