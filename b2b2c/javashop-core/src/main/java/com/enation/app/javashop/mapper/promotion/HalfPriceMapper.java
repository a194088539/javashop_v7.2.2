package com.enation.app.javashop.mapper.promotion;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.halfprice.dos.HalfPriceDO;
import com.enation.app.javashop.model.promotion.halfprice.vo.HalfPriceVO;
import com.enation.app.javashop.model.system.dos.AdminUser;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 第二件半价mapper接口
 * @Author: gy
 * Date: Created in 2020/8/8 1:31 下午
 * Version: 0.0.1
 */
@CacheNamespace(implementation = MybatisRedisCache.class,eviction = MybatisRedisCache.class)
public interface HalfPriceMapper extends BaseMapper<HalfPriceDO> {

    /**
     * 分页查询第二件半价
     * @param page  分页第二件半价活动vo实体
     * @param wrapper 查询条件
     * @return
     */
    IPage<HalfPriceVO> selectPageVO(Page page, @Param("ew") QueryWrapper<HalfPriceVO> wrapper);
}
