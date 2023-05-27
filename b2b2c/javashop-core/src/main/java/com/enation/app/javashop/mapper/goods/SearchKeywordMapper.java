package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goodssearch.SearchKeywordDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * SearchKeyword的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface SearchKeywordMapper extends BaseMapper<SearchKeywordDO> {

    /**
     * 更新搜索分词数量
     * @param dateline 时间
     * @param keyword 分词
     */
    void updateKeywordCount(@Param("time") long dateline, @Param("keyword") String keyword);
}
