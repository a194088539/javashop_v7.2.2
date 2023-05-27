package com.enation.app.javashop.mapper.promotion.seckill;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillDO;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillQueryParam;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 限时抢购入库mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-10
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface SeckillMapper extends BaseMapper<SeckillDO> {

    /**
     * 根据参数分页查询限时抢购入库
     * @param page 分页参数
     * @param param 查询参数
     * @param toDayStartTime 当天的开始时间
     * @param toDayEndTime 当天的结束时间
     * @return
     */
    IPage<SeckillDO> selectCustomPage(Page page,
                                      @Param("param") SeckillQueryParam param,
                                      @Param("toDayStartTime") long toDayStartTime,
                                      @Param("toDayEndTime") long toDayEndTime);
}
