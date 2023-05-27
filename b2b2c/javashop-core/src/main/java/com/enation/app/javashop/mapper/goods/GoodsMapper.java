package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goods.dos.GoodsDO;
import com.enation.app.javashop.model.goods.vo.BackendGoodsVO;
import com.enation.app.javashop.model.goods.vo.BuyCountVO;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import com.enation.app.javashop.model.goods.vo.GoodsSelectorSkuVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * GoodsMapper的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GoodsMapper extends BaseMapper<GoodsDO> {

    /**
     * 获取新赠商品
     *
     * @param length 获取的查询数量
     * @return
     */
    List<BackendGoodsVO> newGoods(@Param("length") Integer length);

    /**
     * 更新商品库存
     * @param param 条件
     */
    void updateQuantity(@Param("param") Map param);

    /**
     * 根据条件查询多个商品信息
     * @param queryWrapper 查询条件
     * @return
     */
    List<GoodsSelectLine> queryGoodsLines(@Param("ew") QueryWrapper<GoodsDO> queryWrapper);

    /**
     * 查询购买数量
     * @param goodsIds 商品id集合
     * @return
     */
    List<BuyCountVO> queryBuyCount(@Param("params")List<Long> goodsIds);

    /**
     * 查询sku集合的信息
     * @param skuIds skuid集合
     * @return
     */
    List<GoodsSelectLine> querySkus(@Param("params")List<Long> skuIds);

    /**
     * 查询sku分页
     * @param iPage 分页参数
     * @param wrapper 条件
     * @return
     */
    IPage<GoodsSelectorSkuVO> querySkusPage(IPage iPage,@Param("ew")QueryWrapper<GoodsDO> wrapper);
}
