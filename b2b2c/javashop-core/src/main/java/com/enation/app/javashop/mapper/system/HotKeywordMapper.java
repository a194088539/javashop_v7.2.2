package com.enation.app.javashop.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.pagedata.HotKeyword;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 热门关键字的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface HotKeywordMapper extends BaseMapper<HotKeyword> {

    /**
     * 查询热门关键字
     * @param num 查询的数量
     * @return HotKeyword
     */
    List<HotKeyword> queryForLimit(@Param("num")Integer num);
}
