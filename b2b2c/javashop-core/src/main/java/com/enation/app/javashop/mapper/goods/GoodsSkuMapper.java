package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goods.dos.GoodsSkuDO;
import com.enation.app.javashop.model.goods.vo.GoodsSkuVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * GoodsSku的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GoodsSkuMapper extends BaseMapper<GoodsSkuDO> {

    /**
     * 查询sku库存
     * @param skuId skuId
     * @return
     */
    Map queryQuantity(@Param("sku_id") Long skuId);

    /**
     * 更新sku库存
     * @param param 条件
     */
    void updateQuantity(@Param("param") Map param);

    /**
     * 查询sku的list集合
     * @param skuIdList skuid集合
     * @return
     */
    List<GoodsSkuVO> queryGoodsSkuVOList(@Param("params") List<Long> skuIdList);

    /**
     * 查询某商家的上架，审核通过的sku集合
     * @param sellerId 商家id
     * @return
     */
    List<GoodsSkuDO> querySellerAllSku(@Param("seller_id")Long sellerId);
}
