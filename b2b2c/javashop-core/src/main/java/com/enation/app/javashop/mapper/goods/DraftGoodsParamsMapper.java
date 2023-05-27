package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goods.dos.DraftGoodsParamsDO;
import com.enation.app.javashop.model.goods.vo.GoodsParamsVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DraftGoodsParams的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface DraftGoodsParamsMapper extends BaseMapper<DraftGoodsParamsDO> {

    /**
     * 查询草稿商品关联的参数及参数值
     * @param categoryId 分类id
     * @param draftGoodsId 草稿商品id
     * @return
     */
    List<GoodsParamsVO> queryDraftGoodsParamsValue(@Param("category_id") Long categoryId,  @Param("draft_goods_id")Long draftGoodsId);
}
