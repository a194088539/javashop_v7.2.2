package com.enation.app.javashop.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.system.dos.AdminUser;
import com.enation.app.javashop.model.system.dto.AdminUserDTO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;


/**
 * 平台管理员的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/7/21
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface AdminUserMapper extends BaseMapper<AdminUser> {

    /**
     * 查询平台管理员列表
     * @param page 页码和每页数量
     * @param keyword 关键字
     * @return AdminUserDTO
     */
    IPage<AdminUserDTO> selectPageDto(Page<AdminUserDTO> page, @Param("keyword") String keyword);
}
