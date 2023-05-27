package com.enation.app.javashop.mapper.payment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.payment.dos.PaymentBillDO;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 支付帐单的Mapper
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/29
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface PaymentBillMapper extends BaseMapper<PaymentBillDO> {
}
