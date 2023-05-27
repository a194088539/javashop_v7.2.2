package com.enation.app.javashop.mapper.distribution;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.distribution.dos.DistributionDO;
import com.enation.app.javashop.model.distribution.dos.DistributionOrderDO;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 分销商Manager的Mapper
 * @author zhanghao
 * @version v1.0
 * @since v7.2.2
 * 2020/8/5
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface DistributionMapper extends BaseMapper<DistributionDO> {

    /**
     * 整理解冻金额
     * @param currentData 解冻日期
     * @return
     */
    void updateCanRebate(@Param("currentData") Long currentData);

    /**
     * 统计下线人数
     * @param memberId  会员id
     * @return
     */
    void updateDownline(@Param("memberId") Long memberId);

}
