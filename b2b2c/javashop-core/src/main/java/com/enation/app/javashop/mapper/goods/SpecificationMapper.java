package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goods.dos.SpecificationDO;
import com.enation.app.javashop.model.goods.vo.SelectVO;
import com.enation.app.javashop.model.goods.vo.SpecificationVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;

/**
 * SpecificationDO的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface SpecificationMapper extends BaseMapper<SpecificationDO> {

    /**
     * 查询分类关联的规格
     * @param categoryId 分类id
     * @return
     */
    List<SelectVO> getCatSpecification(@Param("category_id") Long categoryId);

    /**
     * 验证商家规格名称是否存在
     * @param map 条件
     * @return
     */
    Integer checkSellerSpecName(@Param("ew") Map map);


    /**
     * 查询分类绑定的商家规格
     * @param categoryId 分类id
     * @param sellerId 商家id
     * @return
     */
    List<SpecificationVO> queryCatSpec(@Param("category_id") Long categoryId,@Param("seller_id")Long sellerId);

}
