package com.enation.app.javashop.mapper.promotion.seckill;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillApplyDO;
import com.enation.app.javashop.model.promotion.seckill.vo.SeckillApplyVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 限时抢购申请mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface SeckillApplyMapper extends BaseMapper<SeckillApplyDO> {

    /**
     * 分页查询限时抢购申请列表
     * @param page 分页参数
     * @param queryWrapper 查询参数
     * @return
     */
    IPage<SeckillApplyVO> selectSeckillApplyVOPage(Page page, @Param(Constants.WRAPPER) QueryWrapper<SeckillApplyDO> queryWrapper);
}
