package com.enation.app.javashop.mapper.promotion.seckill;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillDO;
import com.enation.app.javashop.model.promotion.seckill.dos.SeckillRangeDO;
import com.enation.app.javashop.model.promotion.seckill.dto.SeckillQueryParam;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 限时抢购时刻mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-11
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface SeckillRangeMapper extends BaseMapper<SeckillRangeDO> {

    /**
     * 查询秒杀时刻列表
     * @param today 今天的时间
     * @return
     */
    List<SeckillRangeDO> selectReadTimeList(long today);
}
