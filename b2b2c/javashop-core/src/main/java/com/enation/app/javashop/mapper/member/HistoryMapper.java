package com.enation.app.javashop.mapper.member;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.member.dos.HistoryDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 会员浏览足迹Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-07-24
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface HistoryMapper extends BaseMapper<HistoryDO> {

    /**
     * 根据天的日期分组，分页查询出日期
     * @param page 分页信息
     * @param queryWrapper 查询条件构造器
     * @return
     */
    IPage<Map<String, Object>> selectUpdateTimePage(Page page, @Param("ew") QueryWrapper queryWrapper);

    /**
     * 获取会员足迹ID集合
     * @param queryWrapper 查询条件构造器
     * @return
     */
    List<Long> selectHistoryId(@Param("ew") QueryWrapper queryWrapper);

}
