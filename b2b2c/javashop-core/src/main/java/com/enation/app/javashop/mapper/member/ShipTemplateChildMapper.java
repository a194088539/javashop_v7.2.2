package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.shop.dos.ShipTemplateChild;
import com.enation.app.javashop.model.shop.vo.ShipTemplateChildBuyerVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 运费模版详细配置Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-28
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ShipTemplateChildMapper extends BaseMapper<ShipTemplateChild> {

    /**
     * 查询运费模板的子模板数据集合
     * @param wrapper 查询条件构造器
     * @return
     */
    List<ShipTemplateChildBuyerVO> selectChildVo(@Param("ew") QueryWrapper<ShipTemplateChildBuyerVO> wrapper);
}
