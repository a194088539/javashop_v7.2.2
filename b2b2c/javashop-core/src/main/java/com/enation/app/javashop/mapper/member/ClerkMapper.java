package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.shop.dos.ClerkDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 店铺店员Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-28
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface ClerkMapper extends BaseMapper<ClerkDO> {

    /**
     * 查询店员分页数据
     * @param page 分页信息
     * @param shopId 店铺ID
     * @param status 店员状态
     * @param keyword 查询关键字
     * @return
     */
    IPage<ClerkDO> selectClerkPage(Page page, @Param("shop_id") Long shopId, @Param("status") Integer status, @Param("keyword") String keyword);

}
