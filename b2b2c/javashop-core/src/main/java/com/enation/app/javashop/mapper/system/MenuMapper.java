package com.enation.app.javashop.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.system.dos.Menu;
import com.enation.app.javashop.model.system.vo.MenusVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 菜单管理店铺的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据id查询菜单
     * @param id 菜单管理主键
     * @return Map
     */
    List<Map> queryForId(@Param("id")Long id);

    /**
     * 获取菜单集合
     * @return MenusVO
     */
    List<MenusVO> queryForVo();
}
