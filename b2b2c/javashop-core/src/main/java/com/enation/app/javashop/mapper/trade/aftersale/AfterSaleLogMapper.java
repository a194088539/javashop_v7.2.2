package com.enation.app.javashop.mapper.trade.aftersale;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.aftersale.dos.AfterSaleLogDO;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 售后服务操作日志Mapper接口
 * @author duanmingyu
 * @version v1.0
 * @since v7.2.2
 * 2020-08-08
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface AfterSaleLogMapper extends BaseMapper<AfterSaleLogDO> {
}
