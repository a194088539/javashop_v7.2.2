package com.enation.app.javashop.mapper.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.goods.dos.ParametersDO;
import com.enation.app.javashop.model.goods.vo.GoodsParamsVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Parameters的Mapper
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ParametersMapper extends BaseMapper<ParametersDO> {


    /**
     * 查询分类关联的参数
     * @param categoryId 分类id
     * @return
     */
    List<GoodsParamsVO> queryParams(@Param("category_id") Long categoryId);
}
