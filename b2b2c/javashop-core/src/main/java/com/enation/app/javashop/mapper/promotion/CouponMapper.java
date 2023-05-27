package com.enation.app.javashop.mapper.promotion;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.promotion.coupon.dos.CouponDO;
import com.enation.app.javashop.model.promotion.coupon.vo.CouponVO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * CouponMapper的Mapper
 *
 * @author fk
 * @version 1.0
 * @since 7.1.0
 * 2020/7/21
 */
@CacheNamespace(implementation = MybatisRedisCache.class, eviction = MybatisRedisCache.class)
public interface CouponMapper extends BaseMapper<CouponDO> {

    /**
     * 查询优惠券列表
     *
     * @param page    分页
     * @param wrapper 条件
     * @return
     */
    IPage<CouponVO> selectCouponPage(Page page, @Param("ew") QueryWrapper<CouponDO> wrapper);
}
