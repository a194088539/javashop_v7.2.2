package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goodssearch.GoodsWords;
import com.enation.app.javashop.model.goodssearch.GoodsWordsDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * GoodsWords的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GoodsWordsMapper extends BaseMapper<GoodsWordsDO> {


    /**
     * 查询商品分词的和对应数量
     * @param keyword 关键词
     * @return 分词和数量对应集合
     */
    List<GoodsWords> getGoodsWords(@Param("keyword")String keyword);


    /**
     * 增加分词的数量
     * @param words 分词
     */
    void addGoodsNum(@Param("words") String words);

    /**
     * 查询分词集合
     * @param type GoodsWordsType
     * @return
     */
    List<String> selectWordsList(@Param("type") String type);

    /**
     * 减少分词的数量
     * @param words 分词
     */
    void reduceGoodsNum(String words);
}
