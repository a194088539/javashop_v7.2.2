package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.shop.dos.ShopMenu;
import com.enation.app.javashop.model.shop.vo.ShopMenusVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商家端菜单Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-07
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ShopMenuMapper extends BaseMapper<ShopMenu> {

    /**
     * 获取店铺菜单信息集合
     * @param wrapper 查询条件包装器
     * @return
     */
    List<ShopMenusVO> selectShopMenusListVo(@Param("ew") QueryWrapper<ShopMenu> wrapper);
}
