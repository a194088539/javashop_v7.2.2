package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goods.dos.GoodsParamsDO;
import com.enation.app.javashop.model.goods.vo.GoodsParamsVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * GoodsParams的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GoodsParamsMapper extends BaseMapper<GoodsParamsDO> {


    /**
     * 查询商品关联的参数及参数值
     * @param categoryId 分类id
     * @param goodsId 商品id
     * @return
     */
    List<GoodsParamsVO> queryGoodsParamsValue(@Param("category_id") Long categoryId, @Param("goods_id")Long goodsId);

    /**
     * 查询商品关联的可检索的参数值
     * @param ids id集合
     * @return
     */
    List<Map<String, Object>> getIndexGoodsList(@Param("params") List<Object> ids);
}
