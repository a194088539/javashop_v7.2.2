package com.enation.app.javashop.mapper.promotion.groupbuy;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.groupbuy.dos.GroupbuyActiveDO;
import com.enation.app.javashop.model.promotion.groupbuy.vo.GroupbuyActiveVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 团购活动Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GroupbuyActiveMapper extends BaseMapper<GroupbuyActiveDO> {

    /**
     * 获取团购信息分页列表集合
     * @param page 分页信息
     * @param wrapper 查询条件包装器
     * @return
     */
    IPage<GroupbuyActiveVO> selectPageVo(Page page, @Param("ew") QueryWrapper<GroupbuyActiveDO> wrapper);

}
