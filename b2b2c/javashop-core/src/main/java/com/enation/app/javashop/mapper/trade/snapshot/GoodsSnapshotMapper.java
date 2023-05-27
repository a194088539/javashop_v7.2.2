package com.enation.app.javashop.mapper.trade.snapshot;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.trade.snapshot.GoodsSnapshot;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 交易快照mapper
 * @author zs
 * @version v1.0
 * @since v7.2.2
 * 2020-08-08
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface GoodsSnapshotMapper extends BaseMapper<GoodsSnapshot> {

    /**
     * 根据id修改point值
     * @param point 商品使用积分
     * @param snapshotId 交易快照id
     */
    void updatePointById(@Param("point") Integer point, @Param("snapshotId") Long snapshotId);
}
