package com.enation.app.javashop.mapper.payment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.enation.app.javashop.framework.cache.MybatisRedisCache;
import com.enation.app.javashop.model.payment.dos.PaymentMethodDO;
import com.enation.app.javashop.model.trade.order.dos.PayLog;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Param;

/**
 * 支付方式的Mapper
 * @author zs
 * @version 1.0
 * @since 7.2.2
 * 2020/07/29
 */
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface PaymentMethodMapper extends BaseMapper<PaymentMethodDO> {

    /**
     * 根据插件id查询插件配置信息
     * @param clientType 客户端类型
     * @param pluginId 插件id
     * @return
     */
    String selectClientType(@Param("clientType") String clientType, @Param("pluginId") String pluginId);
}
