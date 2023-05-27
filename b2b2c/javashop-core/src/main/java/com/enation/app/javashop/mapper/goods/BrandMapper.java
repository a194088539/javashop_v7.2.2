package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goods.dos.BrandDO;
import com.enation.app.javashop.model.goods.vo.SelectVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 品牌BrandDO的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface BrandMapper extends BaseMapper<BrandDO> {

    /**
     * 查询某分类下的品牌
     *
     * @param categoryId 分类id
     * @return
     */
    List<BrandDO> getBrandsByCategory(@Param("category_id") Long categoryId);

    /**
     * 查询分类品牌，所有品牌，分类绑定的品牌为已选中状态
     *
     * @param categoryId 分类id
     * @return
     */
    List<SelectVO> getCatBrand(@Param("category_id")Long categoryId);

}
