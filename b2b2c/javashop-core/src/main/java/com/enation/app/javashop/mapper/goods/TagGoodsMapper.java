package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goods.dos.TagGoodsDO;
import com.enation.app.javashop.model.goods.vo.GoodsSelectLine;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TagGoods的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface TagGoodsMapper extends BaseMapper<TagGoodsDO> {

    /**
     * 查询标签商品
     * @param tagId 标签id
     * @param num 数量
     * @return
     */
    List<GoodsSelectLine> queryTagGoods(@Param("tag_id") Long tagId, @Param("num") Integer num);

    /**
     * 查询标签商品分页
     * @param iPage 分页条件
     * @param tagId 标签id
     * @return
     */
    IPage queryTagGoodsPage(IPage iPage,@Param("tag_id") Long tagId);


}
