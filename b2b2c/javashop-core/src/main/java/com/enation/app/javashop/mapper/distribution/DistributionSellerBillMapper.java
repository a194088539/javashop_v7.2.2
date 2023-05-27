package com.enation.app.javashop.mapper.distribution;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.distribution.dos.DistributionSellerBillDO;
import com.enation.app.javashop.model.distribution.dto.DistributionSellerBillDTO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商家返现计算的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/8/5
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface DistributionSellerBillMapper extends BaseMapper<DistributionSellerBillDO> {

    /**
     * 商家返现统计
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return List
     */
    List<DistributionSellerBillDTO> queryCountSeller(@Param("startTime") Integer startTime, @Param("endTime") Integer endTime);
}
